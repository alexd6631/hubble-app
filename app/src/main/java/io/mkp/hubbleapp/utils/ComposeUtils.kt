package io.mkp.hubbleapp.utils

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.*
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.ui.graphics.Image
import androidx.ui.graphics.ImageConfig
import androidx.ui.graphics.NativeImage
import androidx.ui.graphics.colorspace.ColorSpace
import androidx.ui.graphics.colorspace.ColorSpaces
import coil.Coil
import coil.api.newGetBuilder
import coil.request.GetRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <T> observe(data: LiveData<T>) = effectOf<T?> {
    val result = +state { data.value }
    val observer = +memo { Observer<T> { result.value = it } }

    +onCommit(data) {
        data.observeForever(observer)
        onDispose { data.removeObserver(observer) }
    }

    result.value
}

/**
 * A simple [image] effect, which loads [data] with the default options.
 */
@CheckResult(suggest = "+")
fun image(data: Any) = effectOf<Image?> {
    // Positionally memoize the request creation so
    // it will only be recreated if data changes.
    val request = +memo(data) {
        Coil.loader().newGetBuilder().data(data).build()
    }
    +image(request)
}

/**
 * A configurable [image] effect, which accepts a [request] value object.
 */
@CheckResult(suggest = "+")
fun image(request: GetRequest) = effectOf<Image?> {
    val image = +state<Image?> { null }

    // Execute the following code whenever the request changes.
    +onCommit(request) {
        val job = CoroutineScope(Dispatchers.Main.immediate).launch {
            // Start loading the image and await the result.
            val drawable = Coil.loader().get(request)
            image.value = MyAndroidImage(drawable.toBitmap())
        }

        // Cancel the request if the input to onCommit changes or
        // the Composition is removed from the composition tree.
        onDispose { job.cancel() }
    }

    // Emit a null Image to start with.
    image.value
}



class MyAndroidImage(val bitmap: Bitmap) : Image {

    /**
     * @see Image.width
     */
    override val width: Int
    get() = bitmap.width

    /**
     * @see Image.height
     */
    override val height: Int
    get() = bitmap.height

    override val config: ImageConfig
    get() = bitmap.config.toImageConfig()

    /**
     * @see Image.colorSpace
     */
    override val colorSpace: ColorSpace
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        bitmap.colorSpace?.toComposeColorSpace() ?: ColorSpaces.Srgb
    } else {
        ColorSpaces.Srgb
    }

    /**
     * @see Image.hasAlpha
     */
    override val hasAlpha: Boolean
    get() = bitmap.hasAlpha()

    /**
     * @see Image.nativeImage
     */
    override val nativeImage: NativeImage
    get() = bitmap

    /**
     * @see
     */
    override fun prepareToDraw() {
        bitmap.prepareToDraw()
    }
}


internal fun ImageConfig.toBitmapConfig(): Bitmap.Config {
    // Cannot utilize when statements with enums that may have different sets of supported
    // values between the compiled SDK and the platform version of the device.
    // As a workaround use if/else statements
    // See https://youtrack.jetbrains.com/issue/KT-30473 for details
    return if (this == ImageConfig.Argb8888) {
        Bitmap.Config.ARGB_8888
    } else if (this == ImageConfig.Alpha8) {
        Bitmap.Config.ALPHA_8
    } else if (this == ImageConfig.Rgb565) {
        Bitmap.Config.RGB_565
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this == ImageConfig.F16) {
        Bitmap.Config.RGBA_F16
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this == ImageConfig.Gpu) {
        Bitmap.Config.HARDWARE
    } else {
        Bitmap.Config.ARGB_8888
    }
}

internal fun Bitmap.Config.toImageConfig(): ImageConfig {
    // Cannot utilize when statements with enums that may have different sets of supported
    // values between the compiled SDK and the platform version of the device.
    // As a workaround use if/else statements
    // See https://youtrack.jetbrains.com/issue/KT-30473 for details
    @Suppress("DEPRECATION")
    return if (this == Bitmap.Config.ALPHA_8) {
        ImageConfig.Alpha8
    } else if (this == Bitmap.Config.RGB_565) {
        ImageConfig.Rgb565
    } else if (this == Bitmap.Config.ARGB_4444) {
        ImageConfig.Argb8888 // Always upgrade to Argb_8888
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this == Bitmap.Config.RGBA_F16) {
        ImageConfig.F16
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this == Bitmap.Config.HARDWARE) {
        ImageConfig.Gpu
    } else {
        ImageConfig.Argb8888
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun ColorSpace.toFrameworkColorSpace(): android.graphics.ColorSpace {
    val frameworkNamedSpace = when (this) {
        ColorSpaces.Srgb -> android.graphics.ColorSpace.Named.SRGB
        ColorSpaces.Aces -> android.graphics.ColorSpace.Named.ACES
        ColorSpaces.Acescg -> android.graphics.ColorSpace.Named.ACESCG
        ColorSpaces.AdobeRgb -> android.graphics.ColorSpace.Named.ADOBE_RGB
        ColorSpaces.Bt2020 -> android.graphics.ColorSpace.Named.BT2020
        ColorSpaces.Bt709 -> android.graphics.ColorSpace.Named.BT709
        ColorSpaces.CieLab -> android.graphics.ColorSpace.Named.CIE_LAB
        ColorSpaces.CieXyz -> android.graphics.ColorSpace.Named.CIE_XYZ
        ColorSpaces.DciP3 -> android.graphics.ColorSpace.Named.DCI_P3
        ColorSpaces.DisplayP3 -> android.graphics.ColorSpace.Named.DISPLAY_P3
        ColorSpaces.ExtendedSrgb -> android.graphics.ColorSpace.Named.EXTENDED_SRGB
        ColorSpaces.LinearExtendedSrgb ->
            android.graphics.ColorSpace.Named.LINEAR_EXTENDED_SRGB
        ColorSpaces.LinearSrgb -> android.graphics.ColorSpace.Named.LINEAR_SRGB
        ColorSpaces.Ntsc1953 -> android.graphics.ColorSpace.Named.NTSC_1953
        ColorSpaces.ProPhotoRgb -> android.graphics.ColorSpace.Named.PRO_PHOTO_RGB
        ColorSpaces.SmpteC -> android.graphics.ColorSpace.Named.SMPTE_C
        else -> android.graphics.ColorSpace.Named.SRGB
    }
    return android.graphics.ColorSpace.get(frameworkNamedSpace)
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun android.graphics.ColorSpace.toComposeColorSpace(): ColorSpace {
    return when (this) {
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.SRGB)
        -> ColorSpaces.Srgb
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.ACES)
        -> ColorSpaces.Aces
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.ACESCG)
        -> ColorSpaces.Acescg
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.ADOBE_RGB)
        -> ColorSpaces.AdobeRgb
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.BT2020)
        -> ColorSpaces.Bt2020
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.BT709)
        -> ColorSpaces.Bt709
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.CIE_LAB)
        -> ColorSpaces.CieLab
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.CIE_XYZ)
        -> ColorSpaces.CieXyz
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.DCI_P3)
        -> ColorSpaces.DciP3
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.DISPLAY_P3)
        -> ColorSpaces.DisplayP3
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.EXTENDED_SRGB)
        -> ColorSpaces.ExtendedSrgb
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.LINEAR_EXTENDED_SRGB)
        -> ColorSpaces.LinearExtendedSrgb
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.LINEAR_SRGB)
        -> ColorSpaces.LinearSrgb
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.NTSC_1953)
        -> ColorSpaces.Ntsc1953
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.PRO_PHOTO_RGB)
        -> ColorSpaces.ProPhotoRgb
        android.graphics.ColorSpace.get(android.graphics.ColorSpace.Named.SMPTE_C)
        -> ColorSpaces.SmpteC
        else -> ColorSpaces.Srgb
    }
}