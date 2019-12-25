package io.mkp.hubbleapp.details

import kotlinx.coroutines.CoroutineDispatcher
import sample.BaseViewModel


class PictureDetailViewModel(
    id: String,
    uiDispatcher: CoroutineDispatcher,
    useCase: PictureDetailUseCase
) : BaseViewModel(uiDispatcher) {
    val detail = useCase.getPictureDetail(id).asLiveData()

    override fun onDestroy() {}
}