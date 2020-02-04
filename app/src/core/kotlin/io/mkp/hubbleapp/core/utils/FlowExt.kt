package io.mkp.hubbleapp.core.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlin.math.pow

@ExperimentalCoroutinesApi
fun <T, U> Flow<T>.flatMapSequential(
    concurrency: Int = DEFAULT_CONCURRENCY,
    prefetch: Int = 8,
    block: suspend (T) -> Flow<U>
): Flow<U> = channelFlow {
    val channel = Channel<Channel<U>>(concurrency)

    launch {
        collect {
            val subFlow = block(it)
            val subChannel = Channel<U>(prefetch)
            channel.send(subChannel)
            launch {
                subFlow.collect {
                    subChannel.send(it)
                }
                subChannel.close()
            }
        }

        channel.close()
    }

    channel.consumeEach { subChannel ->
        subChannel.consumeEach {
            send(it)
        }
    }
}


@ExperimentalCoroutinesApi
fun <T, U> Flow<T>.mapConcurrent(
    concurrency: Int = DEFAULT_CONCURRENCY,
    block: suspend (T) -> U
): Flow<U> = flatMapSequential(concurrency = concurrency, prefetch = 1) { t ->
    suspend { block(t) }.asFlow()
}


@ExperimentalCoroutinesApi
suspend fun <T, U> Iterable<T>.mapConcurrent(
    concurrency: Int = DEFAULT_CONCURRENCY,
    block: suspend (T) -> U
): List<U> = coroutineScope {
    val semaphore = Semaphore(concurrency)
    val deferreds = map { t ->
        async {
            semaphore.acquire()
            block(t).also {
                semaphore.release()
            }
        }
    }
    deferreds.map { it.await() }
}

fun <T> Flow<T>.retryBackoff(firstBackoff: Long, maxBackoff: Long) = retryWhen { _, attempt ->
    val timeMillis = firstBackoff * (2.0.pow(attempt.toDouble()) - 1.0)
    delay(minOf(timeMillis.toLong(), maxBackoff))
    true
}

fun <T> Flow<T>.log(opName: String) = onStart {
    println("Loading $opName")
}.onEach {
    println("Loaded $opName : $it")
}.onCompletion { maybeErr ->
    maybeErr?.let {
        println("Error $opName: $it")
    } ?: println("Completed $opName")
}