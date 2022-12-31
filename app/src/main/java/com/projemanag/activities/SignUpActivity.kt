package com.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.projemanag.R
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setuptoolbar()
    }
    private fun setuptoolbar()
    {
        setSupportActionBar(toolbar_sign_up_activity)
        val actionbar = supportActionBar
        if(actionbar!=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_sign_up_activity.setOnClickListener {
            onBackPressed()
        }
        btn_sign_up.setOnClickListener{
            registerUser()
        }
    }
    private fun registerUser()
    {
        val name = et_name.text.toString()
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        if(validateform(name,email,password))
        {
            showprocessdialog("Please wait")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                hideprocessdialog()
                if (task.isSuccessful) {
                    val firebaseuser: FirebaseUser = task.result!!.user!!
                    val registeremail: String? = firebaseuser.email
                    val user = User(firebaseuser.uid,name,registeremail!!)
                    FirestoreClass().registerUser(this@SignUpActivity,user)

                }
                else
                {
                    Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }


        }
    }
    fun userRegisteredSuccess(){
        Toast.makeText(this,"You have successfully registered",Toast.LENGTH_LONG).show()
        hideprocessdialog()
        //signout from firebase of user
        FirebaseAuth.getInstance().signOut()
        finish()
    }
    private fun validateform(name:String,email:String,password:String):Boolean
    {
        return when{
            //For string.isEmpty(), a null string value will throw a NullPointerException
            //
            //TextUtils will always return a boolean value.
            TextUtils.isEmpty(name)->{
                showsnackerror("Please enter your name")
                false
            }
            TextUtils.isEmpty(email)->{
                showsnackerror("Please enter your email")
                false
            }
            TextUtils.isEmpty(password)->{
                showsnackerror("please enter your password")
                false
            }
            else ->{
                true
            }

        }
    }



}