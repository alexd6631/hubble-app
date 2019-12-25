package io.mkp.hubbleapp

import io.mkp.hubbleapp.client.defaultHubbleSiteClient
import io.mkp.hubbleapp.details.PictureDetailRepository
import io.mkp.hubbleapp.details.PictureDetailRepositoryImpl
import io.mkp.hubbleapp.details.PictureDetailUseCase
import io.mkp.hubbleapp.details.PictureDetailViewModel
import io.mkp.hubbleapp.list.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Container {
    private val uiDispatcher: CoroutineDispatcher = CommonDispatcher.ui

    val PICTURES_REPOSITORY: PicturesRepository by lazy {
        PicturesRepositoryImpl(defaultHubbleSiteClient())
    }

    private fun hubblePictureRepositoryMock(): PicturesRepository {
        return object : PicturesRepository {
            override suspend fun getHubblePictures(): List<HubblePicture> {
                delay(100)
                return listOf(
                    HubblePicture(
                        "1", "Picture 1", "Hubble"
                    ),
                    HubblePicture(
                        "2", "Picture 2", "James Webb"
                    )
                )
            }
        }
    }

    val listPicturesUseCase by lazy {
        ListPicturesUseCase(PICTURES_REPOSITORY)
    }

    fun listPicturesViewModel() =
        ListPicturesViewModel(listPicturesUseCase, uiDispatcher)


    fun mockListPicturesViewModel() =
        ListPicturesViewModel(
            ListPicturesUseCase(hubblePictureRepositoryMock()),
            uiDispatcher
        )

    val pictureDetailRepository: PictureDetailRepository by lazy {
        PictureDetailRepositoryImpl(defaultHubbleSiteClient())
    }

    val pictureDetailUseCase by lazy {
        PictureDetailUseCase(pictureDetailRepository)
    }

    val viewModelCache = mutableMapOf<String, PictureDetailViewModel>()

    fun pictureDetailViewModel(id: String) =
        viewModelCache.getOrPut(id) {
            PictureDetailViewModel(id, uiDispatcher, pictureDetailUseCase).also {
                println("Creating detailViewModel $id")
            }
        }
}