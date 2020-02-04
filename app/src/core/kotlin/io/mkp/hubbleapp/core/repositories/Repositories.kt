package io.mkp.hubbleapp.core.repositories

import io.mkp.hubbleapp.core.models.HubblePicture
import io.mkp.hubbleapp.core.models.HubblePictureDetail
import kotlinx.coroutines.flow.Flow

interface PicturesRepository {
    fun getHubblePictures(): Flow<List<HubblePicture>>
}

interface PictureDetailRepository {
    fun getPictureDetail(id: String): Flow<HubblePictureDetail>
}