package com.terricom.mytype.sleep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.Logger
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class SleepViewModel: ViewModel() {

    val userUid = UserManager.uid
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    val wakeUp = MutableLiveData<Timestamp>()
    val goToSleep = MutableLiveData<Timestamp>()
    val sleepHr = MutableLiveData<Float>()
    val sleepDocId = MutableLiveData<String>()

    val db = FirebaseFirestore.getInstance()
    val sleep = db.collection("Users")

    val _sleepToday = MutableLiveData<List<Sleep>>()
    val sleepToday : LiveData<List<Sleep>>
        get() = _sleepToday

    fun setSleepToday(sleepToday: List<Sleep>){
        _sleepToday.value = sleepToday
    }

    fun setWakeTime(time: Timestamp){
        wakeUp.value = time
    }

    fun setSleepTime(time: Timestamp){
        goToSleep.value = time
    }

    fun setSleepHr(sleep: Date, wakeUp: Date): Float{
        sleepHr.value = wakeUp.time.minus(sleep.time)/(1000 * 60 * 60).toFloat()
        return wakeUp.time.minus(sleep.time)/(1000 * 60 * 60).toFloat()
    }

    val _addSleepResult = MutableLiveData<Boolean>()
    val addSleepResult : LiveData<Boolean>
        get() = _addSleepResult

    fun addSleepSuccess(){
        _addSleepResult.value = true
    }

    fun addSleepFail(){
        _addSleepResult.value = false
    }

    init {
        getToday()
    }

    fun addSleepHr(){
        if (userUid != null){
        //發文功能
        val sleepContent = hashMapOf(
            "wakeUp" to wakeUp.value,
            "goToBed" to goToSleep.value,
            "sleepHr" to sleepHr.value,
            "timestamp" to Timestamp(Date().time)
        )
        sleep.get()
            .addOnSuccessListener { result->
//                for (doc in result){
                    if (sleepToday.value.isNullOrEmpty()){
                        sleep.document(userUid).collection("Sleep").document().set(sleepContent)
                        addSleepSuccess()

                    } else {
                        addSleepFail()
                    }
                }
//            }
        }


    }

    fun updateSleepHr(){
        if (userUid != null){
            //發文功能
            val sleepContent = hashMapOf(
                "wakeUp" to wakeUp.value,
                "goToBed" to goToSleep.value,
                "sleepHr" to sleepHr.value,
                "timestamp" to Timestamp(Date().time)
            )
//            getToday()
            sleep.get()
                .addOnSuccessListener { result->
//                    for (doc in result){
//                        if (sleepToday.value.isNullOrEmpty()){
//                            addSleepFail()
//                        } else {
                            sleep.document(userUid).collection("Sleep").document(sleepDocId.value!!).set(sleepContent)
                            addSleepSuccess()
//                        }
//                    }
                }
        }


    }

    fun getToday(): Boolean {
        val items = mutableListOf<Sleep>()
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = Date()
        val localDateStart: LocalDate = LocalDate.parse("${sdf.format(Date())}")
        localDateStart.atTime(LocalTime.MIDNIGHT)
        val localDateEnd : LocalDate = LocalDate.parse("${sdf.format(Date())}")
        localDateEnd.atTime(LocalTime.MAX)
        Logger.i("getToday() localDateStart = ${localDateStart} localDateEnd = ${localDateEnd}")

        if (userUid!!.isNotEmpty()){
            val sleepRecord = sleep
                .document(userUid)
                .collection("Sleep")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThan("timestamp", Timestamp(Timestamp.valueOf("${localDateStart} 00:00:00.000000000").time))

            sleepRecord
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val convertDate = java.sql.Date(document.toObject(Sleep::class.java).timestamp!!.time)
                        items.add(document.toObject(Sleep::class.java))
                        items[items.size-1].docId = document.id
                        Logger.i("items.add(document.toObject(Sleep::class.java)) = $items")
                    }
                    setSleepToday(items)
                }
            Logger.i("getToday() items = $items")
        }
        Logger.i("items.size =${items.size}")
        return items.size != 0
    }

}