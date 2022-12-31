package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object{
        const val MY_PROFILE_REQUEST_CODE=12
    }
    private lateinit var mUsername:String

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)


        // This is used to align the xml view to this class
        setContentView(R.layout.activity_main)
        setupActionBar()
        nav_view.setNavigationItemSelectedListener(this)
        FirestoreClass().signinUser(this)

        fab_create_board.setOnClickListener{
            val intent = Intent(this@MainActivity,CreateBoardActivity::class.java)
            intent.putExtra("name",mUsername)
            startActivity(intent)
        }
    }
    fun updateNavigationUserDetails(user: User)
    {
        mUsername = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image);

        tv_username.text= user.name
    }

    private fun setupActionBar()
    {
        //setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggelerDrawer()
        }

    }
    private fun toggelerDrawer()
    {
        if(drawer_layout!!.isDrawerOpen(GravityCompat.START))
        {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(drawer_layout!!.isDrawerOpen(GravityCompat.START))
        {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
            doublebacktoexit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== MY_PROFILE_REQUEST_CODE && resultCode==Activity.RESULT_OK )
        {
            FirestoreClass().signinUser(this)
        }
        else
        {
            Log.e("cancelled","cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this@MainActivity,ProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
           R.id.nav_sigin_out->
           {
               FirebaseAuth.getInstance().signOut()
               val intent = Intent(this@MainActivity,IntroActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
               startActivity(intent)
               finish()
           }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

