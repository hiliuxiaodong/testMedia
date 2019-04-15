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
    private boolean mPlayOnAudioFocus = false;
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


    public abstract boolean isPlaying();
    protected abstract void onPlay();
    protected abstract void onPause();
    protected abstract void onStop();

    public final void play(){
        if(mAudioFocusHelper.requestAudioFocus()){
            registerAudioReceiver();
            onPlay();
        }
    }

    public final void pause(){
        if(!mPlayOnAudioFocus){
            mAudioFocusHelper.abandonAudioFocus();
        }
        unRegisterAudioReceiver();
        onPause();
    }

    public final void stop(){
        mAudioFocusHelper.abandonAudioFocus();
        unRegisterAudioReceiver();
        onStop();
    }

    public abstract void seekTo(int position);
    public abstract void setVolume(float volume);

    private void registerAudioReceiver(){
        if(!mAudioNoisyReceiverRegistered){
            mApplicationContext.registerReceiver(mAudioNoisyReceiver,AUDIO_NOISY_INTENT_FILTER);
            mAudioNoisyReceiverRegistered = true;
        }
    }
    private void unRegisterAudioReceiver(){
        if(mAudioNoisyReceiverRegistered){
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    /**
     * Helper class for managing audio focus related tasks.
     */
    private final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

        public AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(this)
                .build();

        private boolean requestAudioFocus() {
            final int result = mAudioManager.requestAudioFocus(focusRequest);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        private void abandonAudioFocus() {
            mAudioManager.abandonAudioFocusRequest(focusRequest);
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
                    abandonAudioFocus();
                    mPlayOnAudioFocus = false;
                    stop();
                    break;
            }
        }
    }
}
