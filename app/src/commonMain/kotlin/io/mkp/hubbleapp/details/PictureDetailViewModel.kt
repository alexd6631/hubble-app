package io.mkp.hubbleapp.details

import io.mkp.hubbleapp.BaseViewModel
import io.mkp.hubbleapp.core.repositories.PictureDetailRepository
import kotlinx.coroutines.CoroutineDispatcher

class PictureDetailViewModel(
    id: String,
    uiDispatcher: CoroutineDispatcher,
    repository: PictureDetailRepository
) : BaseViewModel(uiDispatcher) {
    val detail = repository.getPictureDetail(id).asLiveData()

    override fun onDestroy() {}

    override val liveDataList = listOf(detail)
}