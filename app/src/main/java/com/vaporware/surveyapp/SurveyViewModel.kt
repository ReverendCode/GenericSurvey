package com.vaporware.surveyapp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log

class SurveyViewModel(app: Application): AndroidViewModel(app) {

    private val repository = ExampleSurveyRepository()
    private var surveyList = repository.getAllSurveys()

    fun getSurvey(surveyIdToFind: String) : LiveData<ExampleSurvey> {
        return Transformations.map(surveyList) {
            it.find { survey ->
                survey.surveyId == surveyIdToFind
            }
        }
    }

    fun createSurvey() = repository.createSurvey()


    fun updateSurvey(updatedExampleSurvey: ExampleSurvey) {
        Log.d("updateSurvey", "Updating survey: $updatedExampleSurvey")
        repository.updateSurvey(updatedExampleSurvey)
    }

    fun getFirstOrNewId(id: String): String {
        val foo = surveyList.value!!
        if (foo.first().surveyId != id) return foo.first().surveyId
        if (foo.size > 1) return foo.last().surveyId
        return createSurvey()
    }

    fun deleteSurvey(toRemove: String) {
        repository.deleteSurvey(toRemove)
    }

    fun getCurrentId() : LiveData<String> {
        val someState = repository.getStateObject()
        return Transformations.map(someState) {
            it.currentId
        }
    }

    fun setCurrentId(id: String) {
     repository.setCurrentId(State(id))
    }

}

data class State (
    val currentId: String = "UNSET"
)