package com.example.mywatchface.mvp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import com.example.mywatchface.ZERO_FLOAT
import java.util.Calendar
import java.util.TimeZone

open class WatchFaceContract {

    abstract class WatchFaceView {

        var mCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        var mCenterX: Float = ZERO_FLOAT
        var mCenterY: Float = ZERO_FLOAT
        var mSecondHandLength: Float = ZERO_FLOAT
        var sMinuteHandLength: Float = ZERO_FLOAT
        var sHourHandLength: Float = ZERO_FLOAT
        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        protected var mWatchHandColor: Int = 0
        protected var mWatchHandHighlightColor: Int = 0
        protected var mWatchHandShadowColor: Int = 0
        lateinit var mHourPaint: Paint
        lateinit var mMinutePaint: Paint
        lateinit var mSecondPaint: Paint
        protected lateinit var mTickAndCirclePaint: Paint
        lateinit var mBackgroundPaint: Paint
        lateinit var mBackgroundBitmap: Bitmap
        lateinit var mGrayBackgroundBitmap: Bitmap
        var colorBackground = 0
        var mLowBitAmbient: Boolean = false
        var mBurnInProtection: Boolean = false

        abstract fun initializeBackground(background: Bitmap, mAmbient: Boolean)
        abstract fun initializeWatchFace()
        abstract fun updateWatchHandStyle(mAmbient: Boolean)
        abstract fun initGrayBackgroundBitmap()
        abstract fun getRandomColor(): Int
        abstract fun drawWatchFace(canvas: Canvas, mAmbient: Boolean)
    }

    abstract class WatchFacePresenter {
        abstract fun init(background: Bitmap)
        abstract fun propertiesChanged(properties: Bundle)
        abstract fun ambientModeChanged(inAmbientMode: Boolean)
        abstract fun interrumptionFilterChanged(interruptionFilter: Int): Boolean
        abstract fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
        abstract fun drawBackground(canvas: Canvas)
        abstract fun setDefaultTimeZone()
    }
}