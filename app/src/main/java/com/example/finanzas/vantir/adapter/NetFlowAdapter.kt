package com.example.finanzas.vantir.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.R

class NetFlowAdapter(private val n: Int)
    :RecyclerView.Adapter<NetFlowAdapter.Prototype>(){
    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val etNetFlow = itemView.findViewById<EditText>(R.id.etNetFlow)
        fun bind(position: Int) {
            val yearNumber = position + 1
            etNetFlow.hint = "Flujo neto año $yearNumber"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Prototype {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.prototype_net_flow, parent, false)
        return Prototype(view)
    }

    override fun getItemCount(): Int {
        return n
    }

    override fun onBindViewHolder(holder: Prototype, position: Int) {
        holder.bind(position)
    }
}