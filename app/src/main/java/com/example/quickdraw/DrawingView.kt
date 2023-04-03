package com.example.quickdraw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.nio.file.Paths

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
    private  val paths= ArrayList<CustomPath>()
    private  val Undopaths= ArrayList<CustomPath>()

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
//        mBrushSize=10.toFloat()
//we don't need the above value because we have created the set brush function below
  // we are left with the initilisation of mcanvasBitmap and canvas so lets initized it first

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mcanvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mcanvasBitmap!!)

    }
//    There is inbuild method ondraw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    //parameters are given like from where to start
     canvas.drawBitmap(mcanvasBitmap!!,0f,0f,mCanvasPaint)
    for (path in paths){
        mDrawPaint!!.strokeWidth=path!!.brushThickness
        mDrawPaint!!.color=path!!.color
        canvas.drawPath(path,mDrawPaint!!)
    }
    if(!mDrawPath!!.isEmpty){
mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
        mDrawPaint!!.color=mDrawPath!!.color
        canvas.drawPath(mDrawPath!!,mDrawPaint!!)
    }




    }
    fun OnclickedUndo(){
        if(paths.size>0){
            Undopaths.add(paths.removeAt(paths.size-1))
            invalidate()
        }
    }
    fun OnclickedRedu(){
        if(Undopaths.size>0){
            paths.add(Undopaths.removeAt(Undopaths.size-1))
            invalidate()
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
                paths.add(mDrawPath!!)
                mDrawPath=CustomPath(color,mBrushSize)
            }
            else->{return false}
        }

         invalidate()
        return true

    }

fun setSizeForBrush(newSize:Float){
    mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
          newSize,resources.displayMetrics
        )
    mDrawPaint!!.strokeWidth=mBrushSize
}
fun setColorForBrush(col:String){
    color=Color.parseColor(col)
    mDrawPaint!!.color=color
}
    internal inner class CustomPath(var color:Int,var brushThickness:Float) : Path(){

    }
}