package net.anigato.laundry.presistences

import androidx.lifecycle.LiveData
import androidx.room.*
import net.anigato.laundry.models.SetoranLaundry

@Dao
interface SetoranLaundryDao {
//    untuk load semua data
    @Query("SELECT * FROM SetoranLaundry")
    fun loadAll() : LiveData<List<SetoranLaundry>>

//    untuk ambil data berdasarkan id
    @Query("SELECT * FROM SetoranLaundry WHERE id = :id")
    suspend fun find(id: String) : SetoranLaundry?

//    untuk masukan data baru sekaligus dapat mengupdatenya
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg items: SetoranLaundry)

//    untuk menghapus data
    @Delete
    fun delete(item: SetoranLaundry)
}