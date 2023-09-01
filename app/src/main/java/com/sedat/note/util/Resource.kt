package com.sedat.note.util

sealed class Resource<out T>(val data: T?, val message: String?) {
    class Loading<T> : Resource<T>(null, null)
    class Success<T>(data: T?) : Resource<T>(data, null)
    class Error<T>(exception: String?) : Resource<T>(null, exception)
}