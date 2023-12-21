package ng.org.asicts.election.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ng.org.asicts.election.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Base_Theme_ASICTSElection)
        setContentView(R.layout.activity_main)
    }
}