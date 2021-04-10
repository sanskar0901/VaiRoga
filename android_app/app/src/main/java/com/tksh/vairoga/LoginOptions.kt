package com.tksh.vairoga

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class LoginOptions : AppCompatActivity() {

    private val CALL_PERMISSION_CODE = 101
    private var onBackPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_options)
        val doctorBtn = findViewById<Button>(R.id.btn_doctorLogin)
        val patientBtn = findViewById<Button>(R.id.btn_patientLogin)
        val emergencyBtn = findViewById<Button>(R.id.btn_emergency)


        checkPermission(Manifest.permission.CALL_PHONE,CALL_PERMISSION_CODE)


        doctorBtn.setOnClickListener {
            val Intent = Intent(this, loginPage::class.java)
            Intent.putExtra(LOGIN_KEY, "DOCTOR")
            startActivity(Intent)

        }
        patientBtn.setOnClickListener {
            val Intent = Intent(this, loginPage::class.java)
            Intent.putExtra(LOGIN_KEY, "PATIENT")
            startActivity(Intent)

        }
        emergencyBtn.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:01123978046")
            startActivity(callIntent)
        }

    }
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@LoginOptions, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@LoginOptions, arrayOf(permission),
                    requestCode)
        } else {
            Toast.makeText(this@LoginOptions,
                    "Call Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }
    companion object{
        val LOGIN_KEY = "LOGIN_KEY"
    }

    override fun onBackPressed() {
        if (onBackPressed){
        finishAffinity()
    }else{
            onBackPressed = true
            Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed(Runnable { onBackPressed = false }, 2000)
    }
    }
}
