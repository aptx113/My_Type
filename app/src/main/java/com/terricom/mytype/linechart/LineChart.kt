package com.terricom.mytype.linechart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.terricom.mytype.R
import java.util.*


/**
 * Created by Terri.
 * Graph | Copyrights 2019-08-31.
 */
@SuppressLint("ViewConstructor")
class LineChart : View {

    private var mPaddingTop: Float = 40f
    var mPaddingRight: Float = 40f
    var mPaddingLeft: Float = 40f
    var mPaddingBottom: Float = 90f
    var maxValue: Long = 0
    var marginTop: Int = 50
    var legendArray: Array<String>? = null

    var lineColor: Int = 0
    var bgColor: Int = 0
    var typeFace: Typeface? = null

    private var xLength: Int = 0
    private var yLength: Int = 0

    private var chartXLength: Int = 0
    private var chartYLength: Int = 0

    private var p = Paint()
    private var pCircle = Paint()
    private var pCircleBG = Paint()
    private var pLine = Paint()
    private var pBaseLine = Paint()
    private var pBaseLineX = Paint()
    private var pBaseLineY = Paint()
    private var pMarkText = Paint()
    private var pMarkTextY = Paint()


    private var chartEntities: List<ChartEntity>? = null

    constructor(context: Context?) : super(context) {
        initialize(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {

        attrs?.let {
            val typeArray = context.obtainStyledAttributes(attrs, R.styleable.LineChart)

            this.bgColor = typeArray.getColor(R.styleable.LineChart_chart_bg_color, Color.parseColor("#209387"))
            this.lineColor = typeArray.getColor(R.styleable.LineChart_chart_line_color, Color.parseColor("#32FFFFFF"))
            this.mPaddingTop = typeArray.getDimension(R.styleable.LineChart_chart_padding_top, 20f)
            this.mPaddingRight = typeArray.getDimension(R.styleable.LineChart_chart_padding_right, 20f)
            this.mPaddingBottom = typeArray.getDimension(R.styleable.LineChart_chart_padding_bottom, 20f)
            this.mPaddingLeft = typeArray.getDimension(R.styleable.LineChart_chart_padding_left, 20f)

            typeArray.recycle()
        }

        val graph1 = floatArrayOf(113000f, 183000f, 188000f, 695000f, 324000f, 230000f, 188000f)
        val graph2 = floatArrayOf(0f, 245000f, 1011000f, 1000f, 0f, 0f, 47000f)

        val arrGraph = ArrayList<ChartEntity>()
        arrGraph.add(ChartEntity(Color.GRAY, graph1))
        arrGraph.add(ChartEntity(Color.WHITE, graph2))
        setList(arrGraph)
        invalidate()

    }


    fun setList(list: List<ChartEntity>) {
        this.chartEntities = null
        invalidate()
        this.chartEntities = list
        val maxes = ArrayList<Float>()
        val copy = mutableListOf<Float>()
        for (lineGraph in chartEntities!!) {
            val copies =
                lineGraph.values.copyOf(lineGraph.values.size)
            Arrays.sort(copies)
            maxes.add(copies[copies.size - 1])

        }
        this.maxValue = (Collections.max(maxes) as Float).toLong()
    }

//    fun setList(list: List<ChartEntity>) {
//        this.chartEntities = null
//        invalidate()
//        this.chartEntities = list
//        val maxes = ArrayList<Float>()
//        for (lineGraph in chartEntities!!) {
//            val copies =
//                lineGraph.values.copyOf(lineGraph.values.size)
//            Arrays.sort(copies)
//            maxes.add(copies[copies.size - 1])
//        }
//        this.maxValue = (Collections.max(maxes) as Float).toLong()
//    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }

        if (chartEntities == null) {
            canvas.drawColor(bgColor)

        }

        this.initializePaint()

        this.xLength = (width - (mPaddingLeft + mPaddingRight)).toInt()
        this.yLength = (height - (mPaddingBottom + mPaddingTop + marginTop)).toInt()

        this.chartXLength = (width - (mPaddingLeft + mPaddingRight)).toInt()
        this.chartYLength = (height - (mPaddingTop + mPaddingBottom)).toInt()

        canvas.drawColor(bgColor)


        val graphCanvasWrapper = GraphCanvasWrapper(canvas, this.width, this.height, this.mPaddingLeft.toInt(), this.mPaddingBottom.toInt())
        graphCanvasWrapper.drawLine(0.0f, 0.0f, chartXLength.toFloat(), 0.0f, pBaseLine)
        graphCanvasWrapper.drawLine(0.0f, 0.0f, chartYLength.toFloat(), 0.0f, pBaseLine)


        var newX: Float
        var newY: Float
        val gap = chartXLength / (chartEntities!![0].values.size - 1)
        val yGap = (yLength / 10).toFloat()

        for (i in 0 until 11) {
            val gapY = yGap * i

            newY = yGap * i
            graphCanvasWrapper.drawLine(0.0f, newY, chartXLength.toFloat(), newY, pBaseLine)

            drawGraph(graphCanvasWrapper)
            drawXText(graphCanvasWrapper)
        }
    }


    private fun drawXText(graphCanvas: GraphCanvasWrapper) {

        if (legendArray == null || legendArray.isNullOrEmpty() || chartEntities == null) {
            return
        }
        var x: Float
        var y: Float
        val xGap = (xLength / (chartEntities!![0].values.size - 1)).toFloat()
        val yGap = (yLength / 10).toFloat()

        for (i in 0 until 11){
            val rect = Rect()

            val text = i.toString()
            pMarkTextY.measureText(text)
            pMarkTextY.textSize = 20f
            pMarkTextY.typeface = typeFace

            y = yGap * i
            x = rect.exactCenterX() - 40

            val degree: Int = 0
            val px = (-(20 + rect.width())).toFloat()
            val py = y + rect.exactCenterY() + y + 10

            graphCanvas.drawText(
                text,
                x,
                y,
                pMarkTextY,
                degree.toFloat(),
                px,
                py
            )


        }

        for (i in chartEntities!![0].values.indices) {
            val rect = Rect()
            val text = legendArray!![i]
            pMarkText.measureText(text)
            pMarkText.textSize = 20f
            pMarkText.typeface = typeFace



            x = xGap * i
            y = (-(20 + rect.height())).toFloat()

            pMarkText.getTextBounds(text, 0, text.length, rect)
            val degree: Int = -45
            val px = rect.exactCenterX() + x + 10
            val py = y + rect.exactCenterY() - 10

            graphCanvas.drawText(
                text,
                x - rect.width() / 2,
                y,
                pMarkText,
                degree.toFloat(),
                px,
                py
            )
        }
    }

    private fun drawGraph(graphCanvasWrapper: GraphCanvasWrapper) {

        if (chartEntities == null) {
            return
        }

        this.pCircleBG.color = bgColor

        for (m in chartEntities!!.indices) {
            val linePath = GraphPath(width, height, mPaddingLeft.toInt(), mPaddingBottom.toInt())
            var first = false

            var x: Float
            var y: Float

            this.p.color = chartEntities!![m].color
            this.pCircle.color = chartEntities!![m].color

            val xGap = xLength / (chartEntities!![m].values.size - 1)

            for (j in chartEntities!![m].values.indices) {

                if (j < chartEntities!![m].values.size) {
                    x = (xGap * j).toFloat()
                    y = yLength * chartEntities!![m].values[j] / maxValue
                    if (!first) {
                        linePath.moveTo(x, y)
                        first = true
                    } else {
                        linePath.lineTo(x, y)
                    }
                }
            }

            graphCanvasWrapper.canvas?.drawPath(linePath, p)

            for (t in chartEntities!![m].values.indices) {
                if (t < chartEntities!![m].values.size) {
                    x = (xGap * t).toFloat()
                    y = yLength * chartEntities!![m].values[t] / maxValue
                    graphCanvasWrapper.drawCircle(x, y, 2.0f, pCircle)
                    graphCanvasWrapper.drawCircle(x, y, 0.5f, pCircleBG)
                }
            }
        }


    }

    private fun initializePaint() {
        p = Paint()
        p.flags = Paint.ANTI_ALIAS_FLAG
        p.isAntiAlias = true
        p.isFilterBitmap = true
        p.color = Color.BLUE
        p.strokeWidth = 10f
        p.isAntiAlias = true
        p.strokeCap = Paint.Cap.ROUND
        p.style = Paint.Style.STROKE

        pCircle = Paint()
        pCircle.flags = Paint.ANTI_ALIAS_FLAG
        pCircle.isAntiAlias = true
        pCircle.isFilterBitmap = true
        pCircle.color = Color.BLUE
        pCircle.strokeWidth = 20f
        pCircle.style = Paint.Style.STROKE

        pCircleBG = Paint()
        pCircleBG.isAntiAlias = true
        pCircleBG.isFilterBitmap = true
        pCircleBG.color = bgColor
        pCircleBG.strokeWidth = 10f
        pCircleBG.style = Paint.Style.FILL_AND_STROKE

        pLine = Paint()
        pLine.flags = Paint.ANTI_ALIAS_FLAG
        pLine.isAntiAlias = true //text anti alias
        pLine.isFilterBitmap = true // bitmap anti alias
        pLine.shader = LinearGradient(
            0f,
            300f,
            0f,
            0f,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            Shader.TileMode.MIRROR
        )

        pBaseLine = Paint()
        pBaseLine.flags = Paint.ANTI_ALIAS_FLAG
        pBaseLine.isAntiAlias = true
        pBaseLine.isFilterBitmap = true
        pBaseLine.color = lineColor
        pBaseLine.strokeWidth = 2f

        pBaseLineX = Paint()
        pBaseLineX.flags = Paint.ANTI_ALIAS_FLAG
        pBaseLineX.isAntiAlias = true
        pBaseLineX.isFilterBitmap = true
        pBaseLineX.color = Color.BLACK
        pBaseLineX.strokeWidth = 2f
        pBaseLineX.style = Paint.Style.STROKE

        pBaseLineY = Paint()
        pBaseLineY.flags = Paint.ANTI_ALIAS_FLAG
        pBaseLineY.isAntiAlias = true
        pBaseLineY.isFilterBitmap = true
        pBaseLineY.color = Color.BLACK
        pBaseLineY.strokeWidth = 2f
        pBaseLineY.style = Paint.Style.STROKE

        pMarkText = Paint()
        pMarkText.flags = Paint.ANTI_ALIAS_FLAG
        pMarkText.isAntiAlias = true
        pMarkText.color = Color.BLACK
    }
}