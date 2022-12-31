package com.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.et_email
import org.w3c.dom.Text


class SignInActivity : BaseActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setuptoolbar()
        auth = FirebaseAuth.getInstance()
    }
    private fun setuptoolbar()
    {
        setSupportActionBar(toolbar_sign_in_activity)
        val actionbar = supportActionBar
        if(actionbar!=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_sign_in_activity.setOnClickListener{
            super.onBackPressed()
        }
        btn_sign_in.setOnClickListener {
            signinuser()
        }
    }
    fun signinsuccess(user: User)
    {
        hideprocessdialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()

    }

    private fun signinuser()
    {
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        if(validatedetails(email,password))
        {
            showprocessdialog("Please Wait For a While")
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                task->
                if(task.isSuccessful)
                {
                    FirestoreClass().signinUser(this)
                }
                else
                {
                    showsnackerror("check your email and password")
                }
            }
        }
    }
    private fun validatedetails(email:String,password:String):Boolean
    {
        return when{
            TextUtils.isEmpty(email)->{
                showsnackerror("Please enter your email")
                false
            }
            TextUtils.isEmpty(password)->{
                showsnackerror("please enter your password")
                false
            }
            else-> {
                true
            }
        }

    }
}