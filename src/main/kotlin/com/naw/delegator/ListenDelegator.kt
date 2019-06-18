package com.naw.delegator

import kotlin.reflect.KProperty

class ListenDelegator<T>(listener: (bundle: ListenBundle<T>) -> Unit)
    : Delegator<T>(instanceListener(listener)) {
    private var value: T? = null
    override fun putValue(value: T) {
        this.value = value
    }

    override fun getValue(): T? {
        return this.value
    }

    class ListenBundle<T>(
        val type: ActionType,
        val value: T?,
        val oldValue: T? = null
    )

    enum class ActionType {
        GET,
        SET
    }

    companion object {
        fun<T> instanceListener(listener: (bundle: ListenBundle<T>) -> Unit) : Listener<T> {
            return object : Listener<T> {
                override fun onSet(ref: Any, property: KProperty<*>, oldValue: T?, newValue: T): Boolean {
                    val bundle = ListenBundle(ActionType.SET, newValue, oldValue)
                    listener.invoke(bundle)
                    return true
                }

                override fun onGet(ref: Any, property: KProperty<*>, value: T) {
                    listener.invoke(ListenBundle(ActionType.GET, value))
                }
            }
        }
    }
}

typealias listen<T> = ListenDelegator<T>