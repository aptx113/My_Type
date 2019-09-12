package com.terricom.mytype.profile

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView


object BlurBitmapUtils {
    /**
     * 建议模糊度(在0.0到25.0之间)
     */
    private val BLUR_RADIUS = 20
    private val SCALED_WIDTH = 100
    private val SCALED_HEIGHT = 100

    @JvmOverloads
    fun blur(imageView: ImageView, bitmap: Bitmap, radius: Int = BLUR_RADIUS) {
        imageView.setImageBitmap(getBlurBitmap(imageView.getContext(), bitmap, radius))
    }


    @JvmOverloads
    fun getBlurBitmap(context: Context, bitmap: Bitmap, radius: Int = BLUR_RADIUS): Bitmap {
        // 将缩小后的图片做为预渲染的图片。
        val inputBitmap = Bitmap.createScaledBitmap(bitmap, SCALED_WIDTH, SCALED_HEIGHT, false)
        // 创建一张渲染后的输出图片。
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        // 创建RenderScript内核对象
        val rs = RenderScript.create(context)
        // 创建一个模糊效果的RenderScript的工具对象
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(radius.toFloat())
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn)
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut)

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap)

        return outputBitmap
    }

}