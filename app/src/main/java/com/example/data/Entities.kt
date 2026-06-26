package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey val uid: String,
    val nombre: String,
    val rol: String
)

@Entity(tableName = "inventario")
data class InventoryEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val imagen: String,
    val precio: Double,
    val estado: String // "DISPONIBLE", "OCUPADO", "MANTENIMIENTO"
)

@Entity(tableName = "reservas")
data class ReservaEntity(
    @PrimaryKey val id: String,
    val equipmentId: String,
    val fechaHora: String,
    val fechaRegistro: String,
    val clienteNombre: String,
    val clienteDireccion: String,
    val clienteContacto: String,
    val monto: Double,
    val estado: String, // "Pendiente", "En Camino", "Entregado"
    val workerId: String // UID of creator / worker assigning
)
