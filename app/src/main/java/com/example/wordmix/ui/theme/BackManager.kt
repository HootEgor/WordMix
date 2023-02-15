package com.example.wordmix.ui.theme

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BackManager {
    val db = Firebase.firestore

    fun test(){
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("EEE", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("EEE", "Error getting documents.", exception)
            }
    }
}