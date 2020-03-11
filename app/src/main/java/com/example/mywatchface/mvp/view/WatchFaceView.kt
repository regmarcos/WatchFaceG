package com.example.mywatchface.mvp.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.SurfaceHolder
import androidx.palette.graphics.Palette
import com.example.mywatchface.ACTIVE
import com.example.mywatchface.ALPHA
import com.example.mywatchface.CENTER_CIRCLE
import com.example.mywatchface.CENTER_GAP_AND_CIRCLE_RADIUS
import com.example.mywatchface.FRACTION_ANGLE
import com.example.mywatchface.HOURS
import com.example.mywatchface.HOURS_ALPHA
import com.example.mywatchface.HOURS_HAND_LENGTH
import com.example.mywatchface.HOURS_IN_ANALOG_CLOCK
import com.example.mywatchface.HOUR_HAND_OFFSET
import com.example.mywatchface.HOUR_STROKE_WIDTH
import com.example.mywatchface.INACTIVE
import com.example.mywatchface.INNER_TICK_RADIUS_OFFSET
import com.example.mywatchface.MIDDLE
import com.example.mywatchface.MILLISECONDS
import com.example.mywatchface.MINUTES_ALPHA
import com.example.mywatchface.MINUTES_HAND_LENGTH
import com.example.mywatchface.MINUTE_STROKE_WIDTH
import com.example.mywatchface.OFFSET
import com.example.mywatchface.ONE_ROUND
import com.example.mywatchface.RGB_POSSIBLE_VALUES
import com.example.mywatchface.SECONDS_ALPHA
import com.example.mywatchface.SECONDS_HAND_LENGTH
import com.example.mywatchface.SECOND_TICK_STROKE_WIDTH
import com.example.mywatchface.SHADOW_RADIUS
import com.example.mywatchface.ZERO_FLOAT
import com.example.mywatchface.mvp.WatchFaceContract
import java.util.Calendar
import java.util.Random
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

class WatchFaceView: WatchFaceContract.WatchFaceView {

    private var mCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
    private var mCenterX: Float = ZERO_FLOAT
    private var mCenterY: Float = ZERO_FLOAT
    private var mSecondHandLength: Float = ZERO_FLOAT
    private var sMinuteHandLength: Float = ZERO_FLOAT
    private var sHourHandLength: Float = ZERO_FLOAT
    /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
    private var mWatchHandColor: Int = 0
    private var mWatchHandHighlightColor: Int = 0
    private var mWatchHandShadowColor: Int = 0
    private lateinit var mHourPaint: Paint
    private lateinit var mMinutePaint: Paint
    private lateinit var mSecondPaint: Paint
    private lateinit var mTickAndCirclePaint: Paint
    private lateinit var mBackgroundPaint: Paint
    private lateinit var mBackgroundBitmap: Bitmap
    private lateinit var mGrayBackgroundBitmap: Bitmap
    private var colorBackground = 0
    private var mLowBitAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

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
        mHourPaint = mWatchPaint(mWatchHandColor, HOUR_STROKE_WIDTH)
        mMinutePaint = mWatchPaint(mWatchHandColor, MINUTE_STROKE_WIDTH)
        mSecondPaint = mWatchPaint(mWatchHandHighlightColor,SECOND_TICK_STROKE_WIDTH)
        mTickAndCirclePaint = mWatchPaint(mWatchHandColor, SECOND_TICK_STROKE_WIDTH)
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

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mCenterX = width / MIDDLE
        mCenterY = height / MIDDLE
        mSecondHandLength = (mCenterX * SECONDS_HAND_LENGTH).toFloat()
        sMinuteHandLength = (mCenterX * MINUTES_HAND_LENGTH).toFloat()
        sHourHandLength = (mCenterX * HOURS_HAND_LENGTH).toFloat()
        val scale = width.toFloat() / getMBackgroundBitmap().width.toFloat()
        mBackgroundBitmap = Bitmap.createScaledBitmap(
            mBackgroundBitmap,
            (mBackgroundBitmap.width * scale).toInt(),
            (mBackgroundBitmap.height * scale).toInt(),
            true
        )
        if (!mBurnInProtection && !mLowBitAmbient) {
            initGrayBackgroundBitmap()
        }
    }

    override fun setAlphas(inMuteMode: Boolean) {
        mHourPaint.alpha = if (inMuteMode) HOURS_ALPHA else ALPHA
        mMinutePaint.alpha = if (inMuteMode) MINUTES_ALPHA else ALPHA
        mSecondPaint.alpha = if (inMuteMode) SECONDS_ALPHA else ALPHA
    }

    private fun mWatchPaint(colorWatchHand: Int, width: Float) = Paint().apply {
        color = colorWatchHand
        strokeWidth = width
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor)
    }

    private fun updateInAmbientMode() {
        mHourPaint.color = Color.WHITE
        mMinutePaint.color = Color.WHITE
        mSecondPaint.color = Color.WHITE
        mTickAndCirclePaint.color = Color.WHITE
        mHourPaint.isAntiAlias = INACTIVE
        mMinutePaint.isAntiAlias = INACTIVE
        mSecondPaint.isAntiAlias = INACTIVE
        mTickAndCirclePaint.isAntiAlias = INACTIVE
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
        mHourPaint.isAntiAlias = ACTIVE
        mMinutePaint.isAntiAlias = ACTIVE
        mSecondPaint.isAntiAlias = ACTIVE
        mTickAndCirclePaint.isAntiAlias = ACTIVE
        mHourPaint.setShadowLayer(SHADOW_RADIUS, CENTER_CIRCLE, CENTER_CIRCLE, mWatchHandShadowColor)
        mMinutePaint.setShadowLayer(SHADOW_RADIUS, CENTER_CIRCLE, CENTER_CIRCLE, mWatchHandShadowColor)
        mSecondPaint.setShadowLayer(SHADOW_RADIUS, CENTER_CIRCLE, CENTER_CIRCLE, mWatchHandShadowColor)
        mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, CENTER_CIRCLE, CENTER_CIRCLE, mWatchHandShadowColor)
    }

    override fun getColorBackground() : Int = colorBackground
    override fun setColorBackground(color: Int) { colorBackground = color }
    override fun getMCalendar(): Calendar = mCalendar
    override fun setMCalendar(calendar: Calendar) { mCalendar = calendar }
    override fun setTimeZone(timeZone: TimeZone) { mCalendar.timeZone = timeZone }
    override fun getMBackgroundBitmap(): Bitmap = mBackgroundBitmap
    override fun getMLowBitAmbient(): Boolean  = mLowBitAmbient
    override fun setMLowBitAmbient(state: Boolean) { mLowBitAmbient = state }
    override fun getMBurnInProtection(): Boolean = mBurnInProtection
    override fun setMBurnInProtection(state: Boolean) { mBurnInProtection = state }
    override fun getMBackgroundPaint(): Paint  = mBackgroundPaint
    override fun getMGrayBackgroundBitmap(): Bitmap = mGrayBackgroundBitmap
}