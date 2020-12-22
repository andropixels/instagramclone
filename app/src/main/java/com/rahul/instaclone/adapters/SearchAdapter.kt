package com.rahul.instaclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rahul.instaclone.R
import com.rahul.instaclone.fragment.Profilefragment
import com.rahul.instaclone.model.usermodel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SearchAdapter(var context:Context, var userlist: List<usermodel>):RecyclerView.Adapter<SearchAdapter.searchHolder>(),Filterable {
    val firebaseUser:FirebaseUser?=FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchHolder {
              val view=LayoutInflater.from(context).inflate(R.layout.searchitem,parent,false )
               return searchHolder(view)
    }

    override fun getItemCount(): Int {
             return userlist.size

    }

    override fun onBindViewHolder(holder: searchHolder, position: Int) {

        val userposition=userlist[position]
        holder.username.text=userposition.username
     //   Picasso.get().load("gs://insta-e1ed9.appspot.com/profilepicture/P5JfnYntUGbJsveLp5jKE2b5epk2.jpg").error(R.drawable.profilepicture).into(holder.profilepicture)
        Glide.with(context)
            .load(userposition.image)
            .error(R.drawable.profilepicture)
            .into(holder.profilepicture)
        holder.followbuttonn.setOnClickListener {
            if (holder.followbuttonn.text.toString()=="Follow"){
                firebaseUser?.uid.let {
                    FirebaseDatabase.getInstance().getReference().child("follow")
                        .child(it.toString()).child("following")
                        .child(userposition.uid).setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                FirebaseDatabase.getInstance().getReference().child("follow")
                                    .child(userposition.uid).child("followers")
                                    .child(it.toString()).setValue(true)
                                    }
                                }
                        }
                        } else{

                            firebaseUser?.uid.let {
                                FirebaseDatabase.getInstance().getReference().child("follow")
                                    .child(it.toString()).child("following")
                                    .child(userposition.uid).removeValue().addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            FirebaseDatabase.getInstance().getReference().child("follow")
                                                .child(userposition.uid).child("followers")
                                                .child(it.toString()).removeValue().addOnCompleteListener {

                                                }
                                        }
                                    }
                            } }
        }
        changebutton(userposition.uid,holder.followbuttonn)



        holder.itemView.setOnClickListener {

                   val  pref=context.getSharedPreferences("pref",Context.MODE_PRIVATE).edit()
                   pref.putString("key",userposition.uid).apply()
                   (context as FragmentActivity).supportFragmentManager.beginTransaction()
                       .replace(R.id.framelayout_main_activity,Profilefragment()).commit()

                /*   val  pref1=context.getSharedPreferences("pref1",Context.MODE_PRIVATE).edit()
                   pref1.putString("key1",userposition.uid).apply()*/
               }

    }




    class searchHolder( itemView: View):RecyclerView.ViewHolder(itemView){

        val profilepicture=itemView.findViewById<CircleImageView>(R.id.searchitemprofilepicture)
        val username=itemView.findViewById<TextView>(R.id.usernamesearchitem)
        val followbuttonn=itemView.findViewById<Button>(R.id.searchitemfollowbutton)





    }

    override fun getFilter(): Filter {
        return object: Filter(){
            val newlist:MutableList<usermodel>?=null

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                for (row in userlist) {
                    var listt=  row.username.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(
                        Locale.ROOT
                    )
                    )

                           if (listt){
                               newlist?.add(row)
                                FilterResults().values=newlist
                                 FilterResults().count=newlist!!.size
                           }

                }
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                    FilterResults().values=newlist
                notifyDataSetChanged()
            }
        }
    }
    private fun changebutton(uid: String, followbuttonn: Button?) {


        firebaseUser?.uid.let {
            val ref=   FirebaseDatabase.getInstance().getReference().child("follow").child(it.toString())
                .child("following")

            ref.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child(uid).exists()){
                        followbuttonn?.text="Following"

                    }else{

                        followbuttonn?.text="Follow"

                    }
                }
            })
        }

    }
}
