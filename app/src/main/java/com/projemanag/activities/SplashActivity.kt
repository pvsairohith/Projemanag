package com.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.projemanag.R
import com.projemanag.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //The Typeface class specifies the typeface and intrinsic style of a font
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name.typeface = typeface

        //run after the specified amount of time elapses
        Handler().postDelayed({
            val userid = FirestoreClass().getcurrentuserid()
            if(userid.isNotEmpty())
            {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            else
            {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }


            finish()
        }, 2500)


    }
}