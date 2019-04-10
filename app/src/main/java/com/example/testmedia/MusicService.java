package com.example.testmedia;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {

    private static final String LOG_TAG = "media_browser";

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder Statebuilder;
    private Activity mActivity;

    public MusicService(Activity activity) {
        mActivity = activity;
    }

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this,LOG_TAG);
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMediaButtonReceiver(null);

        Statebuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE);

        mediaSession.setPlaybackState(Statebuilder.build());
        mediaSession.setCallback(new MySessionCallback(this));

        MediaControllerCompat mediaControllerCompat = new MediaControllerCompat(this,mediaSession);
        MediaControllerCompat.setMediaController(mActivity, mediaControllerCompat);
        setSessionToken(mediaSession.getSessionToken());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String s, int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String s, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }
}
