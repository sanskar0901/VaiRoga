package com.tksh.vairoga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Exception

class patientDashboard : AppCompatActivity() {
    private var onBackPressedToExit = false
    private val database = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser
    private val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        val bottomnav = findViewById<BottomNavigationView>(R.id.patient_bottom_nav)
        val credit = findViewById<ProgressBar>(R.id.health_credit)
        val credit_score = findViewById<TextView>(R.id.credit_score)


        database.child("users").child(user!!.uid).child("userData").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val credit_value = snapshot.child("credit").value
                credit.progress = credit_value.toString().toInt()
                val score = "${(credit_value.toString().toInt())/10} / 10"
                credit_score.text = score
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

        })


        bottomnav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homepage -> {
                    startActivity(Intent(this, patientDashboard::class.java))
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(LoginOptions.LOGIN_KEY,"PATIENT")
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        if (user!=null){ fetchPrescription(user) }
        fetchResult()
    }
    companion object{
        val PRESCRIPTION_DATE_KEY = "PRESCRIPTION_DATE_KEY"
        val PRESCRIPTION_SYMPTIONS_KEY = "PRESCRIPTION_SYMPTOMS_KEY"
        val PRESCRIPTION_TESTS_KEY = "PRESCRIPITON_TEST_KEY"
    }
    private fun fetchPrescription(user:FirebaseUser) {
        database.child("users").child(user.uid).child("userData").child("prescriptions").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val data_f = it.getValue(PatientUserData::class.java)
                        if (data_f != null) {
                                    val prescription = it.getValue(prescription::class.java)
                                    if(prescription!=null){ adapter.add(medicalViewholder(prescription))
                            }
                        }

                    findViewById<RecyclerView>(R.id.medical_history).adapter = adapter

                    adapter.setOnItemClickListener { item, view ->
                        val data = item as medicalViewholder
                        val intent = Intent(view.context,PrescriptionDetails::class.java)
                        intent.putExtra(PatientDetails.PRESCRIPTION_DATE_KEY, data.prescription.date)
                        intent.putExtra(PatientDetails.PRESCRIPTION_SYMPTIONS_KEY, data.prescription.symptomps)
                        intent.putExtra(PatientDetails.PRESCRIPTION_TESTS_KEY, data.prescription.tests)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

        })
    }

    private fun fetchResult() {
        try {
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) { Client.api.clone().execute() }
                if (response.isSuccessful) {
                    val data = Gson().fromJson(response.body?.string(), Response::class.java)
                    launch(Dispatchers.Main) {
                        bindCombinedData(data.statewise[0])
                    }
                }
            }
        } catch (e:Exception){
            Log.d("error",e.toString())
        }
    }

    private fun bindCombinedData(statewiseItem: StatewiseItem) {
        val lastUpdatedTime = statewiseItem.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val lastUpdated = findViewById<TextView>(R.id.last_updated_tracker)
        val confirmedTv = findViewById<TextView>(R.id.confirmedTv)
        val recoveredTv = findViewById<TextView>(R.id.recoveredTv)
        val activeTv = findViewById<TextView>(R.id.activeTv)
        val deceasedTv = findViewById<TextView>(R.id.deceasedTv)



        lastUpdated.text = "Last Updated\n ${getTimeAgo(simpleDateFormat.parse(lastUpdatedTime))}"

        confirmedTv.text = statewiseItem.confirmed
        recoveredTv.text = statewiseItem.recovered
        activeTv.text = statewiseItem.active
        deceasedTv.text = statewiseItem.deaths
    }
    private fun getTimeAgo(past: Date):String {
        val now = Date()
        val seconds:Long = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes:Long = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours:Long = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when{
            seconds<60 ->{
                "Few seconds ago"
            }
            minutes<60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes%60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }
    class medicalViewholder(val prescription: prescription):Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val date = viewHolder.itemView.findViewById<TextView>(R.id.prescription_name_tv)
            val name = "Prescribed on ${prescription.date}"
            date.text = name

        }

        override fun getLayout() = R.layout.prescription_layout

    }


    override fun onBackPressed() {
        if (onBackPressedToExit){
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,"User Signed Out", Toast.LENGTH_SHORT).show()
            super.onBackPressed()
            return
        }
        onBackPressedToExit = true
        Toast.makeText(this, "Please press BACK again to Logout", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { onBackPressedToExit = false }, 2000)
    }
}