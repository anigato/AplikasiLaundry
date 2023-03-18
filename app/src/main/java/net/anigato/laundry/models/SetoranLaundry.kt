package net.anigato.laundry.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SetoranLaundry(
    @PrimaryKey val id: String,
    val nama: String,
    val tglMasuk: String, // langsung terisi tanggal saat ini
    val tglSelesai: String, // langsung terisi sesuai tanggal masuk yang ditambah sesuai tipeLaundry
    val tipeLaundry: String, // menentukan totalBiaya dan tglSelesai
    val totalBiaya: Int, // hasil kali berat dengan tipeLaundry
    val berat: Int
)
