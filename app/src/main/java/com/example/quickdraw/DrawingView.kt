package com.example.quickdraw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
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


    }

    internal inner class CustomPath(var color:Int,var brushThickness:Float) : Path(){

    }
}