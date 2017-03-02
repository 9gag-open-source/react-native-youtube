package com.inprogress.reactnativeyoutube;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * Created by raymond on 21/2/2017.
 *
 * reference: https://github.com/jsarjeant/streemd/blob/ece483c3d1f54380c7bc2b86e71dc36082f5dd7c/src/com/example/streemd/UserFeed_VP.java
 */
public class HackyYouTubePlayerFragment extends YouTubePlayerFragment {
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        try {
            super.onSaveInstanceState(bundle);
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {

        }
    }
}
