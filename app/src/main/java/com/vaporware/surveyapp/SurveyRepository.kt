package com.vaporware.surveyapp

import android.arch.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.kiwimob.firestore.livedata.livedata

class ExampleSurveyRepository {

    private val db = FirebaseFirestore.getInstance()
        .collection("Surveys")
    private val state = FirebaseFirestore.getInstance()
        .collection("Info")

    fun createSurvey(): String {
        val surveyReference = db.document()
        val freshId = surveyReference.id
        surveyReference.set(ExampleSurvey(surveyId = freshId))
        return freshId
    }

    fun getAllSurveys():LiveData<List<ExampleSurvey>> {
        return db.livedata(ExampleSurvey::class.java)
    }

    fun updateSurvey(updatedExampleSurvey: ExampleSurvey) {
        db.document(updatedExampleSurvey.surveyId).set(updatedExampleSurvey)
    }

    fun deleteSurvey(toRemove: String) {
        db.document(toRemove).delete()
    }

    fun setCurrentId(newState: State) {
        state.document("data").set(newState)
    }

    fun createStateObject() {
        state.document("data").set(State())
    }

    fun getStateObject(): LiveData<State> {
        return state.document("data").livedata(State::class.java)
    }
}