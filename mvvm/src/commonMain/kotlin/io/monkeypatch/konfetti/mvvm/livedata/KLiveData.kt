package io.monkeypatch.konfetti.mvvm.livedata

expect class KLifecycle

expect open class KLiveData<T>() {
    open val value : T?

    fun hasObservers() : Boolean
    fun hasActiveObservers(): Boolean

    fun observe(lifecycle: KLifecycle, block: (T) -> Unit): Disposable
    fun observeForever(block: (T) -> Unit): Disposable

    protected open fun onActive()

    protected open fun onInactive()
}

expect open class KMutableLiveData<T>() : KLiveData<T> {
    override var value : T?
}

expect class KMediatorLiveData<T>() : KMutableLiveData<T> {
    fun <S> addSource(other: KLiveData<S>, block: ((S) -> Unit))
    fun removeSource(other: KLiveData<*>)
}