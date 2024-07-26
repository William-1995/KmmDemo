package com.example.myapp

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

//class BlueToothHandler() {
//
//    fun retrieveData(id: Int):String {
//        val str = id.toString()
//        return str
//    }
//}

class BlueToothHandlerTest {

    val handler: BlueToothHandler = BlueToothHandler()

    @BeforeTest
    fun setup() {

    }

    @Test
    fun test() {
        val result = handler.retrieveData(1)
        println(result)
        assertEquals("1", result)
    }
}