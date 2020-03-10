package com.example.mywatchface.mvp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import java.util.Calendar

interface WatchFaceContract {

    interface WatchFaceView {

        var mCalendar: Calendar
        var mCenterX: Float
        var mCenterY: Float
        var mSecondHandLength: Float
        var sMinuteHandLength: Float
        var sHourHandLength: Float
        var mHourPaint: Paint
        var mMinutePaint: Paint
        var mSecondPaint: Paint
        var mBackgroundPaint: Paint
        var mBackgroundBitmap: Bitmap
        var mGrayBackgroundBitmap: Bitmap
        var colorBackground: Int
        var mLowBitAmbient: Boolean
        var mBurnInProtection: Boolean

        fun initializeBackground(background: Bitmap, mAmbient: Boolean)
        fun initializeWatchFace()
        fun updateWatchHandStyle(mAmbient: Boolean)
        fun initGrayBackgroundBitmap()
        fun getRandomColor(): Int
        fun drawWatchFace(canvas: Canvas, mAmbient: Boolean)
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