package com.example.myapp.android

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

val mutex = Mutex()
const val queueSize = 5

fun main() = runBlocking {
    val waiting = mutableListOf<Int>()
    val uploading = mutableListOf<Int>()
    val uploaded = mutableListOf<Int>()
    val flow = MutableSharedFlow<Int>()

    for (index in 0..10) {
        launch {
            delay(1000)
            for (item in index * 50..<(index+1) * 50) {
                flow.emit(item)
            }
        }
    }


    for (index in 11..20) {
        launch {
            delay(12000)
            for (item in index * 50..<(index+1) * 50) {
                flow.emit(item)
            }
        }
    }

//    for (index in 21..30) {
//        launch {
//            delay(3000)
//            for (item in index * 50..<(index+1) * 50) {
//                flow.emit(item)
//            }
//        }
//    }

    flow.collect { it ->
        if (!waiting.contains(it)) {
            waiting.add(it)
        }
        produceNumbers(it).consumeEach {
            consume(waiting, uploading, uploaded).send(it)
        }
    }
    println("===")
}

fun CoroutineScope.produceNumbers(num:Int) = produce<Int>(capacity = queueSize) {
    send(num)
}

fun CoroutineScope.consume(waiting: MutableList<Int>, uploading: MutableList<Int>, uploaded: MutableList<Int>): SendChannel<Int> = actor(capacity = queueSize) {
    for (index in channel) {
        mutex.withLock {
            waiting.remove(index)
            var futureUploadedItem = index
            if (uploading.size < queueSize) {
                uploading.add(index)
                upload(index, uploaded)
            } else {
                val uploadingItem = uploading.removeFirst()
                uploading.add(index)
                upload(uploadingItem, uploaded)
                futureUploadedItem = uploadingItem
            }
            println("Receive the number: $index, and uploading item is ${futureUploadedItem}, waiting size: ${waiting.size}, uploading size: ${uploading.size}, uploaded size: ${uploaded.size}")
        }
    }
}

suspend fun upload(item: Int, uploaded: MutableList<Int>): Boolean {
    coroutineScope {
        val job = launch(Dispatchers.IO) {
            delay(Random.nextInt(3000, 6000).toLong())
        }
        job.invokeOnCompletion { }.dispose()
        job.cancelAndJoin()
    }
    uploaded.add(item)
    return true
}