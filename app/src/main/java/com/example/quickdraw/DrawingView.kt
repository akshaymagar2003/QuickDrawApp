package com.example.quickdraw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context,ats:AttributeSet) : View(context,ats){

    private var mDrawPath:CustomPath?=null
//path is basically used for drawing diffrent geometric paths consisting of straight line segments and cubic cureves(ctrl+click)
    private var mcanvasBitmap: Bitmap?=null
    private var mDrawPaint:Paint?=null
    private var mCanvasPaint:Paint?=null
//    below to variable is showing the default values;
    private var mBrushSize:Float=0.toFloat()
    private var color= Color.BLACK
    private var canvas:Canvas?=null

    init{
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint=Paint()
        mDrawPath=CustomPath(color,mBrushSize)
      mDrawPaint!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint= Paint(Paint.DITHER_FLAG)
        mBrushSize=20.toFloat()

  // we are left with the initilisation of mcanvasBitmap and canvas so lets initized it first
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mcanvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mcanvasBitmap!!)

    }
//    There is inbuild method ondraw
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    //parameters are given like from where to start
     canvas!!.drawBitmap(mcanvasBitmap!!,0f,0f,mCanvasPaint)
    if(!mDrawPath!!.isEmpty){
mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
        mDrawPaint!!.color=mDrawPath!!.color
        canvas.drawPath(mDrawPath!!,mDrawPaint!!)
    }




    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
       val touchX=event?.x
        val touchY=event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                mDrawPath!!.color=color
                mDrawPath!!.brushThickness=mBrushSize
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!,touchY!!)

            }
            MotionEvent.ACTION_MOVE->{
                mDrawPath!!.lineTo(touchX!!,touchY!!)

            }
            MotionEvent.ACTION_UP->{
                mDrawPath=CustomPath(color,mBrushSize)
            }
            else->{return false}
        }

         invalidate()
        return true

    }



    internal inner class CustomPath(var color:Int,var brushThickness:Float) : Path(){

    }
}