package com.example.hp.vnlt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;

/**
 * Author   : Vo Dang Phuc
 * ID       : 51303080
 * Email    : dennisphuc@gmail.com
 * */
public class SplashActivity extends AppCompatActivity {

    MediaPlayer mp;

    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mp = MediaPlayer.create(this, R.raw.ringtone);
        mp.start();

        try {
            if (isConnected()) {
                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        }
                    }
                };
                timer.start();
            } else {
                Toast.makeText(this,"Xin hãy kết nối internet để dùng ứng dụng!",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
