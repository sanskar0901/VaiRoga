package com.tksh.vairoga

class DoctorUserData(val name:String, val email:String, val uid:String, val phone:String, val type:String) {
    constructor():this("","","","","DOCTOR")
}