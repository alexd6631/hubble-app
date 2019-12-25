package io.mkp.hubbleapp.client

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

private const val host = "hubblesite.org"

class HubbleClient(private val client: HttpClient) {
    suspend fun getImageMetadata(page: Int): List<ImageMeta> {

        val url = URLBuilder(host = host).path("api", "v3", "images", "all").apply {
            parameters.append("page", page.toString())
        }.build()

        return client.get<String>(url)
            .let { Json.nonstrict.parse(ImageMeta.serializer().list, it) }
    }

    suspend fun getImageDetails(id: String): ImageDetails {
        val url = URLBuilder(host = host).path("api", "v3", "image", id).build()

        return client.get<String>(url)
            .let { Json.nonstrict.parse(ImageDetails.serializer(), it) }
    }
}


