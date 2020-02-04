package io.mkp.hubbleapp.core.usecases

import io.mkp.hubbleapp.core.models.HubblePicture
import io.mkp.hubbleapp.core.repositories.PicturesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ListPicturesUseCase(
    private val repository: PicturesRepository
) {
    fun listPictures(filter: Flow<String>): Flow<List<HubblePicture>> =
        combine(repository.getHubblePictures(), filter) { pics, f ->
            filterPictures(pics, f)
        }

}

private fun filterPictures(
    pictures: List<HubblePicture>,
    filter: String
) = pictures.filter { it.name.contains(filter, ignoreCase = true) }

