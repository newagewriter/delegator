package com.naw.delegator

import kotlin.reflect.KProperty

class FilterDelegator<T>(filter: (value: T) -> Boolean) : Delegator<T>(filterInstance(filter)) {
    private var value: T? = null

    override fun putValue(value: T) {
        this.value = value
    }

    override fun getValue(): T? {
        return this.value
    }

    class NotAllowedValueException(message: String = "") : Throwable(message)

    companion object {
        fun<U> filterInstance(filter: (value: U) -> Boolean): Listener<U> {
            return object : Listener<U> {
                override fun onSet(ref: Any, property: KProperty<*>, oldValue: U?, newValue: U): Boolean {
                    return filter.invoke(newValue)
                }

                override fun onGet(ref: Any, property: KProperty<*>, value: U) {

                }
            }
        }
    }
}

typealias filter<T> = FilterDelegator<T>