package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.projemanag.R
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.Board
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.security.Permission
import java.security.Permissions
import java.util.jar.Manifest

class CreateBoardActivity : BaseActivity(){
    companion object{
        const val select_photo_requestcode=3
        const val read_external = 4
    }
    private lateinit var mUsername:String
    private  var boardimageuri:Uri?=null
    private var mboardimageurl:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setupActionBar()

        if(intent.hasExtra("name"))
        {
            mUsername = intent.getStringExtra("name").toString()
        }
        iv_board_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@CreateBoardActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                selectphoto()
            }
            else
            {
                ActivityCompat.requestPermissions(this@CreateBoardActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    read_external)
            }

        }



    }

    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        val board = Board(
            et_board_name.text.toString(),
            mboardimageurl,
            mUsername,
            assignedUsersArrayList
        )
        FirestoreClass().createboard(this,board)

    }
    private fun uploadBoardImage()
    {
        showprocessdialog("Please wait")
        if(boardimageuri!=null)
        {
            var sref:StorageReference = FirebaseStorage.getInstance().reference.child("Board_Image"
            +System.currentTimeMillis()+getBoardExtension(boardimageuri!!))
            sref.putFile(boardimageuri!!).addOnSuccessListener {
                it->
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    mboardimageurl = uri.toString()
                    createBoard()
                }
            }


        }

    }
    private fun getBoardExtension(uri: Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))

    }

    fun boardCreatedSuccessfull(){
        hideprocessdialog()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== read_external) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.isNotEmpty()) {
                selectphoto()
            } else {
                Toast.makeText(
                    this,
                    "oops, you just denied the permission open settings give the permission for the app",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }




    private fun setupActionBar()
    {
        setSupportActionBar(toolbar_create_board_activity)
        val actionbar = supportActionBar
        if(actionbar!=null)
        {
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_create_board_activity.setOnClickListener{
            super.onBackPressed()
        }
    }
    private fun selectphoto()
    {
        val galleryintent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryintent, select_photo_requestcode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== select_photo_requestcode && resultCode== Activity.RESULT_OK && data!!.data!=null)
        {
            boardimageuri = data.data
            try {
                Glide
                    .with(this@CreateBoardActivity)
                    .load(Uri.parse(boardimageuri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_board_user_image);

            }catch (e: IOException)
            {
                e.printStackTrace()
            }

        }
    }
    fun boardCreatedSuccessfully() {
        hideprocessdialog()

        finish()
    }
}