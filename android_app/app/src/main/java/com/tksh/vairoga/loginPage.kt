package com.tksh.vairoga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class loginPage : AppCompatActivity() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    var option_in:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        val tv_username = findViewById<EditText>(R.id.et_loginEmail)
        val tv_password = findViewById<EditText>(R.id.et_loginPassword)
        option_in = intent.getStringExtra(LoginOptions.LOGIN_KEY)
        val registerText = findViewById<TextView>(R.id.tv_register)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        val registerBtn = findViewById<TextView>(R.id.tv_register)

        if (option_in == "DOCTOR"){
            registerText.visibility = View.GONE
        } else{
            registerText.visibility = View.VISIBLE
        }
        registerBtn.setOnClickListener {
            startActivity(Intent(this,registerPage::class.java))
        }
        loginBtn.setOnClickListener {
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
            firebaseAuth.signInWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("what","this working?")
                        val user = firebaseAuth.currentUser
                        updateUI(user,option_in)
                    } else {
                        Toast.makeText(this,"Cannot Login",Toast.LENGTH_SHORT).show()
                        updateUI(null,option_in)
                        // ...
                    }

                    // ...
                }
        }
    }
    private fun updateUI(currentUser: FirebaseUser?,option:String?) {
        if(currentUser!=null){
            if(currentUser.isEmailVerified){

                if (option=="DOCTOR") {
                    val intent1 = Intent(this, doctorDashboard::class.java)
                    intent1.putExtra(LoginOptions.LOGIN_KEY,option)
                    Log.d("w","Working?")
                    startActivity(intent1)

                } else {
                    val intent1 = Intent(this, patientDashboard::class.java)
                    intent1.putExtra(LoginOptions.LOGIN_KEY,option)
                    startActivity(intent1)
                    
                }




            finish()
            }else{
                Toast.makeText(baseContext, "Please verify your email address",
                    Toast.LENGTH_SHORT).show()
            }

        }

        else{

            //Toast.makeText(baseContext, "Authentication failed.",
            //  Toast.LENGTH_SHORT).show()
        }

    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        val what = intent.getStringExtra(LoginOptions.LOGIN_KEY)
        if (currentUser!=null) {
            updateUI(currentUser, what)
        }
    }
}