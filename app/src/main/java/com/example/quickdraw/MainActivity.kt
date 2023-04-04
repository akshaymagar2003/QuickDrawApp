package com.example.quickdraw
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class MainActivity : AppCompatActivity() {
    private var DrawingView:DrawingView?=null
    var customProgressDialog: Dialog? = null
   lateinit var brush_button:ImageButton
   val openGallaryLauncher: ActivityResultLauncher<Intent> =registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
       result ->
        if(result.resultCode== RESULT_OK && result.data!=null){
            val imageBackGround:ImageView=findViewById(R.id.iv_background)
          imageBackGround.setImageURI(result.data?.data)
        }
   }
   private lateinit var mImageButtonCurrentPaint:ImageButton
    val requestPermission:ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions->permissions.entries.forEach {
                val permissionName=it.key
            val isGranted=it.value

            if(isGranted){
                Toast.makeText(this@MainActivity,"Permission granted now you can read the storage files",Toast.LENGTH_LONG).show()
                val pickIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             openGallaryLauncher.launch(pickIntent)
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
        val ibUndo:ImageButton=findViewById(R.id.ib_Undo)
        ibUndo.setOnClickListener {
           DrawingView?.OnclickedUndo()
        }
        val ibRedo:ImageButton=findViewById(R.id.ib_redo)
        ibRedo.setOnClickListener {
            DrawingView?.OnclickedRedu()
        }
        val ibsave:ImageButton=findViewById(R.id.ib_save)
        ibsave.setOnClickListener {
    if(isReadStorageAllowed()){
        showProgressDialog()

        lifecycleScope.launch{
                 val flDrawingView:FrameLayout=findViewById(R.id.fl_Layout)
            val myBitmap:Bitmap=getBitmapFromView(flDrawingView)

            saveBitmapFile(myBitmap)


        }
    }
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
       private fun getBitmapFromView(view: View):Bitmap{
        val returnedBitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
       //All the drawn part
        val canvas =Canvas(returnedBitmap)
        val bgDrawable=view.background
        if(bgDrawable!=null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
              view.draw(canvas)
        return returnedBitmap
        }

private fun isReadStorageAllowed():Boolean{
    val result=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)

    return result == PackageManager.PERMISSION_GRANTED
}



       private fun requestStoragePermission(){
             if(ActivityCompat.shouldShowRequestPermissionRationale(
                     this,Manifest.permission.READ_EXTERNAL_STORAGE
             )){
                 showRationaleDialog("Kids Drawing App","Kids Drawing App"+"needs to Access YOur External Storage")
             }else{
                 requestPermission.launch(arrayOf(
                     Manifest.permission.READ_EXTERNAL_STORAGE
                 ,Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    private suspend fun saveBitmapFile(mBitmap: Bitmap?): String{
        var result = ""
        withContext(Dispatchers.IO){
            if (mBitmap != null) {
                try{
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    var f = File(externalCacheDir?.absoluteFile.toString() +
                            File.separator + "KidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png")

                    val name = "KidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png"
                    val relativeLocation = Environment.DIRECTORY_PICTURES + "/KidsDrawingApp"

                    val contentValues  = ContentValues().apply {
                        put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")

                        // without this part causes "Failed to create new MediaStore record" exception to be invoked (uri is null below)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.ImageColumns.RELATIVE_PATH, relativeLocation)
                        }
                    }

                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    var stream: OutputStream? = null
                    var uri: Uri? = null

                    try {
                        uri = contentResolver.insert(contentUri, contentValues)
                        if (uri == null){
                            throw IOException("Failed to create new MediaStore record.")
                        }

                        stream = contentResolver.openOutputStream(uri)
                        if (stream == null){
                            throw IOException("Failed to get output stream.")
                        }

                        if (!mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                            throw IOException("Failed to save bitmap.")
                        }
                        //this would open a gallery app
                        //   val intent = Intent()
                        //   intent.type = "image/*"
                        //   intent.action = Intent.ACTION_VIEW
                        //   intent.data = contentUri
                        //   startActivity(Intent.createChooser(intent, "Select Gallery App"))

                    }
                    catch(e: IOException) {
                        if (uri != null) {
                            contentResolver.delete(uri, null, null)
                        }
                        throw IOException(e)
                    }
                    finally {
                        stream?.close()
                    }
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result = f.absolutePath

                    runOnUiThread{
                        cancelProgressDialog()
                        if(result.isNotEmpty()){
                            //Toast.makeText(this@MainActivity,
                            //    "File saved successfully: $result", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this@MainActivity,
                                "File saved successfully: $relativeLocation/$name", Toast.LENGTH_SHORT).show()
//                            shareImage(FileProvider.getUriForFile(this@MainActivity,"com.example.quickdraw.fileprovider",result))
//                            shareImage(result)
                            setUpEnablingFeatures(FileProvider.getUriForFile(baseContext,"com.example.quickdraw.fileprovider",f))



                        }
                        else{
                            Toast.makeText(this@MainActivity,
                                "Something went wrong saving the file", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch(e:Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        customProgressDialog?.show()
    }
    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    private fun setUpEnablingFeatures(uri:Uri){


        val intent = Intent().apply {
            this.action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_STREAM, uri)
            this.type = "image/png"
        }
        startActivity(Intent.createChooser(intent, "Share image via "))

    }
}