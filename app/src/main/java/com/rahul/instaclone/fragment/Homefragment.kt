package com.rahul.instaclone.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.rahul.instaclone.R
import com.rahul.instaclone.adapters.homefeedadapter
import com.rahul.instaclone.model.postmodel


class Homefragment : Fragment() {



    private var adapter:homefeedadapter?=null
   private var  list:MutableList<postmodel>? =null
    private var followinglist:MutableList<postmodel>?=null
    val currentuser= Firebase.auth.currentUser!!.uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //this class cant surviev without the View below
        val inflate = inflater.inflate(R.layout.fragment_homefragment, container, false)
        var recyclerView:RecyclerView?=null
        recyclerView=inflate.findViewById(R.id.recyclerviewhomefragment)
        val linearLayoutManager=LinearLayoutManager(context)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager=LinearLayoutManager(activity as Context)
        list=ArrayList()
        adapter= context?.let { homefeedadapter(it,list as ArrayList<postmodel>) }
        recyclerView.adapter=adapter

        followinginfo()

        return inflate
    }
    private fun followinginfo() {
        followinglist=ArrayList()
        val ref=FirebaseDatabase.getInstance().getReference().child("follow").child(currentuser).child("following")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                           (followinglist as ArrayList<String>)?.clear()

                    for (id in snapshot.children){

                               id.key?.let { (followinglist as ArrayList<String>)?.add(it) }
                    }
                    postinfo()

                }
            }
        })
    }
    private fun postinfo()  {

        val postref=FirebaseDatabase.getInstance().getReference().child("post")
        postref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                list?.clear()

                    for (id in snapshot.children){
                        val data =id?.getValue(postmodel::class.java)

                        for (i in followinglist as ArrayList<String> ){

                            if (data!!.publisher==i){
                                list?.add(data)

                            }
                            adapter!!.notifyDataSetChanged()
                        }

                    }



            }


        })

    }


}