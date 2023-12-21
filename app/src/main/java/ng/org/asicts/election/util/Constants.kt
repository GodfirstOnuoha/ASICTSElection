package ng.org.asicts.election.util

import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

object Constants {
    const val SHARED_PREF = "asict_vote"
    const val VOTED_COUNT = "voted_count"

    const val ASICT = "ASICT"
    const val ASICT_VOTING = "ASICT Voting"
    const val ELECTION = "Election"
    const val POSITION = "Position"
    const val ASPIRANTS = "Aspirants"
    const val VOTES = "Votes"
    const val CODES = "Codes"
    const val USED_CODES = "Used_Codes"
    const val STUDENTS = "Students"
    const val VOTED_STUDENTS = "Voted_Students"

    const val PRESIDENT = "President"
    const val VICE_PRESIDENT = "Vice President"
    const val SECRETARY_GENERAL = "Secretary General"
    const val FINANCIAL_SECRETARY = "Financial Secretary"
    const val ASSISTANT_SECRETARY_GENERAL = "Assistant Secretary General"
    const val TREASURER = "Treasurer"
    const val DIRECTOR_OF_INFORMATION = "Director of Information"
    const val DIRECTOR_OF_ICT = "Director of ICT"
    const val DIRECTOR_OF_WELFARE = "Director of Welfare"
    const val DIRECTOR_OF_SOCIAL = "Director of Social"
    const val DIRECTOR_OF_SPORTS = "Director of Sports"
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.shakeError(): TranslateAnimation {
    val shake = TranslateAnimation(0F, 10F, 0F, 0F)
    shake.duration = 500
    shake.interpolator = CycleInterpolator(7F)
    return shake
}

fun Context.vibrate() {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    val DELAY = 0
    val VIBRATE = 1000
    val SLEEP = 500
    val START = 0
    val vibratePattern = longArrayOf(DELAY.toLong(), VIBRATE.toLong(), SLEEP.toLong())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                500.toLong(),
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        // backward compatibility for Android API < 26
        // noinspection deprecation
        vibrator.vibrate(vibratePattern, START)
    }
}

fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}