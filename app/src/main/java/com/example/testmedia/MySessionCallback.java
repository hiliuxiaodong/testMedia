package com.example.testmedia;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.support.v4.media.session.MediaSessionCompat;

public class MySessionCallback extends MediaSessionCompat.Callback {

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private MediaSessionCompat mediaSessionCompat;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private AudioFocusRequest audioFocusRequest;
    private static  Context mContext;

    public MySessionCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onPlay() {
        super.onPlay();
    }

    @Override
    public void onStop() {
//        super.onStop();
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .setAudioAttributes(audioAttributes)
                .build();

        int result = audioManager.requestAudioFocus(audioFocusRequest);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            //start the service
            mContext.startService(new Intent(mContext,MediaBrowserService.class));
            //set the session active (and update metadata and state)
            mediaSessionCompat.setActive(true);
            //start the player (custom call)

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
