package io.monkeypatch.konfetti.mvvm.livedata

import androidx.lifecycle.*

private class MediatorLiveDataWrapper<T>(
    val activeListener: (Boolean) -> Unit
) : MediatorLiveData<T>() {

    override fun onActive() {
        super.onActive()
        activeListener(true)
    }

    override fun onInactive() {
        super.onInactive()
        activeListener(false)
    }
}

actual open class KLiveData<T> {
    internal val liveData: MediatorLiveData<T> = MediatorLiveDataWrapper {
        if (it) onActive() else onInactive()
    }

    actual open val value: T?
        get() = liveData.value

    actual fun observeForever(block: (T) -> Unit): Disposable {
        val observer = Observer<T>(block)
        liveData.observeForever(observer)
        return disposable { liveData.removeObserver(observer) }
    }

    actual fun hasObservers(): Boolean = liveData.hasObservers()

    actual fun hasActiveObservers(): Boolean = liveData.hasActiveObservers()

    actual fun observe(lifecycle: KLifecycle, block: (T) -> Unit): Disposable {
        val observer = Observer<T>(block)
        liveData.observe(lifecycle.lifecycleOwner, observer)
        return disposable { liveData.removeObserver(observer) }
    }

    protected actual open fun onActive() {}

    protected actual open fun onInactive() {}
}

actual open class KMutableLiveData<T> : KLiveData<T>() {
    actual override var value: T?
        get() = liveData.value
        set(value) {
            liveData.postValue(value)
        }
}

actual open class KMediatorLiveData<T> : KMutableLiveData<T>() {
    actual fun <S> addSource(other: KLiveData<S>, block: ((S) -> Unit)) {
        liveData.addSource(other.liveData) {
            block(it)
        }
    }

    actual fun removeSource(other: KLiveData<*>) {
        liveData.removeSource(other.liveData)
    }
}

actual data class KLifecycle(val lifecycleOwner: LifecycleOwner)

fun LifecycleOwner.kLifecycle(): KLifecycle = KLifecycle(this)

val <T> KLiveData<T>.toLivedata: LiveData<T>
    get() = this.liveData

val <T> KMutableLiveData<T>.toMutableLiveData: MutableLiveData<T>
    get() = this.liveData

val <T> KMediatorLiveData<T>.toMediatorLivedata: MediatorLiveData<T>
    get() = this.liveData