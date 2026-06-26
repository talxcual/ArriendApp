package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserEntity::class, InventoryEntity::class, ReservaEntity::class], version = 1, exportSchema = false)
abstract class LuxeRentalDatabase : RoomDatabase() {
    abstract fun dao(): LuxeRentalDao

    companion object {
        @Volatile
        private var INSTANCE: LuxeRentalDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LuxeRentalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuxeRentalDatabase::class.java,
                    "luxe_rental_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            val database = getDatabase(context, scope)
                            val dao = database.dao()
                            
                            // Insert default workers
                            dao.insertUser(UserEntity("ADMIN", "Juan Pérez", "Administrador"))
                            dao.insertUser(UserEntity("ADMIN2", "María García", "Cliente Premium"))
                            dao.insertUser(UserEntity("ADMIN3", "Carlos Ruiz", "Operador Logística"))

                            // Seed inventory matches the images perfectly
                            dao.insertInventory(listOf(
                                InventoryEntity(
                                    id = "castillo_real",
                                    nombre = "Castillo Real",
                                    imagen = "https://lh3.googleusercontent.com/aida-public/AB6AXuCEq52qtzMnWit1JBZ5P4aaT2uBydbEFBSwwZJhEl9Lv6yMrD09Lcg8znoQyrSEw0N4qjT9gyPkcVqBtdMbkDQBi28F667qzPYNKL3E3VUbh2Ndn8nZegPVZUHari8xYXYsGXyZvd9otNj_VkvAdmNzZPYmhiXmPRI2p84VdfVCaW8ZHAQyX6SiHlnRka3z6fcOQjZADAUVLX794i3kzlWG0Czh0Ds_BD738aYC1Z0wCtpm42R7oud1sLD7mfEIgGTGlJZZCQrLo3o",
                                    precio = 45000.0,
                                    estado = "DISPONIBLE"
                                ),
                                InventoryEntity(
                                    id = "arcade_retro",
                                    nombre = "Arcade Retro",
                                    imagen = "https://lh3.googleusercontent.com/aida-public/AB6AXuDHiSOG3ChSSCGv9ckjOq7F8Z2jbuR3GPuZi5oO8eysXIU-92mcFlPvCWcX6WCNBSpu3rkwlsSPjEXgzy7W-R5BFFmAHhMXgEC9n9wasw1xUl83Sxk7bM7Ma4EcLjz5Rz5HeqYRCaht59uEiOrYRw8yYYkNzx4RehapiwmmwfScJb_unFpePrYKnhI4CmFs4KIExkuazC49h7KyIXbfYl0BrAZBR4OnbX8NlEjWpKSA4xS6Vj5HWqiCA44pFKcWKOh9x-qb12CGbnI",
                                    precio = 35000.0,
                                    estado = "OCUPADO"
                                ),
                                InventoryEntity(
                                    id = "cama_elastica_xl",
                                    nombre = "Cama Elástica XL",
                                    imagen = "https://lh3.googleusercontent.com/aida-public/AB6AXuCd2icIXdCBEKyN5w7qXYu9NZRTO29bBXE5QyVmO8sKGMKx7qwB5v3GEvz5MTpZFNFXHcPREAEmBjlXOAlwocuoRIA-nyzCmnucdr8fRHJf9FYpf--YVpGDbo5xKHuTd3i_x1Y8J6AV8W4WtUB0uRv_PbSfXQTdLGFMywV6WEQRryPFmJjj4JZqvSJGU3MhztWiTsyKnImBvzXL8F5jR6jze0JwEkYv48c5OF3ScyxtDC1S8jJymcWE8mDtr-nw_rPj3WFCNrpqkW4",
                                    precio = 55000.0,
                                    estado = "DISPONIBLE"
                                ),
                                InventoryEntity(
                                    id = "maquina_popcorn",
                                    nombre = "Máquina Popcorn",
                                    imagen = "https://lh3.googleusercontent.com/aida-public/AB6AXuAcbLThDLrWCFMN58Hx05yf35NCsvfzeeXHokpmrvZ4ZB31v-57i_aWfJlb-zPJ1oJowqO7p67OiTg1Ccjp7TERS7kzNGiLSh8-mfUv1LSr8pCZTqrEPHHlqAQ2jRAow0PB0zh4aac-qQU16mb9gubqmQvw9mkQIkEJ4Eve4KH5EKmmm1YDuMtmK6oFaAKfNYie4WNQjn5hkI9ixb8oUDOXz1SbLxWAwnpqccJttpwQuyxnvjAxeB5unoknfYadjqpfSXkISvQIhd8",
                                    precio = 20000.0,
                                    estado = "DISPONIBLE"
                                ),
                                InventoryEntity(
                                    id = "living_luxe",
                                    nombre = "Set de Living Modular Luxe",
                                    imagen = "https://lh3.googleusercontent.com/aida-public/AB6AXuC86w3fFfv74ZA9HqZFh_9VAA4gl1blFksNmo-PYQczrxdx1IgNV6FD2Qz2257RBkof7Ik4rBP7KC3B055W9rY2zylyKkW8xO3AthGwZHE0JyXglrrssfg5LgE-jQsyuV5A0VBsC8179cdKwHZoMJ7QWeR-HD-IfHcM7Hlc59rO8rzBBPCjgZytV6gBLwZVGlG6KC9ol1ylmjvG4E-oMlYOl302wBavJoSGhpLjtDExzJrYsUGuP-8Cu90kl74ukJuWy3jQPNeGeKs",
                                    precio = 14500.0,
                                    estado = "OCUPADO"
                                ),
                                InventoryEntity(
                                    id = "gazebo_pro",
                                    nombre = "Gazebo Pro 3x3m",
                                    imagen = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjkLO_1CYl3rSqUdcSv5l44BtF-Kqb2_-I7PGL7UrtntthBaOzc5EHZw-KX6fZIpjrFGRcVr74sDSCodlvn-GVPEivS58fyqlA9G3SrJOPx0hQOzQbGPGKoEgkX6QBXI9N4CUxdqEjzwVmB-RFYXDaZIdSLMP5Fa10gEZQlBUD7SFo5JPzNDW8Jvj-Q0E1SgNcoCOHdI78n_Yo6rfam2cCpEPFPulGG2gZoL5Wmx_WBK8-_2sj0wKh-WEVKtj5X3izLXs2hao739g",
                                    precio = 12000.0,
                                    estado = "OCUPADO"
                                )
                            ))

                            // Default screen route items
                            dao.insertReserva(ReservaEntity(
                                id = "4421",
                                equipmentId = "living_luxe",
                                fechaHora = "09:00",
                                fechaRegistro = "01/05/2024 08:30",
                                clienteNombre = "Juan Pérez",
                                clienteDireccion = "Av. del Libertador 4500, Palermo",
                                clienteContacto = "11-4444-5555",
                                monto = 14500.0,
                                estado = "Pendiente", // "Pendiente", "En Camino", "Entregado"
                                workerId = "ADMIN"
                            ))

                            dao.insertReserva(ReservaEntity(
                                id = "4390",
                                equipmentId = "gazebo_pro",
                                fechaHora = "11:30",
                                fechaRegistro = "01/05/2024 08:35",
                                clienteNombre = "María García",
                                clienteDireccion = "Calle Falsa 123, Martínez",
                                clienteContacto = "11-5555-6666",
                                monto = 12000.0,
                                estado = "Pendiente",
                                workerId = "ADMIN"
                            ))

                            dao.insertReserva(ReservaEntity(
                                id = "4428",
                                equipmentId = "castillo_real",
                                fechaHora = "14:00",
                                fechaRegistro = "02/05/2024 10:15",
                                clienteNombre = "Carlos Ruiz",
                                clienteDireccion = "Quinta El Olivo, Pilar",
                                clienteContacto = "11-6666-7777",
                                monto = 45000.0,
                                estado = "Pendiente",
                                workerId = "ADMIN2"
                            ))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
