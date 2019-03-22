package com.vaporware.surveyapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.reflect.full.memberProperties

class MainActivity : AppCompatActivity() {

     private val tag = "MainActivity"
     private lateinit var viewModel: SurveyViewModel
     private var currentExampleSurvey: ExampleSurvey? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProviders.of(this).get(SurveyViewModel::class.java)

        //this is ugly, is there a better way?
        val prefs = getDefaultSharedPreferences(applicationContext)
        if (!prefs.getBoolean("hasRun",false)) {
            viewModel.setCurrentId(viewModel.createSurvey())
                prefs.edit().putBoolean("hasRun",true).apply()
        }


        viewModel.getCurrentId().observe(this, Observer {
            Log.d(tag, "observing id: $it")
            if (it == "UNSET") viewModel.setCurrentId(viewModel.createSurvey())
            else viewModel.getSurvey(it!!).observe(this, Observer { curr ->
                currentExampleSurvey = curr
                if (curr != null) updateUi(curr)
            })
        })

        fab.setOnClickListener {
            viewModel.setCurrentId(viewModel.createSurvey())
            Toast.makeText(applicationContext,"New Survey Created",Toast.LENGTH_SHORT).show()
        }

        val map = getUiMap()
        for ((_, viewId) in map) {
            val thisView = findViewById<View>(viewId)
            when (thisView) {
                is EditText -> {
                    thisView.addTextChangedListener(
                        DebouncedEditTextChangeListener {
                            Log.d("onChange","The function is firing: ${thisView.text}")
                            viewModel.updateSurvey(currentExampleSurvey!!.updateFromView(thisView) as ExampleSurvey)
                        }
                    )
                }
            }
        }
    }

    private fun setText(textField: EditText, info: String) {
        textField.setText(info)
        textField.setSelection(textField.length())
    }

    fun handleClick(view: View) {
        viewModel.updateSurvey((currentExampleSurvey?.updateFromView(view) as ExampleSurvey?)!!)
    }

    private fun updateUi(updatedExampleSurvey: ExampleSurvey) {
//    for each value in the Example survey, set the
// corresponding UI element value, as decided by the getUiMap()

        val map = getUiMap()
        for (property in ExampleSurvey::class.memberProperties) {
            val propertyValue = property.get(updatedExampleSurvey)
            Log.d("updateUI",map[property]?.toString()?: "$property is null")
            val thisView = findViewById<View>(map[property]?: 0)

            when (thisView) {
                is CheckBox -> thisView.isChecked = propertyValue as Boolean
                is EditText -> setText(thisView, propertyValue as String)
                is Button -> thisView.text = propertyValue as String
                else -> Log.d("updateUi","Returned 0, we don't know how to handle this")

            }
        }
    }

    fun deleteSurvey(item: MenuItem) {
        val curr = currentExampleSurvey
        val currentId = curr!!.surveyId
        val newId = viewModel.getFirstOrNewId(currentId)
        viewModel.setCurrentId(newId)
        Snackbar.make(this.currentFocus,"Deleting Survey",Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                Toast.makeText(applicationContext, "nevermind", Toast.LENGTH_SHORT).show()
                viewModel.setCurrentId(currentId)
            }.addCallback(DeleteSurveySnackbarCallback(viewModel, currentId)).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
