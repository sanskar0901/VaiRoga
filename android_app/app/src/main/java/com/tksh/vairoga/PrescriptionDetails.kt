package com.tksh.vairoga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class PrescriptionDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription_details)
        val date_tv = findViewById<TextView>(R.id.date_display)
        val symptoms_tv = findViewById<EditText>(R.id.symptoms_display)
        val tests_tv = findViewById<EditText>(R.id.test_display)

        date_tv.text = intent.getStringExtra(PatientDetails.PRESCRIPTION_DATE_KEY)
        symptoms_tv.setText(intent.getStringExtra(PatientDetails.PRESCRIPTION_SYMPTIONS_KEY))
        tests_tv.setText(intent.getStringExtra(PatientDetails.PRESCRIPTION_TESTS_KEY))

    }
}