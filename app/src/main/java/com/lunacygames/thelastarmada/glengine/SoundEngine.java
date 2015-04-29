package com.lunacygames.thelastarmada.glengine;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by tron on 4/19/15.
 */
public class SoundEngine {

    private MediaPlayer bgMusic;
    private MediaPlayer soundEffect;
    private String playback;

    private static SoundEngine ourInstance = new SoundEngine();
    private Context context;

    public static SoundEngine getInstance() {
        return ourInstance;
    }

    private SoundEngine() {
        bgMusic = new MediaPlayer();
        soundEffect = new MediaPlayer();
        playback = "";
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void playBGMusic(String file) {
        if(playback.equalsIgnoreCase(file)) return;
        playback = file;
        bgMusic.release();
        try {
            AssetFileDescriptor fildes = context.getAssets().openFd(file);
            bgMusic = new MediaPlayer();
            bgMusic.setDataSource(fildes.getFileDescriptor(),
                    fildes.getStartOffset(), fildes.getLength());
            bgMusic.prepare();
            bgMusic.setLooping(true);
            bgMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playSoundEffect(String file) {
        setPlayback(soundEffect, file, false);
    }

    private void
    setPlayback(MediaPlayer player, String file, boolean loop) {
        if(player != null) {
            Log.d("MediaPlayer: ", "Releasing MediaPlayer");
            player.release();
        }
        try {
            AssetFileDescriptor fildes = context.getAssets().openFd(file);
            player = new MediaPlayer();
            player.setDataSource(fildes.getFileDescriptor(),
                    fildes.getStartOffset(), fildes.getLength());
            player.prepare();
            player.setLooping(loop);
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        if(bgMusic.isPlaying()) bgMusic.pause();
        if(soundEffect.isPlaying()) soundEffect.pause();
    }

    public void onResume() {
        bgMusic.start();
        soundEffect.start();
    }
}
