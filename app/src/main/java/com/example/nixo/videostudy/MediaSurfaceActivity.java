package com.example.nixo.videostudy;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

public class MediaSurfaceActivity extends AppCompatActivity {


    private RelativeLayout mRlcontiner;
    private SurfaceView mSurfaceView;
    private MediaPlayer mPlayer;
    private boolean mIsStart;
    private SeekBar mSeekBar;
    private Button mPauseBtn;
    private boolean mIsPause;
    private int mBiLiHW;
    private Handler mHandler = new Handler();
    private Runnable mProgressUpDataRunnable = new Runnable() {
        @Override
        public void run() {
            if(mPlayer == null){
                return;
            }
            long currentPosition = mPlayer.getCurrentPosition();
            int duration = mPlayer.getDuration();
            if(mSeekBar != null && duration > 0){
                int progress = (int) (currentPosition*1.0f/duration * 1000);
                mSeekBar.setProgress(progress);
                if(mPlayer.isPlaying()){
                    mHandler.postDelayed(mProgressUpDataRunnable,1000);
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_surface);

        initView();
        initMediaPlayer();
        initEvent();


    }

    private void initEvent() {
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlayer.setDisplay(holder);
                if(!mIsStart){
                    return;
                }
                if(mIsPause){
                    mPlayer.seekTo(mPlayer.getCurrentPosition());
                    return;
                }

                if(!mPlayer.isPlaying()){
                    mPlayer.start();
                    mHandler.post(mProgressUpDataRunnable);

                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsPause = true;
                mHandler.removeCallbacks(mProgressUpDataRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long duration = mPlayer.getDuration();
                int target = (int) (seekBar.getProgress()*1.0f/1000 *duration);
                mPlayer.seekTo(target);
                if(mPlayer.isPlaying()){
                    mIsPause = false;
                    mHandler.post(mProgressUpDataRunnable);
                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPauseBtn.setText("播放");
                mHandler.removeCallbacks(mProgressUpDataRunnable);
                mSeekBar.setProgress(1000);

            }
        });
    }

    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        File file = new File(Environment.getExternalStorageDirectory() + "/baka.mp4");
        String path = file.getAbsolutePath();
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mIsStart = true;

                    if(!mPlayer.isPlaying()){
                        mPlayer.start();
                        mIsPause = false;
                        mHandler.post(mProgressUpDataRunnable);
                    }

                }
            });

            mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    ViewGroup.LayoutParams lp = mRlcontiner.getLayoutParams();
                    mBiLiHW  = (int) (height *1.0f / width);
                    lp.height = (int) (lp.width * 1.0f/width *height);
                    mRlcontiner.setLayoutParams(lp);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        mRlcontiner = findViewById(R.id.id_mRlcontainer);
        mSurfaceView = findViewById(R.id.id_surface);
        mSeekBar = findViewById(R.id.id_SeekBar);
        mSeekBar.setMax(1000);
        mPauseBtn = findViewById(R.id.id_pauseBtn);
        mPauseBtn.setText("暂停");
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    mIsPause = true;
                    mPauseBtn.setText("播放");
                    mHandler.removeCallbacks(mProgressUpDataRunnable);
                }else{
                    mPauseBtn.setText("暂停");
                    mIsPause = false;
                    mHandler.post(mProgressUpDataRunnable);
                    mPlayer.start();
                }

            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewTreeObserver observer = mRlcontiner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRlcontiner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams lp = mRlcontiner.getLayoutParams();
                lp.height = mBiLiHW*mRlcontiner.getWidth() ;
                mRlcontiner.setLayoutParams(lp);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
        mIsPause = true;
        mPauseBtn.setText("播放");
        mHandler.removeCallbacks(mProgressUpDataRunnable);
    }

    public static void start(Context context){
        Intent intent = new Intent(context,MediaSurfaceActivity.class);
        context.startActivity(intent);
    }

}
