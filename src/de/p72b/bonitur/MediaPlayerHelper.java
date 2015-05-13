package de.p72b.bonitur;

import android.media.MediaPlayer;

public class MediaPlayerHelper {
    public final int ERR = 0;
    ActivityBonitur mainActivity;


    public MediaPlayerHelper(ActivityBonitur act) {
        mainActivity = act;
    };


    public void play(int title) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mainActivity, R.raw.smb_bump);
        mediaPlayer.start();
    };
};
