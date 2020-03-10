package com.example.mywatchface.mvp.presenter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.wearable.watchface.WatchFaceService
import android.view.SurfaceHolder
import com.example.mywatchface.ALPHA
import com.example.mywatchface.HOURS_ALPHA
import com.example.mywatchface.HOURS_HAND_LENGTH
import com.example.mywatchface.MIDDLE
import com.example.mywatchface.MINUTES_ALPHA
import com.example.mywatchface.MINUTES_HAND_LENGTH
import com.example.mywatchface.SECONDS_ALPHA
import com.example.mywatchface.SECONDS_HAND_LENGTH
import com.example.mywatchface.ZERO_FLOAT
import com.example.mywatchface.mvp.WatchFaceContract
import java.util.Calendar
import java.util.TimeZone

class WatchFacePresenter(private val view: WatchFaceContract.WatchFaceView) :
    WatchFaceContract.WatchFacePresenter() {

    private var mMuteMode: Boolean = false
    var onTapEnabled = false
    var newColor = false
    var mAmbient: Boolean = false

    override fun init(background: Bitmap) {
        view.mCalendar = Calendar.getInstance()
        view.initializeBackground(background, mAmbient)
        view.initializeWatchFace()
    }

    override fun propertiesChanged(properties: Bundle) {
        view.mLowBitAmbient = properties.getBoolean(
            WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false
        )
        view.mBurnInProtection = properties.getBoolean(
            WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false
        )
    }

    override fun ambientModeChanged(inAmbientMode: Boolean) {
        mAmbient = inAmbientMode
        view.updateWatchHandStyle(mAmbient)

    }

    override fun interrumptionFilterChanged(interruptionFilter: Int): Boolean {
        val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE

        /* Dim display in mute mode. */
        if (mMuteMode != inMuteMode) {
            mMuteMode = inMuteMode
            view.mHourPaint.alpha = if (inMuteMode) HOURS_ALPHA else ALPHA
            view.mMinutePaint.alpha = if (inMuteMode) MINUTES_ALPHA else ALPHA
            view.mSecondPaint.alpha = if (inMuteMode) SECONDS_ALPHA else ALPHA
            return true
        }
        return false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        view.mCenterX = width / MIDDLE
        view.mCenterY = height / MIDDLE
        view.mSecondHandLength = (view.mCenterX * SECONDS_HAND_LENGTH).toFloat()
        view.sMinuteHandLength = (view.mCenterX * MINUTES_HAND_LENGTH).toFloat()
        view.sHourHandLength = (view.mCenterX * HOURS_HAND_LENGTH).toFloat()

        val scale = width.toFloat() / view.mBackgroundBitmap.width.toFloat()

        view.mBackgroundBitmap = Bitmap.createScaledBitmap(
            view.mBackgroundBitmap,
            (view.mBackgroundBitmap.width * scale).toInt(),
            (view.mBackgroundBitmap.height * scale).toInt(), true
        )
        if (!view.mBurnInProtection && !view.mLowBitAmbient) {
            view.initGrayBackgroundBitmap()
        }
    }

    override fun drawBackground(canvas: Canvas) {
        val now = System.currentTimeMillis()
        view.mCalendar.timeInMillis = now
        if (mAmbient && (view.mLowBitAmbient || view.mBurnInProtection)) {
            canvas.drawColor(Color.BLACK)
        } else if (mAmbient) {
            canvas.drawBitmap(
                view.mGrayBackgroundBitmap,
                ZERO_FLOAT,
                ZERO_FLOAT,
                view.mBackgroundPaint
            )
        } else if (onTapEnabled && newColor) {
            view.colorBackground = view.getRandomColor()
            canvas.drawColor(view.colorBackground)
            newColor = false
        } else if (onTapEnabled && !newColor) {
            canvas.drawColor(view.colorBackground)
        } else {
            canvas.drawBitmap(view.mBackgroundBitmap, ZERO_FLOAT, ZERO_FLOAT, view.mBackgroundPaint)
        }
        view.drawWatchFace(canvas, mAmbient)
    }

    override fun setDefaultTimeZone() {
        view.mCalendar.timeZone = TimeZone.getDefault()
    }
}