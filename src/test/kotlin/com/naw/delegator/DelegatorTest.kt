package com.naw.delegator

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class DelegatorTest {
    var checkLastAction = UNKNOWN
    var helloFilter : String by filter { value : String -> value != NOT_ALLOWED_VALUE}

    @Test
    fun testDelegatorListenerCorrectCall() {
        val sample = Sample("Hello", this)
        assertEquals(INITIALIZE, checkLastAction)
        println(sample.stringSample)
        assertEquals(GET, checkLastAction)
        sample.stringSample = "Hello2"
        assertEquals(SET, checkLastAction)
    }

    @Test(expected = UninitializedPropertyAccessException::class)
    fun testFilterDelegatorCheckValueBeforeInit() {
        val testGet = helloFilter
        fail("Something went wrong helloFilter shouldn't be initialized yet but equal $testGet")
    }

    @Test
    fun testFilterDelegatorCheckCorrectValue() {
        try {
            helloFilter = "Hello2"
        } catch (ex : FilterDelegator.NotAllowedValueException) {
            fail(ex.message)
        }
        val testGet = helloFilter
        assertEquals(testGet, "Hello2")
    }

    @Test(expected = FilterDelegator.NotAllowedValueException::class)
    fun testFilterDelegatorSetIncorectValue() {
        helloFilter = NOT_ALLOWED_VALUE
    }

    companion object {
        const val UNKNOWN = -1
        const val INITIALIZE = 0
        const val GET = 1
        const val SET = 2
        const val NOT_ALLOWED_VALUE = "NotAllowedValue"
    }

    class Sample(someString: String, private val test: DelegatorTest) {
        var stringSample: String by listen {
            test.checkLastAction = when(it.type) {
                ListenDelegator.ActionType.GET -> GET
                ListenDelegator.ActionType.SET -> if (it.oldValue != null) SET else INITIALIZE
            }
        }

        init {
            stringSample = someString
        }
    }
}