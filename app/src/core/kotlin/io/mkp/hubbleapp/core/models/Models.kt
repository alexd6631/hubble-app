package io.mkp.hubbleapp.core.models

data class HubblePicture(
    val id: String,
    val name: String,
    val mission: String
)

data class HubblePictureDetail(
    val pictureDescription: String?,
    val imageUrl: String?
)
