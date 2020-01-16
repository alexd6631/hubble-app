package io.mkp.hubbleapp.list

import io.monkeypatch.konfetti.mvvm.livedata.KMutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import io.mkp.hubbleapp.BaseViewModel
import io.mkp.hubbleapp.loading
import io.monkeypatch.konfetti.mvvm.livedata.KLiveData

class ListPicturesViewModel(
    useCase: ListPicturesUseCase,
    dispatcher: CoroutineDispatcher
): BaseViewModel(dispatcher) {
    val filter = KMutableLiveData<String>().apply { value = "" }

    val pictures = useCase.listPictures(filter.asFlow()).asLiveData()

    val loading = pictures.loading()

    override fun onDestroy() {}

    override val liveDataList = listOf(filter, pictures, loading)
}