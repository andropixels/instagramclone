package com.rahul.instaclone.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.rahul.instaclone.R
import com.theartofdev.edmodo.cropper.CropImage


class Addpost : AppCompatActivity() {
    lateinit var publishbtn:Button
    lateinit var postimage:ImageView
    lateinit var caption:EditText
    lateinit  var imageuri:Uri
    lateinit var myimageurl:String


    var Storageref:StorageReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)
        publishbtn=findViewById(R.id.publish)
        postimage=findViewById(R.id.image)
        caption=findViewById(R.id.writehere)

        postimage.setOnClickListener {
            CropImage.activity().setAspectRatio(1,1).start(this@Addpost)

        }

        publishbtn.setOnClickListener {
            uploadpost()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
               if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode== Activity.RESULT_OK&&data!=null){

                   val url=CropImage.getActivityResult(data)
                   imageuri=url.uri
                   postimage.setImageURI(imageuri)
               }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadpost() {
        Storageref=FirebaseStorage.getInstance().reference.child("userpost")
        val fileref=Storageref?.child(System.currentTimeMillis().toString() +".jpg")
        val uploadTask: StorageTask<*>
        uploadTask=fileref!!.putFile(imageuri) /* putting file under the above fileref of current user which is actully under the folder profile picture */
        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->

            if (task.isSuccessful){
                task.exception?.let {
                    throw it

                }
            }

            return@Continuation fileref.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri> { task ->

            if (task.isSuccessful){
                //task contains the resultof the above class which Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
               val ref=FirebaseDatabase.getInstance().reference.child("post")
               val  postid= ref.push().key
                val downloadurl=task.result
                myimageurl=downloadurl.toString()
                val postmap=HashMap<String,Any>()
                postmap["postid"]=postid!!
                postmap["publisher"]=FirebaseAuth.getInstance().currentUser!!.uid
                postmap["caption"]=caption.text.toString()
                postmap["imageo"]=myimageurl
                ref.child(postid).updateChildren(postmap)

                val intent=Intent(this, MainActivity::class.java)
                startActivity(intent)
//                                  progresdialog.dismiss()
            }

        })


    }
}