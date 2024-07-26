import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BlueToothHandlerTest2 {

    val handler: BlueToothHandler = BlueToothHandler()

    @BeforeTest
    fun setup() {

    }

    @Test
    fun test() {
        val result = handler.retrieveData(2)
        println(result)
        assertEquals("2", result)
    }
}