package io.mkp.hubbleapp

data class HubblePicture(
    val id: String,
    val name: String,
    val mission: String
)

data class HubblePictureDetail(
    val pictureDescription: String?,
    val imageUrl: String?
)
