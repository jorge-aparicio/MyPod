package com.cs371m.mypod

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.cs371m.mypod.databinding.ActivityMainBinding
import com.cs371m.mypod.db.MyPodDatabase
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding

    // API Stuff
    private val viewModel: MainViewModel by viewModels()
    // Media Player
    private lateinit var mediaPlayer: MediaPlayer
    private var paused = true;
    private val userModifyingSeekBar = AtomicBoolean(false)
    private var playingEpisodeId = ""
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

        // Set up play bar controls
        binding.imageButton.setOnClickListener {
            if (paused) {
                paused = false;
                mediaPlayer.start();
                binding.imageButton.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
            } else {
                paused = true;
                mediaPlayer.pause();
                binding.imageButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                if(playingEpisodeId != "") viewModel.setProgress(playingEpisodeId, mediaPlayer.currentPosition)
            }
        }

        mediaPlayer.setOnCompletionListener {
            if(playingEpisodeId != "") {
                viewModel.setProgress(playingEpisodeId, 0)
                viewModel.setPlayed(playingEpisodeId, true)
                mediaPlayer.reset();
            }
        }
        // Check for podcast changes
        viewModel.observeCurrPlaying().observe(this) {
            if(playingEpisodeId != "") viewModel.setProgress(playingEpisodeId, mediaPlayer.currentPosition)
            if (it != null) {

                mediaPlayer.reset();
                playingEpisodeId = it.id

                // Set up playBar Details
                binding.pdTitle.text = it.podcastName;
                binding.epTitle.text = it.title;
                Glide.glideFetch(it.imageUrl.toString(), it.imageUrl.toString(), binding.rowImage)

                // Play the podcast
                playSong(it.audioUrl,it.progress);
                paused = false;
                binding.imageButton.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));

                // Set up seekbar
                binding.seekBar.max = mediaPlayer.duration / 1000;
                Log.d("######################", "Episode duration in seconds: ${binding.seekBar.max}")

            }
        }

        // Seekbar progress
        val millisec = 100L

        // Handle the user modifying the seek bar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                userModifyingSeekBar.set(true);
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(seekBar.progress * 1000);
                Log.d("######################", "durs: ${mediaPlayer.currentPosition}")
                userModifyingSeekBar.set(false);
            }
        })

        // Update seekbar over time
        lifecycleScope.launch {
            displayTime(millisec, binding.seekBar);
        }

    }

    // Play a song using the audio url
    private fun playSong(audioURL: String, progress: Int) {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(audioURL)
        mediaPlayer.prepare();
        mediaPlayer.seekTo(progress)
        mediaPlayer.start();
    }

    // Coroutine that modifies seekbar
    private suspend fun displayTime(misc: Long, seekBar: SeekBar) {
        // While the coroutine is running and has not been canceled by its parent
        while (true) {
            if (!userModifyingSeekBar.get()) seekBar.progress = mediaPlayer.currentPosition / 1000;
            if (seekBar.progress == seekBar.max) {
                paused = true;
                binding.imageButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
            };
            delay(misc)
        }
    }

}