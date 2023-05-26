package com.example.finanzas.clients.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.R
import com.example.finanzas.clients.models.Client

class ClientAdapter(private val clients: List<Client>, private val context: Context)
    :RecyclerView.Adapter<ClientAdapter.Prototype>(){
    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvClientName = itemView.findViewById<TextView>(R.id.tvClientName)
        private val tvClientDNI = itemView.findViewById<TextView>(R.id.tvClientDNI)

        fun bind(client: Client) {
            tvClientName.text = "${client.name} ${client.lastName}"
            tvClientDNI.text = "DNI: ${client.dni}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientAdapter.Prototype {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.prototype_client, parent, false)
        return Prototype(view)
    }

    override fun onBindViewHolder(holder: ClientAdapter.Prototype, position: Int) {
        holder.bind(clients[position])
    }

    override fun getItemCount(): Int {
        return clients.size
    }
}