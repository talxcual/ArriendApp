package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LuxeRentalDao {

    // --- Users ---
    @Query("SELECT * FROM usuarios")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM usuarios WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // --- Inventory ---
    @Query("SELECT * FROM inventario")
    fun getAllInventoryFlow(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventario")
    suspend fun getAllInventory(): List<InventoryEntity>

    @Query("SELECT * FROM inventario WHERE id = :id LIMIT 1")
    suspend fun getInventoryById(id: String): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(items: List<InventoryEntity>)

    @Update
    suspend fun updateInventory(item: InventoryEntity)

    // --- Reserves ---
    @Query("SELECT * FROM reservas")
    fun getAllReservasFlow(): Flow<List<ReservaEntity>>

    @Query("SELECT * FROM reservas")
    suspend fun getAllReservasList(): List<ReservaEntity>

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getReservaById(id: String): ReservaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReserva(reserva: ReservaEntity)

    @Update
    suspend fun updateReserva(reserva: ReservaEntity)

    @Delete
    suspend fun deleteReserva(reserva: ReservaEntity)
}
