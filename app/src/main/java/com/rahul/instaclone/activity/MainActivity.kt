package com.rahul.instaclone.activity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.rahul.instaclone.R
import com.rahul.instaclone.fragment.Homefragment
import com.rahul.instaclone.fragment.Notifiactionfrafment
import com.rahul.instaclone.fragment.Profilefragment
import com.rahul.instaclone.fragment.Searchfragment

//lateinit var framelayout:FrameLayout

class MainActivity : AppCompatActivity() {


          internal var selaectedfragment:Fragment?=null
      private val nav=BottomNavigationView.OnNavigationItemSelectedListener {

               when(it.itemId){
                   R.id.navigation_home ->{
                       selaectedfragment=Homefragment()
                   }

                   R.id.navigation_add ->{
                               startActivity(Intent(this@MainActivity, Addpost::class.java))
                   }
                   R.id.navigation_search ->{
                       selaectedfragment=Searchfragment()

                   }
                   R.id.naigation_profile ->{
                       selaectedfragment=Profilefragment()

                   }
                   R.id.navigation_fav ->{
                       selaectedfragment=Notifiactionfrafment()
                   }
               }
          if (selaectedfragment!=null){

              supportFragmentManager.beginTransaction().replace(

                  R.id.framelayout_main_activity,
                  selaectedfragment!!// the  sea;ected fragment value willl come from the above when statement ,i.e on which menu  item user  has clicked

              ).commit()


          }
          return@OnNavigationItemSelectedListener true
           }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
       // framelayout=findViewById(R.id.framelayout_main_activity)

       navView.setOnNavigationItemSelectedListener(nav)

        supportFragmentManager.beginTransaction().replace(
            R.id.framelayout_main_activity,Homefragment()

        ).commit()
    }

    override fun onBackPressed() {
      val transac=supportFragmentManager.findFragmentById(R.id.framelayout_main_activity)
        when(transac){

            Homefragment()->{
                onDestroy()
            }

            else->super.onBackPressed()
        }
        


    }
}