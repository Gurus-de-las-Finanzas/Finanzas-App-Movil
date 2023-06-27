package com.example.finanzas.clients.models

class Client (
    var id: Int,
    var name: String,
    var lastName: String,
    var dni: String
) {
    override fun equals(other: Any?): Boolean {
        if(other is Client) {
            return this.id == other.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int = id.hashCode()
}