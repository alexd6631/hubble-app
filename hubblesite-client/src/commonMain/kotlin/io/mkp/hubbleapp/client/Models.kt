package io.mkp.hubbleapp.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageMeta(
    val id: Int,
    val name: String,
    val collection: String,
    val mission: String
)


@Serializable
data class ImageDetails(
    val name: String,
    val description: String? = null,
    @SerialName("image_files")
    val imageFiles: List<ImageFile> = emptyList()
)

@Serializable
data class ImageFile(
    @SerialName("file_url")
    val fileUrl: String,
    val width: Int? = null,
    val height: Int? = null,
    @SerialName("file_size")
    val fileSize: Long
)
