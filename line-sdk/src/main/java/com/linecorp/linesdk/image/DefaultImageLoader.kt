package com.linecorp.linesdk.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.widget.ImageView
import androidx.collection.LruCache
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.WeakHashMap

internal object DefaultImageLoader : ImageLoader {
    private const val TAG = "DefaultImageLoader"
    private const val BUFFER_SIZE = 8192

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val tasks = Collections.synchronizedMap(WeakHashMap<ImageView, Job>())

    private val memoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    override fun loadImage(url: String?, imageView: ImageView, placeholderResId: Int) {
        imageView.setImageResource(placeholderResId)

        tasks[imageView]?.cancel()

        if (url.isNullOrBlank()) {
            return
        }

        val cachedBitmap = memoryCache.get(url)
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap)
            return
        }

        // Get target dimensions for sampling. Use screen dimensions as fallback if ImageView not laid out yet.
        val targetWidth = if (imageView.width > 0) imageView.width else imageView.context.resources.displayMetrics.widthPixels
        val targetHeight = if (imageView.height > 0) imageView.height else imageView.context.resources.displayMetrics.heightPixels

        val imageViewRef = WeakReference(imageView)

        val job = coroutineScope.launch {
            try {
                val bitmap = downloadAndProcessImage(url, targetWidth, targetHeight)

                imageViewRef.get()?.let {  targetImageView ->
                    if (bitmap != null) {
                        memoryCache.put(url, bitmap)
                        targetImageView.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e(TAG, "Error loading image from URL: $url", e)
                }
            } finally {
                imageViewRef.get()?.let { tasks.remove(it) }
            }
        }
        tasks[imageView] = job
    }

    private suspend fun downloadAndProcessImage(url: String, targetWidth: Int, targetHeight: Int): Bitmap? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val imageUrl = URL(url)
            connection = imageUrl.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            // We need to read the input stream into memory, not just the bitmap directly,
            // to handle EXIF data and image rotation correctly.
            val byteArrayOutputStream = ByteArrayOutputStream()
            connection.inputStream.use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                var len: Int
                while (input.read(buffer).also { len = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, len)
                }
            }

            val imageData = byteArrayOutputStream.toByteArray()

            // First pass: Decode image bounds to get original dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)

            // Second pass: Decode actual bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            var bitmap: Bitmap? = BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)

            // Read EXIF orientation
            val orientation = ByteArrayInputStream(imageData).use { exifInput ->
                ExifInterface(exifInput).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }

            if (bitmap != null) {
                bitmap = rotateBitmap(bitmap, orientation)
            }
            bitmap
        } catch (e: Exception) {
            if (e !is CancellationException) {
                Log.e(TAG, "Error loading image from URL: $url", e)
            }
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            return bitmap
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
            else -> {
                Log.w(TAG, "Unsupported EXIF orientation: $orientation")
                return bitmap
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }



}
