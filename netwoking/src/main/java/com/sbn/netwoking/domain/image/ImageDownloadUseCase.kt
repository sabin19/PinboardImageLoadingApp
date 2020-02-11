package com.sbn.netwoking.domain.image

import android.graphics.Bitmap
import com.sbn.netwoking.data.ImageRepository
import com.sbn.netwoking.domain.UseCase
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.NetworkUtils

class ImageDownloadUseCase constructor(
    private val networkUtils: NetworkUtils,
    private val repository: ImageRepository
) :
    UseCase<String, Bitmap>() {
    override fun execute(parameters: String): Bitmap {
        return if (networkUtils.hasNetworkConnection()) {
            when (val result = repository.getBitMapImage(parameters)) {
                is Response.Success -> {
                    result.data
                }
                is Response.Error -> throw result.exception
            }
        } else throw Exception("No Internet connection")

    }
}