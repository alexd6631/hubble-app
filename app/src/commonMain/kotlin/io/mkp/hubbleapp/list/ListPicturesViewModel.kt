package io.mkp.hubbleapp.list

import io.mkp.hubbleapp.BaseViewModel
import io.mkp.hubbleapp.core.usecases.ListPicturesUseCase
import io.monkeypatch.konfetti.mvvm.livedata.KMutableLiveData
import kotlinx.coroutines.CoroutineDispatcher

class ListPicturesViewModel(
    useCase: ListPicturesUseCase,
    dispatcher: CoroutineDispatcher
): BaseViewModel(dispatcher) {
    val filter = KMutableLiveData<String>().apply { value = "" }

    val pictures = useCase.listPictures(filter.asFlow()).asLiveData()

    override fun onDestroy() {}

    override val liveDataList = listOf(filter, pictures)
}