package com.tksh.vairoga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class SearchPatient : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance().reference
    private val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_patient)

        val searchButton = findViewById<ImageView>(R.id.searchButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val aadharEditText = findViewById<EditText>(R.id.searchAadharNumber)

        clearButton.visibility = View.GONE

        searchButton.setOnClickListener {
            if (aadharEditText.text.toString()==""){
                Toast.makeText(this,"Enter Aadhar Number",Toast.LENGTH_SHORT).show()
            } else{
                clearButton.visibility = View.VISIBLE
                val searchText = aadharEditText.text.toString()
                database.child("users").addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.children.forEach{
                                val data = it.getValue(PatientUserData::class.java)
                                if(data!=null){
                                    if(data.type == "PATIENT" && data.aadhar.equals(searchText)){
                                        adapter.add(aadharViewholder(data))
                                    }
                                }
                            }
                            findViewById<RecyclerView>(R.id.aadhar_rv_display).adapter = adapter
                            adapter.setOnItemClickListener { item, view ->
                                val dataToBePassed = item as aadharViewholder
                                val intent = Intent(view.context,PatientDetails::class.java)
                                intent.putExtra(doctorDashboard.PATIENT_NAME_KEY,dataToBePassed.aadhar.name)
                                intent.putExtra(doctorDashboard.PATIENT_EMAIL_KEY,dataToBePassed.aadhar.email)
                                intent.putExtra(doctorDashboard.PATIENT_AADHAR_KEY,dataToBePassed.aadhar.aadhar)
                                intent.putExtra(doctorDashboard.PATIENT_PHONE_KEY,dataToBePassed.aadhar.phone)
                                intent.putExtra(doctorDashboard.PATIENT_CREDIT_KEY,dataToBePassed.aadhar.credit)
                                intent.putExtra(doctorDashboard.PATIENT_UID_KEY,dataToBePassed.aadhar.uid)
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //TODO("Not yet implemented")
                    }

                })
            }

            clearButton.setOnClickListener {
                aadharEditText.setText("")
                adapter.clear()
                clearButton.visibility = View.GONE
            }

        }
    }
    class aadharViewholder(val aadhar:PatientUserData):Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val text_view = viewHolder.itemView.findViewById<TextView>(R.id.aadhar_number_tv)
            text_view.text = aadhar.name
        }

        override fun getLayout() = R.layout.aadhar_search_layout

    }
}