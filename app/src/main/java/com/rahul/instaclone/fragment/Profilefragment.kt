package com.rahul.instaclone.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.rahul.instaclone.R
import com.rahul.instaclone.activity.editprofileactivity
import com.rahul.instaclone.model.usermodel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profilefragment.*
import kotlinx.android.synthetic.main.fragment_profilefragment.view.*
import kotlinx.android.synthetic.main.fragment_searchfragment.view.*
import java.lang.System.load


class Profilefragment : Fragment() {
  private lateinit var profileid:String
    lateinit var profilepicture:ImageView
    lateinit var username:TextView
    lateinit var bio:TextView
    lateinit var editprofilebtn:Button
    val FirebaseUser= FirebaseAuth.getInstance().currentUser
        lateinit var arrar:MutableList<usermodel>
     var data:usermodel?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflate = inflater.inflate(R.layout.fragment_profilefragment, container, false)
        profilepicture=inflate.findViewById(R.id.deafualtprofilepicture)
        username=inflate.findViewById(R.id.usernametext)
        bio=inflate.findViewById(R.id.biotext)
        editprofilebtn=inflate.findViewById(R.id.btneditprofile)


     arrar= arrayListOf()

        val  pref=context?.getSharedPreferences("pref",Context.MODE_PRIVATE)
             if (pref!=null){
                     this.profileid= pref.getString("key","none").toString()
             }

        FirebaseUser?.uid.let {
            if (it==profileid){
                inflate.btneditprofile.text="Edit profile"

            }
            else{
                followbtnstatus(profileid, editprofilebtn)
            }
        }

        editprofilebtn.setOnClickListener {


            when(inflate.btneditprofile.text){
                "Edit profile"->{startActivity(Intent(context, editprofileactivity::class.java))}
                "follow"->{ FirebaseUser?.uid.let {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(it.toString()).child("following")
                        .child(profileid).setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                FirebaseDatabase.getInstance().getReference().child("follow").child(profileid).child("followers")
                                    .child(it.toString()).setValue(true)

                            }

                        }
                }}
                "following"->{FirebaseUser?.uid.let {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(it.toString()).child("following")
                        .child(profileid).removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                FirebaseDatabase.getInstance().getReference().child("follow").child(profileid).child("followers")
                                    .child(it.toString()).removeValue()

                            }

                        }
                }}


            }

        }

        FirebaseUser?.uid.let {
            if (it==profileid){
                getcurrentuserinfo()
            }else{
                getuserinfo()

            }

        }
        getfollowers()
        getfollowing()

        return inflate
    }

    private fun followbtnstatus(profileid: String, btneditprofile: Button?) {

        FirebaseUser?.uid.let {
            val ref=FirebaseDatabase.getInstance().getReference().child("follow")
                .child(it.toString()).child("following")

                ref.addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                           if (snapshot.child(profileid).exists()){
                               btneditprofile?.text="following"

                           } else {
                               btneditprofile?.text="follow"
                           } }
                }) }



    }
    private fun getfollowers(){
        val ref=FirebaseDatabase.getInstance().getReference().child("follow")
            .child(profileid).child("followers")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {

               val   followercount=snapshot!!.childrenCount.toString()

                followernumber?.setText(followercount)

            }

        })
    }
    private fun getfollowing(){

        val ref=FirebaseDatabase.getInstance().getReference().child("follow")
            .child(profileid).child("following")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val   followingcount=snapshot!!.childrenCount.toString()


                followingnumber?.setText(followingcount.toString())

            }

        })

    }

    private fun getuserinfo(){
        val ref=FirebaseDatabase.getInstance().getReference().child("users")
            .child(profileid)
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
             Toast.makeText(activity as Context,"$error",Toast.LENGTH_LONG).show()

            }

            override fun onDataChange(snapshot: DataSnapshot) {


                val dat =snapshot.getValue(usermodel::class.java)

                username.setText(dat?.username)
                bio.setText(dat?.bio)
//                Picasso.get().load(dat?.image).error(R.drawable.profilepicture).into(image)
             //   Picasso.get().load("gs://insta-e1ed9.appspot.com/profilepicture/P5JfnYntUGbJsveLp5jKE2b5epk2.jpg").error(R.drawable.profilepicture).into(profilepicture)
                Glide.with(activity!! as  Context)
                    .load(dat?.image)
                    .error(R.drawable.profilepicture)
                    .into(profilepicture)
                toolbarusername.setText(dat?.username).toString()


            }
        })
    }

    private fun getcurrentuserinfo(){
        val ref=FirebaseDatabase.getInstance().getReference().child("users")
            .child(FirebaseUser!!.uid)
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity as Context,"$error",Toast.LENGTH_LONG).show()

            }

            override fun onDataChange(snapshot: DataSnapshot) {


                val dat =snapshot.getValue(usermodel::class.java)

                username.setText(dat?.username)
                bio.setText(dat?.bio)
//                Picasso.get().load(dat?.image).error(R.drawable.profilepicture).into(image)
                //   Picasso.get().load("gs://insta-e1ed9.appspot.com/profilepicture/P5JfnYntUGbJsveLp5jKE2b5epk2.jpg").error(R.drawable.profilepicture).into(profilepicture)
                Glide.with( context!!)
                    .load(dat?.image)
                    .error(R.drawable.profilepicture)
                    .into(profilepicture)
                toolbarusername.setText(dat?.username).toString()


            }
        })



    }



}