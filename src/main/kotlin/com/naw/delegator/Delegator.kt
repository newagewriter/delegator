package com.naw.delegator

import kotlin.reflect.KProperty

abstract class Delegator<T>(private val delegate: Listener<T>) {

    operator fun getValue(ref: Any, property: KProperty<*>): T {
        val value = getValue()
        value?.let {
            delegate.onGet(ref, property, it)
            return it
        } ?: throw UninitializedPropertyAccessException()
    }

    operator fun  setValue(ref: Any, property: KProperty<*>, value: T) {
        if (delegate.onSet(ref, property, getValue(), value))
            putValue(value)
    }

    abstract fun putValue(value: T)
    abstract fun getValue(): T?

    interface Listener<U> {
        fun onSet(ref: Any, property: KProperty<*>, oldValue: U?, newValue: U) : Boolean
        fun onGet(ref: Any, property: KProperty<*>, value: U)
    }
}