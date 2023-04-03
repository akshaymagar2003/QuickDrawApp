package com.example.quickdraw

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.security.Permission

class MainActivity : AppCompatActivity() {
    private var DrawingView:DrawingView?=null
   lateinit var brush_button:ImageButton

   private lateinit var mImageButtonCurrentPaint:ImageButton
    val requestPermission:ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions->permissions.entries.forEach {
                val permissionName=it.key
            val isGranted=it.value

            if(isGranted){
                Toast.makeText(this@MainActivity,"Permission granted now you can read the storage files",Toast.LENGTH_LONG).show()
            }
            else{
                if(permissionName==Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this@MainActivity,"Oops you just denied the permission",Toast.LENGTH_LONG).show()

                }
            }
        }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawingView=findViewById(R.id.drawingView)
        DrawingView!!.setSizeForBrush(20.toFloat())
        brush_button=findViewById(R.id.ib_brush)
        brush_button.setOnClickListener{
            showBrushSizeDialog()
        }
        val ibGallary:ImageButton=findViewById(R.id.ib_background)
        ibGallary.setOnClickListener {
requestStoragePermission()
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

     private fun requestStoragePermission(){
             if(ActivityCompat.shouldShowRequestPermissionRationale(
                     this,Manifest.permission.READ_EXTERNAL_STORAGE
             )){
                 showRationaleDialog("Kids Drawing App","Kids Drawing App"+"needs to Access YOur External Storage")
             }else{
                 requestPermission.launch(arrayOf(
                     Manifest.permission.READ_EXTERNAL_STORAGE
                 ))
             }
     }
    private fun showRationaleDialog(title:String,message:String){
        val builder:AlertDialog.Builder=AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel")
            { dialog, _->dialog.dismiss()
            }
        builder.create().show()
    }
}