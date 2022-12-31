package com.projemanag.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.R
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.User
import com.projemanag.utils.constants
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.security.Permission
import java.security.PermissionCollection
import java.security.Permissions
import java.util.jar.Manifest

class ProfileActivity : BaseActivity() {


    companion object{
        private const val Read_storage_permission_code=1
        private const val Pick_Image_Request_code = 2

    }
    private var mselectedimagefileuri:Uri?=null
    private lateinit var muserdetails:User
    private var mProfileImageURL:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupActionBar()
        FirestoreClass().signinUser(this)
        iv_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@ProfileActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                showimagechooser()
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Read_storage_permission_code)
            }
        }
        btn_update.setOnClickListener {
            if(mselectedimagefileuri!=null)
            {
                uploaduserimage()
            }
            else{
                showprocessdialog("Please Wait")
                updateuserprofiledata()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Read_storage_permission_code)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                showimagechooser()
            }
            else
            {
                Toast.makeText(this,"oops, you just denied the permission open settings give the permission for the app",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showimagechooser()
    {
        val galleryintent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryintent, Pick_Image_Request_code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== Pick_Image_Request_code && data!!.data!=null)
        {
            mselectedimagefileuri = data.data
            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(mselectedimagefileuri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_user_image)
            }
            catch(e:IOException)
            {
                e.printStackTrace()
            }
        }
    }
    private fun setupActionBar()
    {
        setSupportActionBar(toolbar_profile_activity)
        val actionbar = supportActionBar
        if(actionbar!=null)
        {
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_profile_activity.setOnClickListener{
            super.onBackPressed()
        }
    }
     fun updateProfileUserDetails(user: User)
    {
        muserdetails = user
        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)
        et_name.setText(user.name)
        et_email.setText(user.email)
        if(user.mobile!=0L)
        {
            et_mobile.setText(user.mobile.toString())
        }
    }

   private fun updateuserprofiledata()
    {
        val userhashmap = HashMap<String,Any>()
        if(mProfileImageURL!=null && mProfileImageURL!=muserdetails.image)
        {
            userhashmap[constants.Image]=mProfileImageURL


        }
        if(et_name.text.toString()!=muserdetails.name)
        {
            userhashmap[constants.Name]=et_name.text.toString()

        }
        if(et_mobile.text.toString()!=muserdetails.mobile.toString())
        {
            userhashmap[constants.Mobile]=et_mobile.text.toString().toLong()

        }

            FirestoreClass().updateUserProfileData(this@ProfileActivity,userhashmap)

    }
    private fun uploaduserimage()
    {
        showprocessdialog("Please Wait")
        if(mselectedimagefileuri!=null)
        {
            val sref:StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE"
            +System.currentTimeMillis()+getfileextension(mselectedimagefileuri))
            sref.putFile(mselectedimagefileuri!!).addOnSuccessListener {
                taskSnapShot->
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    mProfileImageURL = uri.toString()
                    updateuserprofiledata()

                }.addOnFailureListener {
                    Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                    hideprocessdialog()
                }
            }
        }
    }

    private fun getfileextension(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileUpdateSuccess(){
        hideprocessdialog()
        setResult(Activity.RESULT_OK)
        finish()
    }


}