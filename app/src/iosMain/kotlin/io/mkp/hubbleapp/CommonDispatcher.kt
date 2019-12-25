package io.mkp.hubbleapp

import kotlinx.coroutines.*
import platform.Foundation.NSThread
import platform.darwin.*
import kotlin.coroutines.CoroutineContext

@UseExperimental(InternalCoroutinesApi::class)
actual object CommonDispatcher {
    actual val ui: CoroutineDispatcher = object : CoroutineDispatcher(), Delay {

        override fun isDispatchNeeded(context: CoroutineContext): Boolean = !NSThread.isMainThread

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            dispatch_async(dispatch_get_main_queue()) {
                block.run()
            }
        }

        //TODO fully implement https://github.com/Kotlin/kotlinx.coroutines/issues/470 ?
        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatch_get_main_queue()) {
                with(continuation) {
                    resumeUndispatched(Unit)
                }
            }
        }

        @InternalCoroutinesApi
        override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
            val handle = object : DisposableHandle {
                var disposed = false
                    private set

                override fun dispose() {
                    disposed = true
                }
            }
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatch_get_main_queue()) {
                try {
                    if (!handle.disposed) {
                        block.run()
                    }
                } catch (err: Throwable) {
                    throw err
                }
            }

            return handle
        }
    }
}