package com.example.finanzas.clients.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.clients.models.Client
import com.example.finanzas.databinding.PrototypeClientBinding
import com.example.finanzas.shared.OnItemClickListener
import com.example.finanzas.shared.StateManager

class ClientAdapter(
    private val clients: ArrayList<Client>,
    private val buttonActivated: Boolean,
    private val onItemClickListener: OnItemClickListener<Client>? =null,
    private val onClientDeleteListener: OnClientDeleteListener? = null
)
    :RecyclerView.Adapter<ClientAdapter.Prototype>(){

    interface OnClientDeleteListener {
        fun onClientDeleted(client: Client, position: Int)
    }

    inner class Prototype(private val binding: PrototypeClientBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(client: Client, position: Int) {
            binding.tvClientName.text = "${client.name} ${client.lastName}"
            binding.tvClientDNI.text = "DNI: ${client.dni}"

            if(StateManager.frenchButtonActivated || !buttonActivated)
                binding.ibDeleteClient.visibility = View.GONE
            else binding.ibDeleteClient.visibility = View.VISIBLE
            if (!buttonActivated)
                binding.btnGoFrench.visibility = View.INVISIBLE
            else binding.btnGoFrench.setOnClickListener {
                onItemClickListener?.onItemClicked(client)
            }

            binding.ibDeleteClient.setOnClickListener {
                onClientDeleteListener?.onClientDeleted(client, position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Prototype {

        return Prototype(
            PrototypeClientBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Prototype, position: Int) = holder.bind(clients[position], position)
    override fun getItemCount(): Int = clients.size
}