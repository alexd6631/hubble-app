package io.mkp.hubbleapp.details

import io.mkp.hubbleapp.utils.log
import io.mkp.hubbleapp.utils.retryBackoff
import kotlinx.coroutines.flow.*

data class HubblePictureDetail(
    val pictureDescription: String,
    val imageUrl: String?
)

interface PictureDetailRepository {
    suspend fun getPictureDetail(id: String): HubblePictureDetail
}

class PictureDetailUseCase(
    private val repository: PictureDetailRepository
) {
    fun getPictureDetail(id: String): Flow<HubblePictureDetail> =
        suspend { repository.getPictureDetail(id) }
            .asFlow()
            .log("detail view $id")
            .retryBackoff(100, 5000)
}