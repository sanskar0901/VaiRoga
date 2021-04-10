package com.tksh.vairoga

import android.content.Intent
import android.graphics.PathEffect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import org.w3c.dom.Text
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class doctorDashboard : AppCompatActivity() {
    private var onBackPressedToexit = false
    private val database = FirebaseDatabase.getInstance().reference
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        findViewById<RecyclerView>(R.id.rv_patients).layoutManager = LinearLayoutManager(this)

        val bottomnav = findViewById<BottomNavigationView>(R.id.doctor_bottom_nav)
        val searchPatient = findViewById<FloatingActionButton>(R.id.searchPatients)

        searchPatient.setOnClickListener {
            startActivity(Intent(this,SearchPatient::class.java))
        }
        searchPatient.setOnLongClickListener {
            Toast.makeText(this,"Search Patient",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        bottomnav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homepage -> {
                    startActivity(Intent(this, doctorDashboard::class.java))
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(LoginOptions.LOGIN_KEY,"DOCTOR")
                    startActivity(intent)
                    true
                }
                else -> false
            }


        }
        fetchPatients()
        fetchResult()
    }
    companion object{
        val PATIENT_NAME_KEY = "PATIENT_NAME_KEY"
        val PATIENT_EMAIL_KEY = "PATIENT_EMAIL_KEY"
        val PATIENT_AADHAR_KEY = "PATIENT_AADHAR_KEY"
        val PATIENT_PHONE_KEY = "PATIENT_PHONE_KEY"
        val PATIENT_CREDIT_KEY = "PAITIENT_CREDIT_KEY"
        val PATIENT_UID_KEY = "PATIENT_UID_KEY"
    }
    private fun fetchPatients() {
        database.child("users").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    it.children.forEach {
                        val name_display = it.getValue(PatientUserData::class.java)
                        if(name_display!= null){
                            Log.d("datap",it.children.toString())
                            if (name_display.type == "PATIENT"){
                                adapter.add(patientsViewholder(adapter,name_display))
                            }
                        }
                    }
                    findViewById<RecyclerView>(R.id.rv_patients).adapter = adapter
                adapter.setOnItemClickListener { item, view ->
                    val userData = item as patientsViewholder
                    val intent = Intent(view.context, PatientDetails::class.java)
                    intent.putExtra(PATIENT_NAME_KEY,userData.patientUserData.name)
                    intent.putExtra(PATIENT_EMAIL_KEY,userData.patientUserData.email)
                    intent.putExtra(PATIENT_AADHAR_KEY,userData.patientUserData.aadhar)
                    intent.putExtra(PATIENT_PHONE_KEY,userData.patientUserData.phone)
                    intent.putExtra(PATIENT_CREDIT_KEY,userData.patientUserData.credit)
                    intent.putExtra(PATIENT_UID_KEY,userData.patientUserData.uid)
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
        try{
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) { Client.api.clone().execute() }
                if (response.isSuccessful) {
                    val data = Gson().fromJson(response.body?.string(), Response::class.java)
                    launch(Dispatchers.Main) {
                        bindCombinedData(data.statewise[0])
                    }
                }
            }
        }catch (e:Exception){
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

    class patientsViewholder(val adapter: GroupAdapter<GroupieViewHolder>,val patientUserData: PatientUserData):Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val patientName =viewHolder.itemView.findViewById<TextView>(R.id.patient_name_tv)
            patientName.text = patientUserData.name
        }

        override fun getLayout() = R.layout.patient_layout

    }

    override fun onBackPressed() {
        if (onBackPressedToexit){
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,"User Signed Out",Toast.LENGTH_SHORT).show()
            super.onBackPressed()
            return
        }
        onBackPressedToexit = true
        Toast.makeText(this, "Please press BACK again to logout", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { onBackPressedToexit = false }, 2000)
    }
}