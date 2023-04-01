package com.example.quickdraw

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var DrawingView:DrawingView?=null
   lateinit var brush_button:ImageButton
   private lateinit var mImageButtonCurrentPaint:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawingView=findViewById(R.id.drawingView)
        DrawingView!!.setSizeForBrush(20.toFloat())
        brush_button=findViewById(R.id.ib_brush)
        brush_button.setOnClickListener{
            showBrushSizeDialog()
        }

        //The below code is useful to access the image button in linear layout by using indexing like an array

        val linearLayoutPaintColors=findViewById<LinearLayout>(R.id.ColorPallet)
        mImageButtonCurrentPaint=linearLayoutPaintColors[1] as ImageButton
   mImageButtonCurrentPaint!!.setImageDrawable(
       ContextCompat.getDrawable(this,R.drawable.pallet_pressed_color)
   )


    }
    private  fun showBrushSizeDialog(){
        val brushDialog= Dialog(this)
        //The below statement shows that how the dialog will look like
        brushDialog.setContentView(R.layout.dialog_brush_size_layout)
        brushDialog.setTitle("Brush Size: ")
        val smallbtn=brushDialog.findViewById<ImageButton>(R.id.id_small_brush)
        val mediumbtn=brushDialog.findViewById<ImageButton>(R.id.id_medium_brush)
        val largebtn=brushDialog.findViewById<ImageButton>(R.id.id_large_brush)
        smallbtn.setOnClickListener{
            DrawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
       mediumbtn.setOnClickListener{
            DrawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        largebtn.setOnClickListener{
            DrawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

fun paintClicked(view:View){
  if(view!=mImageButtonCurrentPaint){
      val colorbutton=view  as ImageButton
     val s= colorbutton.tag.toString()
      DrawingView?.setColorForBrush(s)


     colorbutton.setImageDrawable(
          ContextCompat.getDrawable(this,R.drawable.pallet_pressed_color)
      )
      mImageButtonCurrentPaint.setImageDrawable(
          ContextCompat.getDrawable(this,R.drawable.pallet_nomal_color)
      )
      mImageButtonCurrentPaint=view

  }
}
}