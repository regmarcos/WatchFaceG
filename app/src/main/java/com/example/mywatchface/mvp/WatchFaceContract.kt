package com.example.mywatchface.mvp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import java.util.Calendar
import java.util.TimeZone

interface WatchFaceContract {

    interface WatchFaceView {
        fun initializeBackground(background: Bitmap, mAmbient: Boolean)
        fun initializeWatchFace()
        fun updateWatchHandStyle(mAmbient: Boolean)
        fun initGrayBackgroundBitmap()
        fun getRandomColor(): Int
        fun drawWatchFace(canvas: Canvas, mAmbient: Boolean)
        fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
        fun setAlphas(inMuteMode: Boolean)
        fun getColorBackground(): Int
        fun setColorBackground(color: Int)
        fun getMCalendar(): Calendar
        fun setMCalendar(calendar: Calendar)
        fun setTimeZone(timeZone: TimeZone)
        fun getMBackgroundBitmap(): Bitmap
        fun getMLowBitAmbient(): Boolean
        fun setMLowBitAmbient(state: Boolean)
        fun getMBurnInProtection(): Boolean
        fun setMBurnInProtection(state: Boolean)
        fun getMBackgroundPaint(): Paint
        fun getMGrayBackgroundBitmap(): Bitmap
    }

    interface WatchFacePresenter {
        fun init(background: Bitmap)
        fun propertiesChanged(properties: Bundle)
        fun ambientModeChanged(inAmbientMode: Boolean)
        fun interrumptionFilterChanged(interruptionFilter: Int): Boolean
        fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
        fun drawBackground(canvas: Canvas)
        fun setDefaultTimeZone()

    }
}