package com.tksh.vairoga

class PatientUserData(val name:String, val email:String, val uid:String, val aadhar:String, val phone:String, val type:String, val credit:Int) {
    constructor():this("","","","","","PATIENT",0)
}