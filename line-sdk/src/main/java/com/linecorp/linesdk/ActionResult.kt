package com.linecorp.linesdk


sealed class ActionResult<T, U> {
    data class Success<T, U>(val value: T) : ActionResult<T, U>()
    data class Error<T, U>(val value: U) : ActionResult<T, U>()
}
