package com.rahul.instaclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rahul.instaclone.R
import android.widget.ImageView
import android.widget.TextView
import android.content.Context
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rahul.instaclone.model.postmodel
import com.rahul.instaclone.model.usermodel
import de.hdodenhof.circleimageview.CircleImageView


class homefeedadapter(var context:Context, var postslist: ArrayList<postmodel>):RecyclerView.Adapter<homefeedadapter.homefeeedviewholder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): homefeeedviewholder {

                                      //inflate item view file here
        val view=LayoutInflater.from(context).inflate(R.layout.homefragment,parent,false )
        return homefeeedviewholder(view)
    }

    override fun getItemCount(): Int {


         return  postslist.size

    }

    override fun onBindViewHolder(holder: homefeeedviewholder, position: Int) {

        val pos=postslist[position]
                          userinfo(holder.pro,holder.use,pos.publisher)

           Glide.with(context).load(pos.imageo).error(R.drawable.profilepicture).into(holder.posthomepragment)
          holder.caption.setText(pos.caption)




    }
    private fun userinfo( pro:CircleImageView,use: TextView, publisher: String) {

        val ref=FirebaseDatabase.getInstance().getReference().child("users").child(publisher)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {




                    if (snapshot.exists()){

                        val data=snapshot.getValue(usermodel::class.java)
                        use.text=data?.username
                        Glide.with(context).load(data?.image).error(R.drawable.profilepicture).into(pro)


                    }



            }

        })

    }

    class homefeeedviewholder(itemView:View):RecyclerView.ViewHolder(itemView){


        val posthomepragment=itemView.findViewById<ImageView>(R.id.posthomepragment)
        val caption=itemView.findViewById<EditText>(R.id.cption)
        val  pro=itemView.findViewById<CircleImageView>(R.id.pro)
        val use=itemView.findViewById<TextView>(R.id.use)


    }


}