package com.taleb.practices.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil
import kotlin.math.roundToInt


//////////////////////////String///////////////////////////////////

/**
 * this function separate your string with separator
 * @param count separation count
 * @param separator character for separation
 * @param fromRightToLeft define separation which start from right or left
 * */
fun String.separate(count: Int = 3, separator: Char = ',', fromRightToLeft: Boolean = true): String {

    if (this.isEmpty()) {
        return ""
    }

    if (count < 1) {
        return this
    }

    if (count >= this.length) {
        return this
    }

    val str = if (fromRightToLeft) {
        this.reversed()
    } else {
        this
    }
    val chars = str.toCharArray()
    val namOfSeparation = ceil(chars.size.toFloat() / count.toFloat()) - 1
    val separatedChars = CharArray(chars.size + namOfSeparation.roundToInt())
    var j = 0
    for (i in 0 until chars.size) {
        separatedChars[j] = chars[i]
        if (i > 0 && (i + 1) < chars.size && (i + 1) % count == 0) {
            j += 1
            separatedChars[j] = separator
        }

        j += 1
    }

    return if (fromRightToLeft)
        String(separatedChars).reversed()
    else
        String(separatedChars)
}

fun String.isValidIranianNationalCode(): Boolean {

    return when {
        this.isEmpty() -> false
        this.length != 10 -> false
        this.subSequence(0, 3) == "000" -> false
        else -> {
            return try {
                val check = 1
                for (i in 0 until this.length) {
                    val num = this.subSequence(i, i + 1).toString().toInt()
                    val res = num * (10 - i)
                    check.plus(res.toInt())
                }

                check.rem(11) != 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}

fun String.isValidIranianMobileNumber(): Boolean {
    return if (this.isEmpty())
        false
    else
        this.matches(Regex("(0|\\+98)?([ ]|,|-|[()]){0,2}9[1|2|3|4]([ ]|,|-|[()]){0,2}(?:[0-9]([ ]|,|-|[()]){0,2}){8}"))
}

/////////////////////////////View////////////////////////////////

fun View.animateVertically(goesDown: Boolean = true) {
    val animatorSet = AnimatorSet()
    val distance: Float = if (goesDown)
        100f
    else
        -100f
    val animatorTranslateY = ObjectAnimator.ofFloat(this, "translationY", distance, 0.0f)
    animatorTranslateY.duration = 700

    animatorSet.playTogether(animatorTranslateY)
    animatorSet.start()
}

fun View.animateHorizontally(goesRight: Boolean = true) {
    val animatorSet = AnimatorSet()
    val distance: Float = if (goesRight)
        100f
    else
        -100f
    val animatorTranslateX = ObjectAnimator.ofFloat(this, "translationX", distance, 0.0f)
    animatorTranslateX.duration = 700

    animatorSet.playTogether(animatorTranslateX)
    animatorSet.start()
}

fun View.getScreenShot(): Bitmap {
    val screenView = this.rootView
    screenView.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(screenView.drawingCache)
    screenView.isDrawingCacheEnabled = false
    return bitmap
}


///////////////////////////Context//////////////////////////////////

fun Context.isAllPermissionGranted(needPermission: List<String>): Boolean {
    val unCatchesPermissionStr = StringBuilder()
    for (permission in needPermission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            unCatchesPermissionStr.append("$permission,")
        }
    }

    if (unCatchesPermissionStr.length < 2) {
        return true
    }
    var deniedPermission = unCatchesPermissionStr.split(',')
    deniedPermission = deniedPermission.subList(0, deniedPermission.size - 2)
    if (deniedPermission.isEmpty()) {
        return true
    }

    return false
}


fun Context.getDeniedPermissionsFrom(permissions: List<String>): List<String> {
    val unCatchesPermissionStr = StringBuilder()
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            unCatchesPermissionStr.append("$permission,")
        }
    }

    if (unCatchesPermissionStr.length < 2) {
        return ArrayList()
    }
    var deniedPermission = unCatchesPermissionStr.split(',')
    deniedPermission = deniedPermission.subList(0, deniedPermission.size - 2)
    return deniedPermission
}

///////////////////////////File//////////////////////////////////

fun File.getUri(context: Context): Uri? {
    val filePath = this.absolutePath;
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.Media._ID),
        MediaStore.Images.Media.DATA + "=? ",
        arrayOf(filePath), null
    );
    return if (cursor != null && cursor.moveToFirst()) {
        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        cursor.close();
        Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
    } else {
        if (this.exists()) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, filePath);
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            );
        } else {
            null
        }
    }
}

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

///////////////////////////Bitmap//////////////////////////////////

fun Bitmap.blur(context: Context, scale: Float = 0.4f, radius: Float = 7.5f): Bitmap {
    val width = Math.round(this.width * scale)
    val height = Math.round(this.height * scale)

    val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(radius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    return outputBitmap
}


fun Bitmap.saveToFile(fileName: String, filePath: File, quality: Int = 85): File? {
    val dir = File(filePath.absolutePath)
    if (!dir.exists())
        dir.mkdirs()
    val file = File(filePath.absolutePath, fileName)
    try {
        val fOut = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG, quality, fOut)
        fOut.flush()
        fOut.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return file
}

///////////////////////////FragmentManager//////////////////////////////////

fun FragmentManager.replaceFragmentInFrame(fragment: androidx.fragment.app.Fragment, frameLayoutId: Int) {
    val transaction = this.beginTransaction()
    transaction.replace(frameLayoutId, fragment).commit()
}
