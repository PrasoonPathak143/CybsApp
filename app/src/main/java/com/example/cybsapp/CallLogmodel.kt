package com.example.cybsapp

data class CallLogmodel(
    private var phNumber : String,
    private var contactName : String,
    private var callType :String,
    private var callDate : String,
    private var callTime : String,
    private var callDuration : String
){
    fun getNumber() : String{
        return phNumber
    }
    fun getContactName() : String{
        return contactName
    }
    fun getCallType() : String{
        return callType
    }
    fun getCallDate() : String{
        return callDate
    }
    fun getCallTime() : String{
        return callTime
    }
    fun getCallDuration() : String{
        return callDuration
    }
}
