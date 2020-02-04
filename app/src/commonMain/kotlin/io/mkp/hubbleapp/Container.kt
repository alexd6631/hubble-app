package io.mkp.hubbleapp

import io.mkp.hubbleapp.client.defaultHubbleSiteClient
import io.mkp.hubbleapp.core.usecases.ListPicturesUseCase
import io.mkp.hubbleapp.details.PictureDetailViewModel
import io.mkp.hubbleapp.list.ListPicturesViewModel
import io.mkp.hubbleapp.repositories.PicturesRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Container {
    private val uiDispatcher: CoroutineDispatcher = CommonDispatcher.ui

    private val picturesRepositoryImpl: PicturesRepositoryImpl by lazy {
        PicturesRepositoryImpl(defaultHubbleSiteClient())
    }

    val listPicturesUseCase by lazy {
        ListPicturesUseCase(picturesRepositoryImpl)
    }

    fun listPicturesViewModel() =
        ListPicturesViewModel(listPicturesUseCase, uiDispatcher)

    val viewModelCache = mutableMapOf<String, PictureDetailViewModel>()

    fun pictureDetailViewModel(id: String) =
        viewModelCache.getOrPut(id) {
            PictureDetailViewModel(id, uiDispatcher, picturesRepositoryImpl).also {
                println("Creating detailViewModel $id")
            }
        }
}