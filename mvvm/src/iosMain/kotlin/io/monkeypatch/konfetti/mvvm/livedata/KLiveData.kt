package io.monkeypatch.konfetti.mvvm.livedata

actual open class KLiveData<T> {
    internal var _value: T? = null
        set(value) {
            field = value
            version += 1
            observers.forEach(::considerNotify)
        }

    actual open val value: T?
        get() = _value

    private val observers = mutableListOf<ObserverWrapper>()

    private var activeCount = 0

    var version = -1
        private set

    actual fun hasObservers(): Boolean = observers.size > 0

    actual fun hasActiveObservers() = activeCount > 0

    actual fun observe(lifecycle: KLifecycle, block: (T) -> Unit): Disposable {
        if (lifecycle.state == BaseLifecycle.State.DESTROYED) {
            return disposable {  }
        }

        val wrapper = LifecycleBoundObserver(lifecycle, block)
        observers.add(wrapper)
        lifecycle.addObserver(wrapper)

        return disposable { removeObserver(wrapper) }
    }

    protected actual open fun onActive() {}

    protected actual open fun onInactive() {}

    actual fun observeForever(block: (T) -> Unit): Disposable {
        val wrapper = AlwaysActiveObserver(block)
        observers.add(wrapper)
        wrapper.activeStateChanged(true)
        return disposable { removeObserver(wrapper) }
    }


    private fun removeObserver(wrapper: ObserverWrapper) {
        observers.remove(wrapper)
        wrapper.detachObserver()
        wrapper.activeStateChanged(false)
    }

    private fun considerNotify(w: ObserverWrapper) {
        _value?.takeIf { version > w.version }?.let {
            w.version = version
            w.observer(it)
        }
    }

    private abstract inner class ObserverWrapper(
        val observer: (T) -> Unit
    ) {
        var active = false

        var version = -1

        abstract fun shouldBeActive(): Boolean

        open fun detachObserver() {}

        open fun activeStateChanged(newActive: Boolean) {
            if (newActive == active) { return }
            active = newActive
            val wasInactive = activeCount == 0
            activeCount += if (newActive) 1 else -1
            if (wasInactive && newActive) {
                onActive()
            }
            if (activeCount == 0 && !newActive) {
                onInactive()
            }
            if (newActive) {
                considerNotify(this)
            }
        }
    }

    private inner class AlwaysActiveObserver(observer: (T) -> Unit) : ObserverWrapper(observer) {
        override fun shouldBeActive(): Boolean = true
    }

    private inner class LifecycleBoundObserver(
        val lifecycle: KLifecycle,
        observer: (T) -> Unit
    ) : ObserverWrapper(observer), StateChangedListener {
        override fun shouldBeActive(): Boolean =
            lifecycle.state.isAtLeast(BaseLifecycle.State.STARTED)

        override fun onStateChanged(state: BaseLifecycle.State) {
            if (lifecycle.state == BaseLifecycle.State.DESTROYED) {
                removeObserver(this)
            } else {
                activeStateChanged(shouldBeActive())
            }
        }

        override fun detachObserver() {
            lifecycle.removeObserver(this)
        }
    }
}

actual open class KMutableLiveData<T> : KLiveData<T>() {
    actual override var value: T?
        get() = _value
        set(value) {
            _value = value
        }
}

actual open class KMediatorLiveData<T> : KMutableLiveData<T>() {
    private val sources = mutableMapOf<KLiveData<*>, Source<*>>()

    actual fun <S> addSource(other: KLiveData<S>, block: ((S) -> Unit)) {
        val source = Source(other, block)
        sources[other] = source
        if (hasActiveObservers()) {
            source.plug()
        }
    }

    actual fun removeSource(other: KLiveData<*>) {
        sources.remove(other)?.unplug()
    }

    override fun onActive() {
        sources.forEach { (_, s) -> s.plug() }
    }

    override fun onInactive() {
        sources.forEach { (_, s) -> s.unplug() }
    }

    private inner class Source<V>(
        val liveData: KLiveData<V>,
        val block: ((V) -> Unit)
    ) {
        private var version = -1
        private var disposable: Disposable? = null
        fun plug() {
            disposable = liveData.observeForever {
                if (version != liveData.version) {
                    version = liveData.version
                    block(it)
                }
            }
        }

        fun unplug() {
            disposable?.dispose()
        }
    }
}