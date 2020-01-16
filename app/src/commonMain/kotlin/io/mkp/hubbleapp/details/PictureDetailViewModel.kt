package io.mkp.hubbleapp.details

import kotlinx.coroutines.CoroutineDispatcher
import io.mkp.hubbleapp.BaseViewModel
import io.monkeypatch.konfetti.mvvm.livedata.KLiveData


class PictureDetailViewModel(
    id: String,
    uiDispatcher: CoroutineDispatcher,
    useCase: PictureDetailUseCase
) : BaseViewModel(uiDispatcher) {
    val detail = useCase.getPictureDetail(id).asLiveData()

    override fun onDestroy() {}

    override val liveDataList = listOf(detail)
}