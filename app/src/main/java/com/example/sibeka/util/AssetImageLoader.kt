package com.example.sibeka.util

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest

object AssetImageLoader {

    fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED) // Assets lokal tidak perlu disk cache ganda
            .crossfade(180)
            .respectCacheHeaders(false)
            .build()
    }

    fun buildAssetRequest(
        context: Context,
        assetRelativePath: String,
        targetWidth: Int = 400,
        targetHeight: Int = 400
    ): ImageRequest {
        val cleanPath = assetRelativePath.trimStart('/')
        val fullUri = "file:///android_asset/$cleanPath"

        return ImageRequest.Builder(context)
            .data(fullUri)
            .size(targetWidth, targetHeight)
            .error(com.example.sibeka.R.drawable.ic_asset_missing)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .crossfade(150)
            .build()
    }
}
