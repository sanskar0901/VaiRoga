package com.tksh.vairoga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class PatientDetails : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)
        val name_display = findViewById<TextView>(R.id.patient_details_name)
        val email_display = findViewById<TextView>(R.id.patient_details_email)
        val aadhar_display = findViewById<TextView>(R.id.patient_details_Aadhar)
        val phone_display = findViewById<TextView>(R.id.patient_details_Phone)
        val precription_display = findViewById<RecyclerView>(R.id.rv_prescription)


        val Pname = "Name - ${intent.getStringExtra(doctorDashboard.PATIENT_NAME_KEY)}"
        val Pemail = "Email - ${intent.getStringExtra(doctorDashboard.PATIENT_EMAIL_KEY)}"
        val Paadhar = "Aadhar Number - ${intent.getStringExtra(doctorDashboard.PATIENT_AADHAR_KEY)}"
        val Pphone = "Phone Number - ${intent.getStringExtra(doctorDashboard.PATIENT_PHONE_KEY)}"
        val credit = intent.getIntExtra(doctorDashboard.PATIENT_CREDIT_KEY,0)
        val Puid = intent.getStringExtra(doctorDashboard.PATIENT_UID_KEY)

        name_display.text = Pname
        email_display.text = Pemail
        aadhar_display.text = Paadhar
        phone_display.text = Pphone

        fetchPrescriptions(Puid)
    }
    companion object{
        val PRESCRIPTION_DATE_KEY = "PRESCRIPTION_DATE_KEY"
        val PRESCRIPTION_SYMPTIONS_KEY = "PRESCRIPTION_SYMPTOMS_KEY"
        val PRESCRIPTION_TESTS_KEY = "PRESCRIPITON_TEST_KEY"
    }
    private fun fetchPrescriptions(Puid:String?) {
        if (Puid!=null){
            database.child("users").child(Puid).child("userData").child("prescriptions")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val pres = it.getValue(prescription::class.java)
                            if (pres != null) {
                                adapter.add(prescriptionViewholder(pres))
                            }
                        }
                        findViewById<RecyclerView>(R.id.rv_prescription).adapter = adapter

                        adapter.setOnItemClickListener { item, view ->
                            val data = item as prescriptionViewholder
                            val intent = Intent(view.context, PrescriptionDetails::class.java)
                            intent.putExtra(PRESCRIPTION_DATE_KEY, data.prescription.date)
                            intent.putExtra(PRESCRIPTION_SYMPTIONS_KEY, data.prescription.symptomps)
                            intent.putExtra(PRESCRIPTION_TESTS_KEY, data.prescription.tests)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //TODO("Not yet implemented")
                    }
                })
        }
    }
    class prescriptionViewholder(val prescription: prescription): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val prescriptionDate =viewHolder.itemView.findViewById<TextView>(R.id.prescription_name_tv)
            prescriptionDate.text = "Date Prescribed - ${prescription.date}"
        }

        override fun getLayout() = R.layout.prescription_layout

    }
}