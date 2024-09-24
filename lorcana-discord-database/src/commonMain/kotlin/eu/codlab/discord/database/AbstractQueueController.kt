package eu.codlab.discord.database

import eu.codlab.discord.database.utils.Queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class AbstractQueueController {
    private val queue = Queue()

    @Suppress("TooGenericExceptionCaught")
    protected suspend fun <T> post(block: () -> T): T =
        suspendCoroutine { continuation ->
            queue.post {
                try {
                    val result = block()
                    continuation.resume(result)
                } catch (exception: Throwable) {
                    continuation.resumeWithException(exception)
                }
            }
        }
}
