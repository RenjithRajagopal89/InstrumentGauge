package com.ic.driverinformation.view.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.ic.driverinformation.R

class Oodometer: View {

    companion object {
        val DEFAULT_FONT: Typeface = Typeface.SANS_SERIF
        val DEFAULT_UNIT: String = "km"
        val DEFAULT_TEXT_COLOR = Color.WHITE
        val DEFAULT_TEXT_SIZE = 40f
        const val DEFAULT_SIZE = 100
        const val DEFAULT_LABEL_SIZE = 50f
    }

    lateinit var font: String
    var distanceTravelled: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }
    var distanceUnit: String = DEFAULT_UNIT
    var textColor: Int = DEFAULT_TEXT_COLOR
    var textSize: Float = DEFAULT_TEXT_SIZE

    private lateinit var textPaint: Paint
    private var viewHeight: Int = 0
    private var viewWidth: Int = 0

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

        val typedArray: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.Oodometer, 0 , 0)
        val typeface: Typeface

        try {
            font = typedArray.getString(R.styleable.Oodometer_readingFont).toString()
            distanceTravelled = typedArray.getFloat(R.styleable.Oodometer_distanceTravelled, 0f)
            distanceUnit = typedArray.getString(R.styleable.Oodometer_distanceUnit).toString()
            textColor = typedArray.getInt(R.styleable.Oodometer_textColor, DEFAULT_TEXT_COLOR)
            textSize = typedArray.getFloat(R.styleable.Oodometer_textSize, DEFAULT_TEXT_SIZE)

            typeface = if (font == "null") {
                DEFAULT_FONT
            } else {
                Typeface.createFromAsset(context.assets, "fonts/$font")
            }

            if (distanceUnit == "null") distanceUnit = DEFAULT_UNIT

        } finally {
            typedArray.recycle()
        }

        textPaint = Paint()
        textPaint.color = textColor
        textPaint.textSize = textSize
        textPaint.strokeWidth = 10f
        textPaint.typeface = typeface
        textPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {

        val path = Path()
        val text: String = String.format("%s %s", distanceTravelled, distanceUnit)

        val widths = FloatArray(text.length)
        textPaint.getTextWidths(text, widths)
        var totalWidth = 0f
        for (width in widths) {
            totalWidth += width
        }

        val widthDiff = viewWidth - totalWidth

        if (widthDiff > 0) {
            path.moveTo(widthDiff / 2, (viewHeight / 2).toFloat())
            path.lineTo(viewWidth - widthDiff / 2, (viewHeight / 2).toFloat())
            canvas?.drawTextOnPath(text, path, 0f, 0f, textPaint)
        } else {
            path.moveTo(0f, viewHeight.toFloat())
            path.lineTo(totalWidth, viewHeight.toFloat())
            canvas?.drawTextOnPath(text, path, 0f, 0f, textPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewHeight = h
        viewWidth = w
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)
        setMeasuredDimension(chosenWidth, chosenHeight)
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
}