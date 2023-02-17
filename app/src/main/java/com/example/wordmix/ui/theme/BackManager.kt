package com.example.wordmix.ui.theme

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import org.json.JSONTokener
import java.util.ArrayList

class BackManager {
    val db = Firebase.firestore

    fun test(){
        db.collection("Users")
            .whereEqualTo("Login", "admi")
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Log.d("EEE", "null")
                }
                for (document in documents) {
                    Log.d("EEE", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("EEE", "Error getting documents: ", exception)
            }
//        db.collection("Users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    parseJson(document.data)
//                    Log.d("EEE", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("EEE", "Error getting documents.", exception)
//            }
    }

    suspend fun login(login: String, password: String): User? {

        var user: User? = null

        db.collection("Users")
            .whereEqualTo("Login", login)
            .whereEqualTo("Password", password)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Log.d("EEE", "no such user")
                }
                else if(documents.size() > 1){
                    Log.d("EEE", "more then 1 user")
                }
                else{
                    val docData = documents.documents[0].data
                    if (docData != null) {
                        user = User((docData["ID"] as Long).toInt(), (docData["Language"] as Long).toInt())
                    }
                }
//                for (document in documents) {
//                    Log.d("EEE", "${document.id} => ${document.data}")
//                }
            }
            .addOnFailureListener { exception ->
                user = null
                Log.w("EEE", "Error getting documents: ", exception)
            }.await()

        return user
    }

    suspend fun getUserScoreHistory(user: User): ArrayList<ScoreCell>?{
        var scoreHistory: ArrayList<ScoreCell> = ArrayList<ScoreCell>()
        var cell: ScoreCell
        db.collection("Score")
            .whereEqualTo("UserID", user.ID)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Log.d("EEE", "no user history, UserID: ${user.ID}")
                }
                else{
                    for (document in documents) {
                        val docData = document.data
                        cell = ScoreCell(ID = (docData["ID"] as Long).toInt(),
                                        language = (docData["Language"] as Long).toInt(),
                                        score = (docData["Score"] as Long).toInt(),
                                        userID = (docData["UserID"] as Long).toInt())

                        scoreHistory.add(cell)
                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.w("EEE", "Error getting documents: ", exception)
            }.await()

        scoreHistory.reverse()
        return scoreHistory
    }

    suspend fun getLeaderBoardByLanguage(): ArrayList<ScoreCell>{
        var scoreHistory: ArrayList<ScoreCell> = ArrayList<ScoreCell>()
        var cell: ScoreCell
        db.collection("Score")
//            .whereEqualTo("Language", language)
            .orderBy("Score")
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Log.d("EEE", "no leaderboard")
                }
                else{
                    for (document in documents) {
                        val docData = document.data
                        cell = ScoreCell(ID = (docData["ID"] as Long).toInt(),
                            language = (docData["Language"] as Long).toInt(),
                            score = (docData["Score"] as Long).toInt(),
                            userID = (docData["UserID"] as Long).toInt())
                        scoreHistory.add(cell)
                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.w("EEE", "Error getting documents: ", exception)
            }.await()

        scoreHistory.reverse()
        return scoreHistory
    }

    suspend fun getUserNameByID(userID: Int): String{
        var userName = ""
        db.collection("Users")
            .whereEqualTo("ID", userID)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Log.d("EEE", "no such user")
                }
                else if(documents.size() > 1){
                    Log.d("EEE", "more then 1 user")
                }
                else{
                    val docData = documents.documents[0].data
                    if (docData != null) {
                        userName = docData["Login"] as String
                    }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("EEE", "Error getting documents: ", exception)
            }.await()

        return userName
    }

    fun parseJson(file: String){
        val jsonObject = JSONTokener(file).nextValue() as JSONObject

// ID
        val id = jsonObject.getString("id")
        Log.i("ID: ", id)

// Employee Name
        val employeeName = jsonObject.getString("employee_name")
        Log.i("Employee Name: ", employeeName)

// Employee Salary
        val employeeSalary = jsonObject.getString("employee_salary")
        Log.i("Employee Salary: ", employeeSalary)

// Employee Age
        val employeeAge = jsonObject.getString("employee_age")
        Log.i("Employee Age: ", employeeAge)
    }
}