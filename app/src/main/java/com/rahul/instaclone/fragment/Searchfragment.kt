package com.rahul.instaclone.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.rahul.instaclone.R
import com.rahul.instaclone.adapters.SearchAdapter
import com.rahul.instaclone.model.usermodel
import java.lang.reflect.Array


class Searchfragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: SearchAdapter
    lateinit var edittext_search:EditText
    lateinit var data:MutableList<usermodel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflate = inflater.inflate(R.layout.fragment_searchfragment, container, false)
        data= arrayListOf()
//        adapter=SearchAdapter(activity as Context,data)
        recyclerView=inflate.findViewById(R.id.serachRecycler)
        edittext_search=inflate.findViewById(R.id.edittext_search);
         adapter= SearchAdapter(requireContext(),data)
         val dataref=FirebaseDatabase.getInstance().getReference().child("users")

            dataref.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(activity as Context, error.message,Toast.LENGTH_LONG).show()

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (datasnapshot in snapshot.children)    {
                        val value = datasnapshot.getValue(usermodel::class.java)
                        (data as ArrayList<usermodel>).add(value!!);
                    }
                    recyclerView.layoutManager=LinearLayoutManager(activity!! as Context)
                   adapter=SearchAdapter(activity!! as Context,data)
                    recyclerView.adapter=adapter
                    adapter.notifyDataSetChanged()

                }

            })


        edittext_search.addTextChangedListener ( object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {


                return

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                           return

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                adapter.getFilter().filter(s.toString())


            }

        }
        )
        return inflate
    }


}