package io.mkp.hubbleapp.repositories

import io.mkp.hubbleapp.client.HubbleClient
import io.mkp.hubbleapp.client.ImageDetails
import io.mkp.hubbleapp.client.ImageMeta
import io.mkp.hubbleapp.core.models.HubblePicture
import io.mkp.hubbleapp.core.models.HubblePictureDetail
import io.mkp.hubbleapp.core.repositories.PictureDetailRepository
import io.mkp.hubbleapp.core.repositories.PicturesRepository
import io.mkp.hubbleapp.core.utils.log
import io.mkp.hubbleapp.core.utils.mapConcurrent
import io.mkp.hubbleapp.core.utils.retryBackoff
import kotlinx.coroutines.flow.asFlow

private const val nPages = 15

class PicturesRepositoryImpl(
    private val hubbleClient: HubbleClient
) : PicturesRepository, PictureDetailRepository {
    override fun getHubblePictures() =
        this::fetchPictures.asFlow()
            .log("Fetching pics list")
            .retryBackoff(100, 5000)

    override fun getPictureDetail(id: String) =
        suspend { fetchPictureDetails(id) }.asFlow()
            .log("Fetching pic detail $id")
            .retryBackoff(100, 5000)

    private suspend fun fetchPictures(): List<HubblePicture> =
        (1..nPages).mapConcurrent {
            hubbleClient.getImageMetadata(it)
        }.flatten().map(ImageMeta::toHubblePicture)

    private suspend fun fetchPictureDetails(id: String): HubblePictureDetail =
        hubbleClient.getImageDetails(id).toHubblePictureDetail()
}

private fun ImageMeta.toHubblePicture() =
    HubblePicture(id.toString(), name, mission)

private fun ImageDetails.toHubblePictureDetail(): HubblePictureDetail {
    val firstPicture = imageFiles.minBy { it.fileSize }

    return HubblePictureDetail(
        description,
        firstPicture?.fileUrl?.prefixHttps()
    )
}

private fun String.prefixHttps() = "https:$this"