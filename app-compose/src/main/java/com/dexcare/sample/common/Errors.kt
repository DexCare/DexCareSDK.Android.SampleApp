package com.dexcare.sample.common

fun Throwable.displayMessage(): String {
    return if (!message.isNullOrBlank()) {
        message.toString()
    } else {
        "Error: ${this.javaClass.simpleName}"
    }
}
