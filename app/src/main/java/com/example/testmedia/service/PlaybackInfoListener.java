package com.example.testmedia.service;

import android.support.v4.media.session.PlaybackStateCompat;

public abstract class PlaybackInfoListener {

    public abstract void onPlaybackStateChange(PlaybackStateCompat stateCompat);

    public void onPlaybackCompleted(){

    };
}
