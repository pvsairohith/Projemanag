package com.projemanag.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import kotlinx.android.synthetic.main.dialogprocess.*

open class BaseActivity : AppCompatActivity() {

    private var doublebacktoexitpressedonce = false
    private lateinit var mprogress: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun showprocessdialog(text:String)
    {
        mprogress = Dialog(this)
        mprogress.setContentView(R.layout.dialogprocess)
        mprogress.tv_progress_text.text = text
        mprogress.show()
    }
    fun hideprocessdialog()
    {
        mprogress.dismiss()
    }
    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doublebacktoexit()
    {
        if(doublebacktoexitpressedonce)
        {
            super.onBackPressed()
        }
        this.doublebacktoexitpressedonce = true
        Toast.makeText(this,resources.getString(R.string.please_click_backtoexit),Toast.LENGTH_LONG).show()
        Handler().postDelayed({doublebacktoexitpressedonce = false},2000)
    }
    fun showsnackerror(message: String)
    {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        snackbar.show()

    }

}