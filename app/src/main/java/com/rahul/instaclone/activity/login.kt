package com.rahul.instaclone.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rahul.instaclone.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        createnewaccount.setOnClickListener {
            startActivity(Intent(this,signup::class.java))
        }

        btnlogin.setOnClickListener {
            val progressdialog= ProgressDialog(this@login)
            progressdialog.setTitle("sigining up...")
            progressdialog.setMessage("this may take while")
            progressdialog.setCanceledOnTouchOutside(false)
            progressdialog.show()
            val email=edtemail.text.toString()
            val password=edtpassword.text.toString()
            val mauth=FirebaseAuth.getInstance()
            mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
                if (task.isSuccessful){
                    progressdialog.dismiss()
                    val intent= Intent(this,login::class.java)
                    startActivity(intent)

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                    finish()
                }else{
                    val toaast=task.exception
                    Toast.makeText(this,"$toaast", Toast.LENGTH_LONG).show()

                }

            }
        }

    }

    override fun onStart() {

        if (FirebaseAuth.getInstance().currentUser!=null){


            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        }
        super.onStart()
    }
}