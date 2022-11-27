package com.cs371m.mypod

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.cs371m.mypod.databinding.ActivityMainBinding
import com.cs371m.mypod.db.MyPodDatabase
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // API Stuff
    private val viewModel: MainViewModel by viewModels()
    // Media Player
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_subscriptions
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        viewModel.setNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.navigation_profile) {

                navView.visibility = View.GONE
            } else {

                navView.visibility = View.VISIBLE
            }
        }

        // Set up Media Player
        mediaPlayer = MediaPlayer();

        // Check for podcast changes
        viewModel.observeCurrPlaying().observe(this) {
            mediaPlayer.reset();
            if (it != null && it.size == 5) {

                // Set up playBar Details
                binding.pdTitle.text = it[0];
                binding.epTitle.text = it[1];
                Glide.glideFetch(it[2], it[2], binding.rowImage)

                // Get Episode duration
                val splitString = it[4].split(":");
                var hours = 0;
                var min = 0;
                var secs = 0;
                if (splitString.size == 3) hours = splitString[0].toInt();
                if (splitString.size == 3) min = splitString[1].toInt();
                else if (splitString.size == 2) min = splitString[0].toInt();
                if (splitString.size == 3) secs = splitString[2].toInt();
                else if (splitString.size == 2) secs = splitString[1].toInt();
                else secs = splitString[0].toInt()

                // Set up seekbar
                binding.seekBar.max = hours * 3600 + min * 60 + secs;
                Log.d("######################", "Episode duration in seconds: ${binding.seekBar.max}")

                playSong(it[3]);
            }
        }

        // Seekbar progress
        val millisec = 100L
        CoroutineScope(Dispatchers.Main).launch {
            displayTime(millisec, binding.seekBar)
        }

    }

    // Play a song using the audio url
    private fun playSong(audioURL: String) {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(audioURL)
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    // Coroutine that modifies seekbar
    private suspend fun displayTime(misc: Long, seekBar: SeekBar) {
        // While the coroutine is running and has not been canceled by its parent
        while (GlobalScope.coroutineContext.isActive) {
            seekBar.progress = mediaPlayer.currentPosition / 1000;
            // Leave this code as is.  it inserts a delay so that this thread does
            // not consume too much CPU
            delay(misc)
        }
    }

}