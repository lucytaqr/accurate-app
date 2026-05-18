package com.accurate.userdirectory.core.network

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiErrorHandler {
    fun handle(throwable: Throwable): String = when (throwable) {
        is UnknownHostException -> "Tidak ada koneksi internet."
        is SocketTimeoutException -> "Koneksi lambat. Coba lagi nanti."
        is HttpException -> {
            when (throwable.code()) {
                in 400..499 -> "Data belum valid. Periksa kembali input Anda."
                in 500..599 -> "Server sedang bermasalah. Coba lagi nanti."
                else -> "Terjadi kesalahan. Coba lagi."
            }
        }
        is IOException -> "Gagal terhubung. Periksa koneksi internet Anda."
        else -> throwable.message ?: "Terjadi kesalahan. Coba lagi."
    }
}
