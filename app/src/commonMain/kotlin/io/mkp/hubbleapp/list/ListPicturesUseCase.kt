package io.mkp.hubbleapp.list

import io.mkp.hubbleapp.HubblePicture
import io.mkp.hubbleapp.utils.log
import io.mkp.hubbleapp.utils.retryBackoff
import kotlinx.coroutines.flow.*

interface PicturesRepository {
    suspend fun getHubblePictures(): List<HubblePicture>
}

class ListPicturesUseCase(
    private val repository: PicturesRepository
) {
    fun listPictures(filter: Flow<String>): Flow<List<HubblePicture>> =
        combine(fetchPictures(), filter) { pics, f ->
            filterPictures(pics, f)
        }

    private fun fetchPictures(): Flow<List<HubblePicture>> =
        repository::getHubblePictures.asFlow()
            .log("picture list")
            .retryBackoff(100, 5000)
}

private fun filterPictures(
    pictures: List<HubblePicture>,
    filter: String
) = pictures.filter { it.name.contains(filter, ignoreCase = true) }
