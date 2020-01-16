package io.mkp.hubbleapp

import io.monkeypatch.konfetti.mvvm.KViewModel
import io.monkeypatch.konfetti.mvvm.livedata.KLiveData
import io.monkeypatch.konfetti.mvvm.livedata.KMediatorLiveData
import io.monkeypatch.konfetti.mvvm.livedata.KMutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@UseExperimental(ExperimentalCoroutinesApi::class)
abstract class BaseViewModel(
    protected val uiDispatcher: CoroutineDispatcher
) : KViewModel(), CoroutineScope by CoroutineScope(SupervisorJob() + uiDispatcher) {

    final override fun onDestroyed() {
        cancel()
        onDestroy()
    }

    protected abstract fun onDestroy()

    fun <T> Flow<T>.asLiveData(): KLiveData<T> {
        var job: Job? = null
        return object : KMutableLiveData<T>() {
            override fun onActive() {
                job = launch(uiDispatcher) {
                    collect { value = it }
                }
            }

            override fun onInactive() {
                job?.cancel()
                job = null
            }
        }
    }

    fun <T> KLiveData<T>.asFlow(): Flow<T> = callbackFlow {
        val d = observeForever { offer(it) }
        awaitClose { d.dispose() }
    }

    abstract val liveDataList: List<KLiveData<*>>
}

fun <T> KLiveData<T>.loading(): KLiveData<Boolean> = KMediatorLiveData<Boolean>().apply {
    value = true
    addSource(this@loading) {
        value = false
        removeSource(this@loading)
    }
}