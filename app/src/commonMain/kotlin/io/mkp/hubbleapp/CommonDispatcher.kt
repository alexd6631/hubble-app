package io.mkp.hubbleapp

import kotlinx.coroutines.CoroutineDispatcher

expect object CommonDispatcher {
    val ui: CoroutineDispatcher
}