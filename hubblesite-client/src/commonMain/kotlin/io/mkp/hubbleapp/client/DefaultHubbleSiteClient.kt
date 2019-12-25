package io.mkp.hubbleapp.client

import io.ktor.client.HttpClient

//expect fun defaultHubbleSiteClient(): HttpClient

fun defaultHubbleSiteClient() = HubbleClient(HttpClient())