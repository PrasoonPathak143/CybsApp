package com.example.cybsapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.cybsapp.databinding.LayoutCallLogBinding
import com.google.firebase.database.FirebaseDatabase

class CallLogAdapter(private val activity: Activity, private val context : Context, private val list : ArrayList<CallLogmodel>) : RecyclerView.Adapter<CallLogAdapter.ViewHolder>() {

    class ViewHolder(binding: LayoutCallLogBinding) : RecyclerView.ViewHolder(binding.root){
        val phoneNumber = binding.tvPhoneNumber
        val contactName = binding.tvContactName
        val callDate = binding.tvCallDate
        val callTime = binding.tvCallTime
        val callDuration = binding.tvCallDuration
        val callType = binding.tvCallType
        val btnAdd = binding.btnAddInformation
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutCallLogBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLog : CallLogmodel = list[position]
        holder.phoneNumber.text = currentLog.getNumber()
        holder.contactName.text = currentLog.getContactName()
        holder.callType.text = currentLog.getCallType()
        holder.callDate.text = currentLog.getCallDate()
        holder.callTime.text = currentLog.getCallTime()
        holder.callDuration.text = currentLog.getCallDuration()
        holder.btnAdd.setOnClickListener {
            showCustomAlertDialog(context, currentLog)

        }

    }
    private fun showCustomAlertDialog(context: Context, currentLog : CallLogmodel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add Details")

        val inflater = LayoutInflater.from(context)
        val customView: View = inflater.inflate(R.layout.add_information_dialog, null)
        builder.setView(customView)

        val editText1 = customView.findViewById<EditText>(R.id.edit_reason)
        val editText2 = customView.findViewById<EditText>(R.id.edit_discussion)

        builder.setPositiveButton("OK") { dialog, _ ->
            val reason = editText1.text.toString()
            val discussion = editText2.text.toString()
            sendDataToServer(currentLog.getCallDate(), currentLog.getCallTime(), currentLog.getNumber(), reason, discussion)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)

        builder.show()
    }
    private fun sendDataToServer(date : String, time : String, number : String, reason : String, discussion : String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("CallLog")
            .child(getDeviceName())
            .child(date)
            .child(time)
            .child(number)
        val newData = HashMap<String, Any>()
        newData["Reason"] = reason
        newData["Discussion"] = discussion
        myRef.setValue(newData)
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String): String {
        if (s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar() + s.substring(1)
        }
    }



}
