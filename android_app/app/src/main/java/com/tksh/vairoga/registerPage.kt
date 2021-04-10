package com.tksh.vairoga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class registerPage : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)
        val btn_register = findViewById<Button>(R.id.btn_registerUser)

         val tv_username = findViewById<EditText>(R.id.et_registerEmail)
         val tv_password = findViewById<EditText>(R.id.et_registerPassword)
         val tv_name = findViewById<EditText>(R.id.et_registerName)
         val tv_aadhar = findViewById<EditText>(R.id.et_registerAadhar)
         val tv_phone = findViewById<EditText>(R.id.et_registerPhone)
        btn_register.setOnClickListener {
            if (tv_username.text.toString().isEmpty()) {
                tv_username.error = "Please enter email"
                tv_username.requestFocus()

            }

            if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
                tv_username.error = "Please enter valid email"
                tv_username.requestFocus()

            }

            if (tv_password.text.toString().isEmpty()) {
                tv_password.error = "Please enter password"
                tv_password.requestFocus()

            }
            else {


                auth.createUserWithEmailAndPassword(
                    tv_username.text.toString(),
                    tv_password.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            val user = auth.currentUser
                            user!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        database.child("users").child(user.uid).child("userData").setValue(PatientUserData(tv_name.text.toString(),tv_username.text.toString(),user.uid, tv_aadhar.text.toString(),
                                            tv_phone.text.toString(),"PATIENT",100
                                        ))
                                        val Intent = Intent(this, loginPage::class.java)
                                        Intent.putExtra(LoginOptions.LOGIN_KEY, "PATIENT")
                                        startActivity(Intent)
                                        finish()
                                    }

                                }

                        } else {
                            Toast.makeText(
                                baseContext, "Sign Up failed. Try again after some time.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }
        }
    }


}