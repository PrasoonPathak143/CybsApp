package com.example.cybsapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cybsapp.databinding.LayoutCallLogBinding

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
            val builder = AlertDialog.Builder(context)


            builder.setView(R.layout.add_information_dialog).setTitle("Add Details").setPositiveButton("OK"){
                dialog, _ -> dialog.dismiss()
            }
                .setNegativeButton("Cancel"){
                    dialog,_->dialog.dismiss()
                }
                .setCancelable(false)


            val alertDialog = builder.create()

            alertDialog.show()


        }

    }


}
