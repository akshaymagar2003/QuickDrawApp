package com.example.quickdraw

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private var DrawingView:DrawingView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawingView=findViewById(R.id.drawingView)
        DrawingView!!.setSizeForBrush(20.toFloat())
    }

}