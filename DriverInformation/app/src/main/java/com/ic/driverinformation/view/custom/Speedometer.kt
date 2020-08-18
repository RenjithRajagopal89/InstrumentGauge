package com.ic.driverinformation.view.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.ic.driverinformation.R


class Speedometer: View {
    companion object {
        const val DEFAULT_INDENTATION = 20
        const val DEFAULT_MAX_SPEED = 300f
        const val DEFAULT_CURRENT_SPEED = 0f
        const val DEFAULT_NEEDLE_COLOR = Color.RED
        const val DEFAULT_RIM_COLOR = Color.GRAY
        const val DEFAULT_LABEL_COLOR = Color.WHITE
        const val DEFAULT_SIZE = 300
        const val DEFAULT_LABEL_SIZE = 30f
        const val DEFAULT_READING_SIZE = 85f
        const val DEFAULT_SPEED_UNIT = "km/h"
        val DEFAULT_FONT: Typeface = Typeface.SANS_SERIF
    }

    var maxSpeed: Float = DEFAULT_MAX_SPEED
    var currentSpeed: Float = DEFAULT_CURRENT_SPEED
        set(value) {
            if(value > this.maxSpeed)
                field = maxSpeed
            else if(value < 0)
                field = 0f
            else
                field = value
            postInvalidate()
        }
    var indentation: Int = DEFAULT_INDENTATION
    var needleColor: Int = DEFAULT_NEEDLE_COLOR
    var rimColor: Int = DEFAULT_RIM_COLOR
    var labelColor: Int = DEFAULT_LABEL_COLOR
    var labelSize: Float = DEFAULT_LABEL_SIZE
    var readingSize: Float = DEFAULT_READING_SIZE
    lateinit var font: String

    private lateinit var rimPath: Path
    private lateinit var rimPathPaint: Paint
    private lateinit var rim: RectF
    private var rimRadius: Float = 0.0f
    private var rimCenterX: Float = 0.0f
    private var rimCenterY: Float = 0.0f
    private var needleLength: Float = 0.0f
    private lateinit var needlePaint: Paint

    private lateinit var labelPath: Path
    private lateinit var labelPathPaint: Paint
    private lateinit var readingPath: Path
    private lateinit var readingPathPaint: Paint
    private var speedUnit: String = DEFAULT_SPEED_UNIT

    private lateinit var typeface: Typeface

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.Speedometer, 0 , 0)

        try {
            maxSpeed = typedArray.getFloat(R.styleable.Speedometer_maxSpeed, DEFAULT_MAX_SPEED)
            currentSpeed = typedArray.getFloat(R.styleable.Speedometer_currentSpeed, DEFAULT_CURRENT_SPEED)
            indentation = typedArray.getInt(R.styleable.Speedometer_indentation, DEFAULT_INDENTATION)
            needleColor = typedArray.getColor(R.styleable.Speedometer_needleColor, DEFAULT_NEEDLE_COLOR)
            rimColor = typedArray.getColor(R.styleable.Speedometer_rimColor, DEFAULT_RIM_COLOR)
            labelColor = typedArray.getColor(R.styleable.Speedometer_labelColor, DEFAULT_LABEL_COLOR)
            labelSize = typedArray.getFloat(R.styleable.Speedometer_labelSize, DEFAULT_LABEL_SIZE)
            readingSize = typedArray.getFloat(R.styleable.Speedometer_readingSize, DEFAULT_READING_SIZE)
            speedUnit = typedArray.getString(R.styleable.Speedometer_speedUnit).toString()
            if (speedUnit == "null") speedUnit = DEFAULT_SPEED_UNIT

            font = typedArray.getString(R.styleable.Speedometer_fontName).toString()
            typeface = if (font == "null") {
                DEFAULT_FONT
            } else {
                Typeface.createFromAsset(context.assets, "fonts/$font")
            }
        } finally {
            typedArray.recycle()
        }

        init()
    }

    private fun init() {
        rim = RectF()
        rimPath = Path()

        rimPathPaint = Paint()
        rimPathPaint.setColor(rimColor)
        rimPathPaint.style = Paint.Style.FILL_AND_STROKE
        rimPathPaint.strokeWidth = 35f
        rimPathPaint.isAntiAlias = true

        labelPath = Path()

        labelPathPaint = Paint()
        labelPathPaint.setColor(labelColor)
        labelPathPaint.textSize = labelSize
        labelPathPaint.strokeWidth = 10f
        labelPathPaint.typeface = typeface
        rimPathPaint.isAntiAlias = true

        needlePaint = Paint()
        needlePaint.setColor(needleColor)
        needlePaint.style = Paint.Style.FILL_AND_STROKE
        needlePaint.strokeWidth = 5f
        rimPathPaint.isAntiAlias = true

        readingPathPaint = Paint(labelPathPaint)
        readingPathPaint.textSize = readingSize
        readingPathPaint.strokeWidth = 20f
    }

    override fun onDraw(canvas: Canvas?) {
        drawRim(canvas)
        drawLabels(canvas)
        drawNeedle(canvas)
        drawReading(canvas)
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        rimRadius = if (width > height) {
            (height / 2.2).toFloat()
        } else {
            (width / 2.2).toFloat()
        }

        needleLength = rimRadius * 0.85f

        rim.set(rimCenterX - rimRadius, rimCenterY - rimRadius,
            rimCenterX + rimRadius, rimCenterY + rimRadius)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)

        val chosenDimension = chosenWidth.coerceAtMost(chosenHeight)
        if (chosenWidth < chosenHeight) {
            rimCenterX = (chosenWidth / 2).toFloat()
            rimCenterY = (chosenDimension * 0.75).toFloat()
        } else {
            rimCenterX = (chosenDimension / 2).toFloat()
            rimCenterY = (chosenHeight * 0.75).toFloat()
        }

        setMeasuredDimension(chosenDimension, chosenDimension)
    }

    private fun chooseDimension(mode: Int, size: Int): Int {
        return if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            size
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            getPreferredSize()
        }
    }

    // in case there is no size specified
    private fun getPreferredSize(): Int {
        return DEFAULT_SIZE
    }

    private fun drawRim(canvas: Canvas?) {
        rimPath.reset()
        for (i in -180..0 step 2) {
            rimPath.addArc(rim, i.toFloat(), 1f)
        }
        canvas?.drawPath(rimPath, rimPathPaint)
    }

    private fun drawLabels(canvas: Canvas?) {
        canvas?.save()
        canvas?.rotate(-180f, rimCenterX, rimCenterY)
        val halfCircumference = rimRadius * Math.PI

        for (i in 0..maxSpeed.toInt() step indentation) {
            labelPath.addCircle(rimCenterX, rimCenterY, rimRadius, Path.Direction.CW)
            canvas?.drawTextOnPath(String.format("%d", i),
                labelPath,
                ((i * halfCircumference/maxSpeed).toFloat()),
                60f,
                labelPathPaint)
        }

        canvas?.restore()
    }

    private fun drawNeedle(canvas: Canvas?) {

        canvas?.drawCircle(rimCenterX, rimCenterY, 10f, needlePaint)

        var angle: Double = currentSpeed / maxSpeed * 180.toDouble()
        if (angle > 90) {
            angle = 180 - angle
            canvas?.drawLine(
                rimCenterX,
                rimCenterY,
                (rimCenterX + Math.abs(Math.cos(Math.toRadians(angle)) * needleLength)).toFloat(),
                (rimCenterY - Math.abs(Math.sin(Math.toRadians(angle)) * needleLength)).toFloat(),
                needlePaint
            )
        } else {
            canvas!!.drawLine(
                rimCenterX,
                rimCenterY,
                (rimCenterX - Math.abs(Math.cos(Math.toRadians(angle)) * needleLength)).toFloat(),
                (rimCenterY - Math.abs(Math.sin(Math.toRadians(angle)) * needleLength)).toFloat(),
                needlePaint
            )
        }
    }

    private fun drawReading(canvas: Canvas?) {
        readingPath = Path()
        val reading: String = String.format("%d", currentSpeed.toInt())
        var widths = FloatArray(reading.length)
        readingPathPaint.getTextWidths(reading, widths)
        var totalWidth = 0f
        for (width in widths) {
            totalWidth += width
        }

        readingPath.moveTo(rimCenterX - totalWidth / 2, (rimCenterY * .65).toFloat())
        readingPath.lineTo(rimCenterX + totalWidth / 2, (rimCenterY * .65).toFloat())
        canvas?.drawTextOnPath(reading, readingPath, 0f, 0f, readingPathPaint)

        val speedUnitPath = Path()
        widths = FloatArray(speedUnit.length)
        labelPathPaint.getTextWidths(speedUnit, widths)
        var size = 0f
        for (width in widths) {
            size += width
        }

        speedUnitPath.moveTo(rimCenterX - size / 2, (rimCenterY * 0.70).toFloat())
        speedUnitPath.lineTo(rimCenterX + size / 2, (rimCenterY * 0.70).toFloat())
        canvas?.drawTextOnPath(speedUnit, speedUnitPath, 0f, 0f, labelPathPaint)
    }
}