package com.example.mywatchface.mvp.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.palette.graphics.Palette
import com.example.mywatchface.ALPHA
import com.example.mywatchface.CENTER_GAP_AND_CIRCLE_RADIUS
import com.example.mywatchface.FRACTION_ANGLE
import com.example.mywatchface.HOURS
import com.example.mywatchface.HOURS_IN_ANALOG_CLOCK
import com.example.mywatchface.HOUR_HAND_OFFSET
import com.example.mywatchface.HOUR_STROKE_WIDTH
import com.example.mywatchface.INNER_TICK_RADIUS_OFFSET
import com.example.mywatchface.MILLISECONDS
import com.example.mywatchface.MINUTE_STROKE_WIDTH
import com.example.mywatchface.OFFSET
import com.example.mywatchface.ONE_ROUND
import com.example.mywatchface.RGB_POSSIBLE_VALUES
import com.example.mywatchface.SECOND_TICK_STROKE_WIDTH
import com.example.mywatchface.SHADOW_RADIUS
import com.example.mywatchface.ZERO_FLOAT
import com.example.mywatchface.mvp.WatchFaceContract
import java.util.Calendar
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

class WatchFaceView: WatchFaceContract.WatchFaceView() {

    override fun initializeBackground(background: Bitmap, mAmbient: Boolean) { //View
        mBackgroundPaint = Paint().apply {
            color = Color.BLACK
        }
        mBackgroundBitmap = background

        /* Extracts colors from background image to improve watchface style. */
        Palette.from(mBackgroundBitmap).generate {
            it?.let {
                mWatchHandHighlightColor = it.getVibrantColor(Color.RED)
                mWatchHandColor = it.getLightVibrantColor(Color.WHITE)
                mWatchHandShadowColor = it.getDarkMutedColor(Color.BLACK)
                updateWatchHandStyle(mAmbient)
            }
        }
    }

    override fun initializeWatchFace() {
        /* Set defaults for colors */
        mWatchHandColor = Color.WHITE
        mWatchHandHighlightColor = Color.RED
        mWatchHandShadowColor = Color.BLACK
        mHourPaint = hoursPaint()
        mMinutePaint = minutesPaint()
        mSecondPaint = secondsPaint()
        mTickAndCirclePaint = tickAndCirclePaint()
    }

    override fun updateWatchHandStyle(mAmbient: Boolean) {
        if (mAmbient) {
            updateInAmbientMode()
        } else {
            updateInNormalMode()
        }
    }

    override fun initGrayBackgroundBitmap() {
        mGrayBackgroundBitmap = Bitmap.createBitmap(
            mBackgroundBitmap.width,
            mBackgroundBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(mGrayBackgroundBitmap)
        val grayPaint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(ZERO_FLOAT)
        val filter = ColorMatrixColorFilter(colorMatrix)
        grayPaint.colorFilter = filter
        canvas.drawBitmap(mBackgroundBitmap, ZERO_FLOAT, ZERO_FLOAT, grayPaint)
    }

    override fun getRandomColor(): Int {
        val random = Random()
        return Color.argb(
            ALPHA, random.nextInt(RGB_POSSIBLE_VALUES),
            random.nextInt(RGB_POSSIBLE_VALUES),
            random.nextInt(RGB_POSSIBLE_VALUES)
        )
    }

    override fun drawWatchFace(canvas: Canvas, mAmbient: Boolean) {
        val innerTickRadius = mCenterX - INNER_TICK_RADIUS_OFFSET
        val outerTickRadius = mCenterX
        for (tickIndex in HOURS) {
            val tickRot = (tickIndex.toDouble() * ONE_ROUND / HOURS_IN_ANALOG_CLOCK).toFloat()
            val innerX = sin(tickRot.toDouble()).toFloat() * innerTickRadius
            val innerY = (-cos(tickRot.toDouble())).toFloat() * innerTickRadius
            val outerX = sin(tickRot.toDouble()).toFloat() * outerTickRadius
            val outerY = (-cos(tickRot.toDouble())).toFloat() * outerTickRadius
            canvas.drawLine(
                mCenterX + innerX, mCenterY + innerY,
                mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint
            )
        }
        val seconds = mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / MILLISECONDS
        val secondsRotation = seconds * FRACTION_ANGLE
        val minutesRotation = mCalendar.get(Calendar.MINUTE) * FRACTION_ANGLE
        val hourHandOffset = mCalendar.get(Calendar.MINUTE) / HOUR_HAND_OFFSET
        val hoursRotation = mCalendar.get(Calendar.HOUR) * OFFSET + hourHandOffset
        canvas.save()
        canvas.rotate(hoursRotation, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
            mCenterX,
            mCenterY - sHourHandLength,
            mHourPaint
        )
        canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
            mCenterX,
            mCenterY - sMinuteHandLength,
            mMinutePaint
        )
        if (!mAmbient) {
            canvas.rotate(secondsRotation - minutesRotation, mCenterX, mCenterY)
            canvas.drawLine(
                mCenterX,
                mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                mCenterX,
                mCenterY - mSecondHandLength,
                mSecondPaint
            )

        }
        canvas.drawCircle(mCenterX, mCenterY, CENTER_GAP_AND_CIRCLE_RADIUS, mTickAndCirclePaint)
        canvas.restore()
    }

    private fun tickAndCirclePaint() = Paint().apply {
        color = mWatchHandColor
        strokeWidth = SECOND_TICK_STROKE_WIDTH
        isAntiAlias = true
        style = Paint.Style.STROKE
        setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
    }

    private fun secondsPaint() = Paint().apply {
        color = mWatchHandHighlightColor
        strokeWidth = SECOND_TICK_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
    }

    private fun minutesPaint() = Paint().apply {
        color = mWatchHandColor
        strokeWidth = MINUTE_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
    }

    private fun hoursPaint() = Paint().apply {
        color = mWatchHandColor
        strokeWidth = HOUR_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
    }

    private fun updateInAmbientMode() {
        mHourPaint.color = Color.WHITE
        mMinutePaint.color = Color.WHITE
        mSecondPaint.color = Color.WHITE
        mTickAndCirclePaint.color = Color.WHITE
        mHourPaint.isAntiAlias = false
        mMinutePaint.isAntiAlias = false
        mSecondPaint.isAntiAlias = false
        mTickAndCirclePaint.isAntiAlias = false
        mHourPaint.clearShadowLayer()
        mMinutePaint.clearShadowLayer()
        mSecondPaint.clearShadowLayer()
        mTickAndCirclePaint.clearShadowLayer()
    }

    private fun updateInNormalMode() {
        mHourPaint.color = mWatchHandColor
        mMinutePaint.color = mWatchHandColor
        mSecondPaint.color = mWatchHandHighlightColor
        mTickAndCirclePaint.color = mWatchHandColor
        mHourPaint.isAntiAlias = true
        mMinutePaint.isAntiAlias = true
        mSecondPaint.isAntiAlias = true
        mTickAndCirclePaint.isAntiAlias = true
        mHourPaint.setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
        mMinutePaint.setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
        mSecondPaint.setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
        mTickAndCirclePaint.setShadowLayer(
            SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor
        )
    }
}