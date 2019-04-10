package com.example.testmedia.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

public abstract class PlayerAdapter {

    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;

    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private boolean mAudioNoisyReceiverRegistered = false;
    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                        if(isPlaying()){
                            pause();
                        }
                    }
                }
            };

    private final Context mApplicationContext;
    private final AudioFocusHelper mAudioFocusHelper;
    private final AudioManager mAudioManager;

    public PlayerAdapter(Context context) {
        mApplicationContext = context;
        mAudioFocusHelper = new AudioFocusHelper();
        mAudioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
    }


    /**
     * Helper class for managing audio focus related tasks.
     */
    private final class AudioFocusHelper
            implements AudioManager.OnAudioFocusChangeListener {

        private boolean requestAudioFocus() {
            final int result = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        private void abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPlayOnAudioFocus && !isPlaying()) {
                        play();
                    } else if (isPlaying()) {
                        setVolume(MEDIA_VOLUME_DEFAULT);
                    }
                    mPlayOnAudioFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    setVolume(MEDIA_VOLUME_DUCK);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        mPlayOnAudioFocus = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioManager.abandonAudioFocus(this);
                    mPlayOnAudioFocus = false;
                    stop();
                    break;
            }
        }
    }
}
