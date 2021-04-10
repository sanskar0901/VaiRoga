package com.tksh.vairoga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val type = intent.getStringExtra(LoginOptions.LOGIN_KEY)
        val profile_display = findViewById<TextView>(R.id.profile_display)
        val name_display = findViewById<TextView>(R.id.name_display)
        val email_display = findViewById<TextView>(R.id.email_display)
        val phone_display = findViewById<TextView>(R.id.phone_display)
        val aadhar_display = findViewById<TextView>(R.id.aadhar_display)
        val name_tv = findViewById<TextView>(R.id.name_tv)
        val email_tv = findViewById<TextView>(R.id.email_tv)
        val phone_tv = findViewById<TextView>(R.id.phone_tv)
        val aadhar_tv = findViewById<TextView>(R.id.aadhar_tv)


        if(type == "DOCTOR"){
            profile_display.setTextColor(resources.getColor(R.color.purple))
            aadhar_display.visibility = View.GONE
            aadhar_tv.visibility = View.GONE
            database.child("users").child(currentUser!!.uid).child("userData").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    name_tv.text = snapshot.child("name").value.toString()
                    email_tv.text = snapshot.child("email").value.toString()
                    phone_tv.text = snapshot.child("phone").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }

            })
        }else{
            profile_display.setTextColor(resources.getColor(R.color.green))
            aadhar_display.visibility = View.VISIBLE
            aadhar_tv.visibility = View.VISIBLE
            Log.d("profile data",currentUser!!.uid)
            database.child("users").child(currentUser!!.uid).child("userData").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("profile data",snapshot.child("name").value.toString())
                    name_tv.text = snapshot.child("name").value.toString()
                    email_tv.text = snapshot.child("email").value.toString()
                    phone_tv.text = snapshot.child("phone").value.toString()
                    aadhar_tv.text = snapshot.child("aadhar").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }

            })
        }


    }


}