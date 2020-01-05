package io.mkp.hubbleapp.details

import io.mkp.hubbleapp.HubblePictureDetail
import io.mkp.hubbleapp.client.HubbleClient

class PictureDetailRepositoryImpl(
    private val hubbleClient: HubbleClient
) : PictureDetailRepository {
    override suspend fun getPictureDetail(id: String): HubblePictureDetail {
        val details = hubbleClient.getImageDetails(id)
        val highRes = details.imageFiles.minBy { it.fileSize }

        return HubblePictureDetail(
            details.description,
            highRes?.fileUrl?.prefixHttps()
        )
    }
}

private fun String.prefixHttps() = "https:$this"