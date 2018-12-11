package com.example.nanu.androidaudiovideo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,MediaPlayer.OnCompletionListener{


    private VideoView myVideo;
    private Button playBtn,btnPlayMusic,btnPauseMusic;

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private SeekBar volumeSeekBar,moveBackAndForthSeekBar;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing
        myVideo = findViewById(R.id.myVideo);
        playBtn = findViewById(R.id.playBtn);

        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        btnPauseMusic = findViewById(R.id.btnPauseMusic);
        volumeSeekBar = findViewById(R.id.seekBarVolume);
        moveBackAndForthSeekBar = findViewById(R.id.seekBarMove);

        playBtn.setOnClickListener(this);
        btnPlayMusic.setOnClickListener(this);
        btnPauseMusic.setOnClickListener(this);
        mediaController = new MediaController(this);
        mediaPlayer = MediaPlayer.create(this,R.raw.music);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);//downcasting

        int maxVolumeOfUserDevice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolumeOfUserDevice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(maxVolumeOfUserDevice);//setting seekbar to maximum volume i.e it can reach upto that volume
        volumeSeekBar.setProgress(currentVolumeOfUserDevice);//setting it to users current volume

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //int progress = current volume
                //boolean fromUser = if user is using the seekbar or not

                if(fromUser){
                   // Toast.makeText(MainActivity.this,progress+" ",Toast.LENGTH_SHORT).show();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        moveBackAndForthSeekBar.setOnSeekBarChangeListener(MainActivity.this);
        moveBackAndForthSeekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnCompletionListener(MainActivity.this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View btnView) {


        switch (btnView.getId()){

            case R.id.playBtn :
                playVideo();
                break;

            case R.id.btnPlayMusic:
                mediaPlayer.start();
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        moveBackAndForthSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                },0,1000);

                break;

            case  R.id.btnPauseMusic:
                mediaPlayer.pause();
                timer.cancel();
                break;

        }


    }

    private void playVideo(){
        Uri videoUri  = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cat);

        myVideo.setVideoURI(videoUri);

        myVideo.setMediaController(mediaController);

        mediaController.setAnchorView(myVideo);
        myVideo.start();
    }

    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param seekBar  The SeekBar whose progress has changed
     * @param progress The current progress level. This will be in the range min..max where min
     *                 and max were set by {@link ProgressBar#setMin(int)} and
     *                 {@link ProgressBar#setMax(int)}, respectively. (The default values for
     *                 min is 0 and max is 100.)
     * @param fromUser True if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            mediaPlayer.seekTo(progress);
        }
    }

    /**
     * Notification that the user has started a touch gesture. Clients may want to use this
     * to disable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mediaPlayer.pause();
    }

    /**
     * Notification that the user has finished a touch gesture. Clients may want to use this
     * to re-enable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        timer.cancel();
        Toast.makeText(this,"Music is ended",Toast.LENGTH_SHORT).show();
    }
}
