package com.vaporware.surveyapp

import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log


class DeleteSurveySnackbarCallback(private val viewModel: SurveyViewModel, private val survey: String?) : Snackbar.Callback() {
    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
        super.onDismissed(transientBottomBar, event)
        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
            Log.d("deleteSurvey","Actually delete the survey now")
            if (survey != null) viewModel.deleteSurvey(survey)
        }
    }
}


class DebouncedEditTextChangeListener(val timerLengthInMillis: Long = 600,
                                      val timerIntervalInMillis: Long = 100,
                                      val function: () -> Unit): TextWatcher {
    private var timer: CountDownTimer? = null

    override fun afterTextChanged(change: Editable?) {
        timer = object : CountDownTimer(timerLengthInMillis, timerIntervalInMillis) {

            override fun onTick(millisUntilFinished: Long) {
                //nothing to do here.
            }
            override fun onFinish() {
                function()
            }
        }.start()
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (timer != null) timer!!.cancel()
    }

}