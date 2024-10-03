package ru.sevagrbnv.test_osm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.main)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.main, MapFragment())
                .commit()
    }
}