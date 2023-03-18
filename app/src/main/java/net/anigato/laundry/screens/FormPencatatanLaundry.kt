package net.anigato.laundry.screens

import android.app.DatePickerDialog
import android.view.ViewDebug.IntToString
import android.widget.Toast
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.launch
import net.anigato.laundry.models.SetoranLaundry
import net.anigato.laundry.presistences.SetoranLaundryDao
import net.anigato.laundry.ui.theme.Purple700
import net.anigato.laundry.ui.theme.Teal200
import java.util.*

@Composable
fun FormPencatatanLaundry(setoranLaundryDao: SetoranLaundryDao){
    val context = LocalContext.current
    val nama = remember { mutableStateOf(TextFieldValue(""))}
    val totalBiaya = remember { mutableStateOf("")}
    val biaya = remember { mutableStateOf("")}
    val berat = remember { mutableStateOf(TextFieldValue(""))}

//    radio button tipe laundry
    val tipeLaundry = remember { mutableStateOf("3 Jam")} // tipeLaundry diberikan nilai awal
    val radioTipeLaundry = listOf("3 Jam", "Besok Selesai", "Reguler")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioTipeLaundry[0]) } // selectedOptiond an onOptionSelected diberikan nilai awal

//    date
    val tglMasuk = remember { mutableStateOf("")}
    val tglSelesai = remember { mutableStateOf("")}
    val tglNilai = remember { mutableStateOf("0")}

//    deklarasi integer tahun, bulan, tanggal dan kalender
    val tahun: Int
    val bulan: Int
    val tanggal: Int
    val kalender = Calendar.getInstance()

//    ambil tanggal saat ini
    tahun = kalender.get(Calendar.YEAR)
    bulan = kalender.get(Calendar.MONTH)
    tanggal = kalender.get(Calendar.DAY_OF_MONTH)
    kalender.time = Date()

//    deklarasi date picker
//    val datePickerDialogTglMasuk = DatePickerDialog(
//        context,
//        { _: DatePicker, tahun: Int, bulan: Int, tanggal: Int ->
//            tglMasuk.value = "$tanggal/${bulan+1}/$tahun"
//        }, tahun, bulan, tanggal
//    )

//    style tombol save dan reset
    val saveButtonColors = ButtonDefaults.buttonColors( backgroundColor = Purple700, contentColor = Teal200 )
    val resetButtonColors = ButtonDefaults.buttonColors( backgroundColor = Teal200, contentColor = Purple700 )

    val scope = rememberCoroutineScope() // untuk menyimpan ke db


    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
//        Judul Aplikasi
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Catat Laundry",
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp
            )
        }
//        Form nama pelanggan
        Column {
            OutlinedTextField(
                label = { Text(text = "Nama Pelanggan") },
                value = nama.value,
                onValueChange = {
                    nama.value = it
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = { Text(text = "Contoh : Anggi") }
            )
        }
//        radio button tipe laundry
        Column {
            Text(text = "Tipe Laundry", fontSize = 18.sp)
//            tglMasuk diisi dengan tanggal, bulan dan tahun saat aplikasi dibuka, untuk bulan ditambah 1 karena integer dari bulan dimulai dari 0
            tglMasuk.value = tanggal.toString()+"/"+(bulan+1).toString()+"/"+tahun.toString()
//            tglSelesai diisi dengan tanggal, bulan dan tahun saat aplikasi dibuka, bulan juga ditambah 1. namun pada tanggal ditambahkan dengan tglNilai yang akan diisi sdaat radiobutton dipilih
            tglSelesai.value = (tanggal+Integer.parseInt(tglNilai.value)).toString()+"/"+(bulan+1).toString()+"/"+tahun.toString()
            Row {
                radioTipeLaundry.forEach { text ->
                    RadioButton(
                        selected = text == selectedOption,
                        onClick = {
                            onOptionSelected(text)
//                            kondisi untuk membuat biaya jika berat belum diisi dan tglNilai berdasarkan nilai text
                            if(text.equals("3 Jam")){
                                biaya.value = "10000"
                                tglNilai.value = "0"
                            }else if(text.equals("Besok Selesai")){
                                biaya.value = "8000"
                                tglNilai.value = "1"
                            }else {
                                biaya.value = "6000"
                                tglNilai.value = "3"
                            }
//                            memasukan nilai text ke tipeLaundry
                            tipeLaundry.value = text
//                            kondisi untuk membuat totalBiaya langsung terisi nilai ketika radiobutton diklik
                            if(berat.value.text.isNotBlank()){ // jika form berat sudah pernah diisi
//                                mengisi total biaya dengan perhitungan berat*biaya yang diubah kebentuk string
                                totalBiaya.value = ((Integer.parseInt(berat.value.text))*Integer.parseInt(biaya.value)).toString()
                            }else{
                                totalBiaya.value = "0" // jika form berat belum pernah diisi
                            }
                        }
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 0.dp, top = 12.dp)
                    )
                }
            }
        }
//        form berat pakaian
        Column {
            OutlinedTextField(
                label = { Text(text = "Berat (Kg)") },
                value = berat.value,
                onValueChange = {
                    berat.value = it
//                    kondisi untuk membuat biaya jika radiobutton tipeLaundry belum dipilih, sekaligus mengisi totalBiaya
                    if(berat.value.text.isNotBlank()){
                        if(tipeLaundry.value.equals("3 Jam")){
                            biaya.value = "10000"
                        }else if(tipeLaundry.value.equals("Besok Selesai")){
                            biaya.value = "8000"
                        }else {
                            biaya.value = "6000"
                        }
//                        mengisi total biaya dengan perhitungan berat*biaya yang diubah kebentuk string
                        totalBiaya.value = ((Integer.parseInt(berat.value.text))*Integer.parseInt(biaya.value)).toString()
                    }
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType =
                KeyboardType.Number), // membuat keyboard hanya ada tulisan
                placeholder = { Text(text = "Contoh : 5") }
            )
        }

//        form total biaya (diisi otomatis)
        Column {
            OutlinedTextField(
                label = { Text(text = "Total Biaya") },
                value = totalBiaya.value,
                onValueChange = {
                    totalBiaya.value
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                placeholder = { Text(text = "Rp. ") },
                readOnly = true
            )
        }

//        form datepicker tanggal masuk, otomatis diisi dengan tanggal saat ini
        Column {
            OutlinedTextField(
                label = { Text(text = "Tanggal Masuk") },
                value = tglMasuk.value,
                onValueChange = {
                    tglMasuk.value
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                placeholder = { Text(text = "dd/mm/yyyy") },
                readOnly = true
            )
        }
//        form tanggal selesai (otomatis berdasarkan tipe laundry)
        Column {
            OutlinedTextField(
                label = { Text(text = "Tanggal Selesai") },
                value = tglSelesai.value,
                onValueChange = {
                    tglSelesai.value
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                placeholder = { Text(text = "dd/mm/yyyy") },
                readOnly = true
            )
        }


        Row (modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()) {
            Button(modifier = Modifier.weight(5f), onClick = {
//                kondisi memastikan semua form tidak kosong
                if (nama.value.text.isNotEmpty() &&
                    tglMasuk.value.isNotEmpty() &&
                    tglSelesai.value.isNotEmpty() &&
                    tipeLaundry.value.isNotEmpty() &&
                    totalBiaya.value.isNotEmpty() &&
                    berat.value.text.isNotEmpty()
                ) { // jika kondisi terpenuhi
                    Toast.makeText(context, "Berhasil Disimpan", Toast.LENGTH_LONG).show() // membuat popup
                    val id = uuid4().toString()
//                    mengirim ke model, urutan harus sama dengan model
                    val item = SetoranLaundry(
                        id,
                        nama.value.text,
                        tglMasuk.value,
                        tglSelesai.value,
                        tipeLaundry.value,
                        Integer.parseInt(totalBiaya.value),
                        Integer.parseInt(berat.value.text)
                    )
//                    disimpan ke db
                    scope.launch {
                        setoranLaundryDao.insertAll(item)
                    }
//                    mengosongkan form ketika sudah berhasil menyimpan data
                    nama.value = TextFieldValue("")
                    tipeLaundry.value = "3 Jam"
                    berat.value = TextFieldValue("")
                    totalBiaya.value = "0"
                    tglMasuk.value = tglMasuk.value
                    tglSelesai.value = tglSelesai.value

//                    kondisi form kosong hanya ada nama dan berat, karena tipe laundry otomatis pilih 1, tanggal masuk pasti ambil tanggal sekarang dan tanggal selesai pasti diisi tanggal sekarang ditambah dengan tglNilai dari radiobutton tipelaundry
                } else if (nama.value.text.isEmpty()) {
                    Toast.makeText(context, "Harap isi Nama Pelanggan",Toast.LENGTH_LONG).show()
                } else if (berat.value.text.isEmpty()) {
                    Toast.makeText(context, "Harap isi Berat Pakaian",Toast.LENGTH_LONG).show()
                }
            }, colors = saveButtonColors) {
                Text(
                    text = "Simpan",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ), modifier = Modifier.padding(8.dp)
                )
            }
            Button(modifier = Modifier.weight(5f), onClick = {
//                mengosongkan semua form
                nama.value = TextFieldValue("")
                tipeLaundry.value = "3 Jam"
                berat.value = TextFieldValue("")
                totalBiaya.value = "0"
                tglMasuk.value = tglMasuk.value
                tglSelesai.value = tglSelesai.value
            }, colors = resetButtonColors) {
                Text(
                    text = "Reset",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ), modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
