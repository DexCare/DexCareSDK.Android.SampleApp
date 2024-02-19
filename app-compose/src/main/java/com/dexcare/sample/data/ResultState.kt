package com.dexcare.sample.data

/**
 * State representing result of any actions related with data.
 * */
sealed class ResultState<out T : Any> {

    abstract fun hasFinished(): Boolean

    data object UnInitialized : ResultState<Nothing>() {
        override fun hasFinished() = false
    }

    data class Error(val errorResult: ErrorResult) : ResultState<Nothing>() {
        override fun hasFinished() = true
    }

    data class Complete<out T : Any>(val data: T) : ResultState<T>() {
        override fun hasFinished() = true
    }
}

/**
 * Error displayable to UI.
 * */
data class ErrorResult(val title: String = "Error", val message: String)
