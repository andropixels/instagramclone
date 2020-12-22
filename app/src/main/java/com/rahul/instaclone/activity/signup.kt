package com.rahul.instaclone.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rahul.instaclone.R
import kotlinx.android.synthetic.main.activity_signup.*

class signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        alreadyhaveaccount.setOnClickListener {
            startActivity(Intent(this,login::class.java))
        }

        btnsignup.setOnClickListener {
            val progressdialog=ProgressDialog(applicationContext)
            progressdialog.setTitle("sigining up...")
            progressdialog.setMessage("this may take while")
            progressdialog.setCanceledOnTouchOutside(false)
            progressdialog.show()
            userinfo()
        }
    }

    private fun userinfo():Boolean {

       val fullname=edtFULLname.text.toString()
        val username=edtUSERNAME.text.toString()
       val email=edtEMAIL.text.toString()
        val password= edtPASSWORD.text.toString()

        //for firebase authntication
        val firebaseauth=FirebaseAuth.getInstance()
        firebaseauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if (task.isSuccessful){
                val progressdialog=ProgressDialog(this@signup)
                progressdialog.setTitle("sigining up...")
                progressdialog.setMessage("this may take while")
                progressdialog.setCanceledOnTouchOutside(false)
                progressdialog.show()
                //if user with entered email and password get athunticated we will get to n]know through the task (add on complete listener)
                //now if the user is authnticated we wil post data on realtime database

                storedata(fullname,username,email,password,progressdialog)
            }
            else{

                val toaast=task.exception
                Toast.makeText(this,"$toaast",Toast.LENGTH_LONG).show()
            }

        }
        return true

    }

    private fun storedata(fullname: String, username: String, email: String, password: String,progressDialog: ProgressDialog) {



        //for storing/retriving data from firebase create the database reference variable
        val dataref=FirebaseDatabase.getInstance().getReference().child("users")////here we have added the first field of the json tree i.e users
        //get current user id for the each child of the users in json tree
        val currentuserid=FirebaseAuth.getInstance().currentUser!!.uid
        //now we need to store data somewgere like in lists may be,bcoz we cant store datafields directly inside the database ref variable
        //so for fast data retriving and and storing we will use map here basically a hash map
        val usermap=HashMap<String,Any>()
            //user map is the hashmap now we can store data inside it with key value pair
        usermap["uid"]=currentuserid
        usermap["fullname"]=fullname
        usermap["username"]=username
        usermap["bio"]="this is my deafult bio"
        usermap["image"]="gs://insta-e1ed9.appspot.com/images/profile picture.png"
        usermap["email"]=email
        usermap["password"]=password
        dataref.child(currentuserid).setValue(usermap).addOnCompleteListener { task ->

            if (task.isSuccessful){

                     val currentuser= FirebaseAuth.getInstance().currentUser!!.uid
                                      FirebaseDatabase.getInstance().getReference().child("follow").child(currentuser)
                                          .child("following").child(currentuser).setValue(true)

                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)

                 finish()



                progressDialog.dismiss()



            }
            else{

                val toaast=task.exception
                Toast.makeText(this,"$toaast",Toast.LENGTH_LONG).show()
            }
        }


    }


}