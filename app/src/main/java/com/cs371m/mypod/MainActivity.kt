package com.cs371m.mypod

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cs371m.mypod.databinding.ActivityMainBinding
import com.cs371m.mypod.db.EpisodeDao
import com.cs371m.mypod.glide.Glide
import com.cs371m.mypod.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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


        val download: (EpisodeDao.Episode)->Unit = {
            val episode = it
            val manager =  getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager;
            val file ="MyPod/"+episode.id
//        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).toString() +file

            val request = DownloadManager.Request(Uri.parse(episode.audioUrl))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS,file) // Uri of the destination file
                .setTitle(episode.title) // Title of the Download Notification
                .setDescription("Downloading") // Description of the Download Notification
                .setRequiresCharging(false) // Set if charging is required to begin the download
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true)
            val downloadID = manager!!.enqueue(request)

            viewModel.downloadEpisode(episode,downloadID.toInt(),file)
            val onComplete: BroadcastReceiver = object : BroadcastReceiver() {

                override fun onReceive(ctxt: Context, intent: Intent) {
                    // your code
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        viewModel.setDownloaded(id.toInt())
                }
            }
            registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        viewModel.setDownloadFun(download)
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

        binding.frameLayout.visibility = GONE

        mediaPlayer.setOnCompletionListener {
            if(playingEpisodeId != "") {
                viewModel.setProgress(playingEpisodeId, 0)
                viewModel.setPlayed(playingEpisodeId, true)
                mediaPlayer.reset();
                binding.frameLayout.visibility = GONE

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
                binding.frameLayout.visibility = VISIBLE

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