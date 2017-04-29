package com.example.hp.vnlt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mp = MediaPlayer.create(this, R.raw.ringtone);
        mp.start();

        Thread timer = new Thread(){
          public void run() {
                try{
                    sleep(2000);
                }  catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()){
            mp.stop();
        }
        mp.release();
        this.finish();
    }
}
