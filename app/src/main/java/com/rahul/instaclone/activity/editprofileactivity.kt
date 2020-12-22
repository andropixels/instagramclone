package com.rahul.instaclone.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.rahul.instaclone.R
import com.rahul.instaclone.model.usermodel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_profilefragment.*


class editprofileactivity : AppCompatActivity() {
    private lateinit var check:String
    lateinit var savebutton:ImageView
    lateinit var cancelbtn:ImageView
    lateinit var logout:Button
    lateinit var fullname:EditText
    lateinit var username:EditText
    lateinit var bio:EditText
    lateinit var profileid:String
    lateinit var changepropicture:TextView
    lateinit var profilepicture:ImageView
    lateinit var imageuri:Uri
    private lateinit var myimageurl:String
    private  var firebasestorageref:StorageReference?=null
    val firebaseuser= Firebase.auth.currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofileactivity)
        savebutton=findViewById(R.id.save_editprofile)
        cancelbtn=findViewById(R.id.cancel_editprofile)
        logout=findViewById(R.id.btnlogout)
         fullname=findViewById(R.id.youname)
        username=findViewById(R.id.username)
        bio=findViewById(R.id.bio_editprofileactivity)
        changepropicture=findViewById(R.id.editpropictire)
        profilepicture=findViewById(R.id.profilepicture_editactivity)
        check=""
        myimageurl=""
        val  pref=getSharedPreferences("pref",Context.MODE_PRIVATE)
        if (pref!=null){

            this.profileid=pref.getString("key","none").toString()

        }

        logout.setOnClickListener {
            val mauth=FirebaseAuth.getInstance()
            mauth.signOut()

            val intent= Intent(this@editprofileactivity,login::class.java)
            startActivity(intent)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            finish()

        }


               changepropicture.setOnClickListener {
                   check="ok"

                   CropImage.activity().setAspectRatio(1,1).start(this@editprofileactivity)
               }

        savebutton.setOnClickListener {

            if (check=="ok"){
                     updateprofilepictureandinfo()

            }
            else{updateuserinfo()}
        }
        userinfo()



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){

            val result=CropImage.getActivityResult(data)
            imageuri=result.uri
            profilepicture.setImageURI(imageuri)

        }
    }

    private fun userinfo() {
        val ref=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseuser)

        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {


                  val data=snapshot.getValue(usermodel::class.java)
                  fullname.setText(data?.fullname)
                  username.setText(data?.username)
                  bio.setText(data?.bio)
            //   Picasso.get().load(data?.image).error(R.drawable.profilepicture).into(profilepicture)
                Glide.with(applicationContext)
                    .load(data?.image)
                    .error(R.drawable.profilepicture)
                    .into(profilepicture)            }
        })
    }




    private fun updateprofilepictureandinfo() {
                  val  progresdialog=ProgressDialog(this)
        progresdialog.setTitle("updating")
        progresdialog.setMessage("this may take while")
        progresdialog.setCanceledOnTouchOutside(false)
        progresdialog.show()

        when{
            TextUtils.isEmpty(fullname.text.toString())->{ Toast.makeText(this,"enterfullname",Toast.LENGTH_LONG).show()}
            TextUtils.isEmpty (username.text.toString())->{Toast.makeText(this,"enterusername",Toast.LENGTH_LONG).show()}
            TextUtils.isEmpty (bio.text.toString())->{Toast.makeText(this,"enterbio",Toast.LENGTH_LONG).show()}

                else->{

                    //so we contian the image in  the imageuri now we are uploading it to the firebase storage
                    firebasestorageref=FirebaseStorage.getInstance().reference.child("profilepicture")//this will create the folder at the storage
                    val fileref=firebasestorageref?.child(firebaseuser +".jpg")
                    val uploadTask:StorageTask<*>
                    uploadTask=fileref!!.putFile(imageuri) /* putting file under the above fileref of current user which is actully under the folder profile picture */
                    uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>> { task ->

                        if (task.isSuccessful){
                            task.exception?.let {
                                throw it

                            }
                        }

                        return@Continuation fileref.downloadUrl
                    }).addOnCompleteListener {task ->

                            if (task.isSuccessful){
                                  //task contains the resultof the above class which Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
                                val downloadurl=task.result
                                  myimageurl=downloadurl.toString()
                                val usermap=HashMap<String,Any>()
                                usermap["uid"]=firebaseuser
                                usermap["fullname"]=fullname.text.toString()
                                usermap["username"]=username.text.toString()
                                usermap["bio"]=bio.text.toString()
                                usermap["image"]=myimageurl
                                val ref=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseuser)
                                ref.updateChildren(usermap)
                                val intent=Intent(this,MainActivity::class.java)
                                startActivity(intent)
//                                  progresdialog.dismiss()
                            }

                    }
                }
        }

    }

    private fun updateuserinfo() {


        val usermap=HashMap<String,Any>()
        usermap["uid"]=firebaseuser
        usermap["fullname"]=fullname.text.toString()
        usermap["username"]=username.text.toString()
        usermap["bio"]=bio.text.toString()
        if(fullname.text.toString()==""){
            Toast.makeText(this,"enter fullname",Toast.LENGTH_LONG).show()
        }
        else if (username.text.toString()==""){
            Toast.makeText(this,"enter username",Toast.LENGTH_LONG).show()

        }
        else if (bio.text.toString()==""){            Toast.makeText(this,"enter bio",Toast.LENGTH_LONG).show()
        }
        else{

            val ref=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseuser)
            ref.updateChildren(usermap)
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}
