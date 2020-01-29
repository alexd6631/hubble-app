package io.mkp.hubbleapp.list

import io.mkp.hubbleapp.models.HubblePicture
import io.mkp.hubbleapp.client.HubbleClient
import io.mkp.hubbleapp.utils.mapConcurrent

private const val nPages = 15

class PicturesRepositoryImpl(
    private val hubbleClient: HubbleClient
): PicturesRepository {
    override suspend fun getHubblePictures(): List<HubblePicture> {
        return (1 .. nPages).mapConcurrent {
            hubbleClient.getImageMetadata(it)
        }.flatten().map {
            HubblePicture(it.id.toString(), it.name, it.mission)
        }
    }
}