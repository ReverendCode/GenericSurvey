package com.vaporware.surveyapp

import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1


typealias BooleanUpdate = Pair<KMutableProperty1<ExampleSurvey, Boolean>, Boolean>


data class ExampleSurvey(
    override val surveyId: String = "0",
    var sampleText: String = "The answer is 42",
    var sampleBoolean: Boolean = false
): Survey<ExampleSurvey>()

private val textMap = mapOf(
    R.id.sampleEditText to ExampleSurvey::sampleText
)
private val booleanMap = mapOf(
    Pair(R.id.sampleCheckbox, true) to listOf(
        BooleanUpdate(ExampleSurvey::sampleBoolean, true)
    ),
    Pair(R.id.sampleCheckbox, false) to listOf(
        BooleanUpdate(ExampleSurvey::sampleBoolean, false)
    )
)

fun getUiMap() = hashMapOf<KProperty1<ExampleSurvey, *>,Int>(
    ExampleSurvey::sampleBoolean to R.id.sampleCheckbox,
    ExampleSurvey::sampleText to R.id.sampleEditText
)



abstract class Survey<T> (open val surveyId: String = "0") {

    fun updateFromView(view: View): Survey<T> {
        val updatedSurvey = this
        when (view) {
            is CheckBox -> {
                for ((location, updatedValue)
                in booleanMap[Pair(view.id,view.isChecked)]?: listOf()) {
                    location.set(updatedSurvey as ExampleSurvey,updatedValue)
                }
            }
            is EditText -> {
                val updatedText = view.editableText.toString()
                val location = textMap[view.id]
                location?.set(updatedSurvey as ExampleSurvey,updatedText)
            }
        }
        return updatedSurvey
    }

}