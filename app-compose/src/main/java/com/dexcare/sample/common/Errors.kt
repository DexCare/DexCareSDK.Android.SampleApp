package com.dexcare.sample.common

import com.dexcare.sample.data.ErrorResult

fun Throwable.displayMessage(): String {
    return if (!message.isNullOrBlank()) {
        message.toString()
    } else {
        "Error: ${this.javaClass.simpleName}"
    }
}

fun Throwable.toError(): ErrorResult {
    val message = if (!message.isNullOrBlank()) {
        message.toString()
    } else {
        "Error: ${this.javaClass.simpleName}"
    }
    return ErrorResult(title = "Error", message = message)
}
