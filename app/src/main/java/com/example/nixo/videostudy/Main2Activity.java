package com.example.nixo.videostudy;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class Main2Activity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController controller;
    private boolean isPause;
    private int mPosition;
    public static final String VIDEO_POSITION = "video_position";
    public static final String VIDEO_IS_PAUSE = "video_is_pause";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        videoView = findViewById(R.id.videoview);
        controller = findViewById(R.id.mediaController);
        controller = new MediaController(this);
        File file = new File(Environment.getExternalStorageDirectory().getPath(),"baka.mp4");
        Log.i("Nixo", file.getAbsolutePath());
        videoView.setVideoPath(file.getAbsolutePath());
        videoView.setMediaController(controller);


    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(mPosition);
        if(!isPause){
            videoView.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPosition = videoView.getCurrentPosition();
        isPause = !videoView.isPlaying();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPosition = savedInstanceState.getInt(VIDEO_POSITION);
        isPause = savedInstanceState.getBoolean(VIDEO_IS_PAUSE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(VIDEO_POSITION,mPosition);
        outState.putBoolean(VIDEO_IS_PAUSE,isPause);
    }

    public static void start(Context context){
        Intent intent = new Intent(context,Main2Activity.class);
        context.startActivity(intent);
    }
}
