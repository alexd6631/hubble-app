package io.mkp.hubbleapp.list

import io.mkp.hubbleapp.models.HubblePicture
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals


internal class ListPicturesUseCaseTest {

    @Test
    fun testListPicturesUseCase() = runBlockingTest {
        val useCase = ListPicturesUseCase(MockPicturesRepository())

        val inputFlow = flow {
            emit("")
            delay(1000)
            emit("nebula")
            delay(1000)
            emit("exoplanet")
        }

        val timeline = useCase.listPictures(inputFlow)
            .map { pics -> currentTime to pics.map { it.id } }
            .toList()

        assertEquals(
            listOf(
                500L to listOf("1", "2", "3"),
                1000L to listOf("1", "2"),
                2000L to listOf("3")
            ),
            timeline
        )
    }
}

class MockPicturesRepository : PicturesRepository {
    override suspend fun getHubblePictures(): List<HubblePicture> {
        delay(500)
        return listOf(
            HubblePicture("1", "Crab Nebula", "hubble"),
            HubblePicture("2", "Orion Nebula", "hubble"),
            HubblePicture("3", "Illustration of exoplanet", "hubble")
        )
    }
}