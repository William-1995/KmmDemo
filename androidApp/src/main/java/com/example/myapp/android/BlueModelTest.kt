import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

const val queueSize = 5
val mutex = Mutex()
val duplicatedSet = mutableSetOf<Int>()

fun CoroutineScope.produceNumbers(num:Int) = produce<Int>(capacity = queueSize) {
    send(num)
}

fun CoroutineScope.consume(waiting: MutableList<Int>, uploading: MutableList<Int>, uploaded: MutableList<Int>): SendChannel<Int> = actor(capacity = queueSize) {
    for (index in channel) {
        launch(Dispatchers.IO) {
            mutex.withLock {
                waiting.remove(index)
                if (waiting.size > queueSize) {
                    if (uploading.size < queueSize) {
                        uploading.add(index)
                    } else if (uploading.size == queueSize) {
                        uploading.removeFirst()
                        uploading.add(index)
                    }
                    if (duplicatedSet.add(index)) {
                        val job = launch {
                            delay(Random.nextInt(100, 200).toLong())
                        }
                        job.cancelAndJoin()
                        uploaded.add(index)
                        println("Recieve1 the number: $index, waiting size: ${waiting.size}, uploading size: ${uploading.size}, uploaded size: ${uploaded.size}")
                    }
                } else {
                    uploading.clear()
                    if (duplicatedSet.add(index)) {
                        if (waiting.isNotEmpty()) {
                            uploading.add(index)
                        }
                        val job = launch {
                            delay(Random.nextInt(100, 200).toLong())
                        }
                        job.cancelAndJoin()
                        uploaded.add(index)
                        println("Recieve2 the number: $index, waiting size: ${waiting.size}, uploading size: ${uploading.size}, uploaded size: ${uploaded.size}")
                    }
                }
            }
        }
    }
}

fun main() = runBlocking {
    val waiting = mutableListOf<Int>()
    val updating = mutableListOf<Int>()
    val uploaded = mutableListOf<Int>()
    val flow = MutableSharedFlow<Int>()

    launch {
        delay(1000)
        for (item in 0 .. 50) {
            waiting.add(item)
            flow.emit(item)
        }
    }

    launch {
        delay(1000)
        for (item in 51 .. 99) {
            waiting.add(item)
            flow.emit(item)
        }
    }

    flow.collect { it ->
        if (!waiting.contains(it)) {
            waiting.add(it)
        }
        produceNumbers(it).consumeEach {
            consume(waiting, updating, uploaded).send(it)
        }
    }
    println("===")
}