package com.example.mywatchface.mvp.presenter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.wearable.watchface.WatchFaceService
import android.view.SurfaceHolder
import com.example.mywatchface.MARGIN_DRAW_BITMAP
import com.example.mywatchface.ZERO_FLOAT
import com.example.mywatchface.mvp.WatchFaceContract
import java.util.Calendar
import java.util.TimeZone

class WatchFacePresenter(private val view: WatchFaceContract.WatchFaceView) :
    WatchFaceContract.WatchFacePresenter {

    private var mMuteMode: Boolean = false
    var onTapEnabled = false
    var newColor = false
    var mAmbient: Boolean = false

    override fun init(background: Bitmap) {
        view.setMCalendar(Calendar.getInstance())
        view.initializeBackground(background, mAmbient)
        view.initializeWatchFace()
    }

    override fun propertiesChanged(properties: Bundle) {
        view.setMLowBitAmbient(properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false))
        view.setMBurnInProtection(properties.getBoolean(WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false))
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
            view.setAlphas(inMuteMode)
            return true
        }
        return false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        view.surfaceChanged(holder, format, width, height)
    }

    override fun drawBackground(canvas: Canvas) {
        val now = System.currentTimeMillis()
        view.getMCalendar().timeInMillis = now
        if (mAmbient && (view.getMLowBitAmbient() || view.getMBurnInProtection())) {
            canvas.drawColor(Color.BLACK)
        } else if (mAmbient) {
            canvas.drawBitmap(
                view.getMGrayBackgroundBitmap(),
                ZERO_FLOAT,
                ZERO_FLOAT,
                view.getMBackgroundPaint()
            )
        } else if (onTapEnabled && newColor) {
            view.setColorBackground(view.getRandomColor())
            canvas.drawColor(view.getColorBackground())
            newColor = false
        } else if (onTapEnabled && !newColor) {
            canvas.drawColor(view.getColorBackground())
        } else {
            canvas.drawBitmap(view.getMBackgroundBitmap(), MARGIN_DRAW_BITMAP, MARGIN_DRAW_BITMAP, view.getMBackgroundPaint())
        }
        view.drawWatchFace(canvas, mAmbient)
    }

    override fun setDefaultTimeZone() {
        view.setTimeZone(TimeZone.getDefault())
    }
}