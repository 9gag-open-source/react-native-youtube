package com.inprogress.reactnativeyoutube;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ReadableMap;

/**
 * Created by raymond on 20/2/2017.
 */

public class YoutubePlayerActivity extends ReactActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = "YoutubePlayerActivity";
    private YouTubeView youtubeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        youtubeView = (YouTubeView) findViewById(R.id.youtubeView);

        final Intent i = getIntent();
        youtubeView.setApiKey(i.getStringExtra(YouTubeManager.PROP_API_KEY));
        youtubeView.setVideoId(i.getStringExtra(YouTubeManager.PROP_VIDEO_ID));
        youtubeView.setPlay(i.getBooleanExtra(YouTubeManager.PROP_PLAY, false));
        youtubeView.setHidden(i.getBooleanExtra(YouTubeManager.PROP_HIDDEN, false));
        youtubeView.setInline(i.getBooleanExtra(YouTubeManager.PROP_INLINE, false));
        youtubeView.setRelated(i.getBooleanExtra(YouTubeManager.PROP_REL, false));
        youtubeView.setModestbranding(i.getBooleanExtra(YouTubeManager.PROP_MODESTBRANDING, false));
        youtubeView.setLoop(i.getBooleanExtra(YouTubeManager.PROP_LOOP, false));
        youtubeView.setControls(i.getIntExtra(YouTubeManager.PROP_CONTROLS, 1));
        youtubeView.setShowInfo(i.getBooleanExtra(YouTubeManager.PROP_SHOW_INFO, true));
        youtubeView.setFullscreen(i.getBooleanExtra(YouTubeManager.PROP_FULLSCREEN, true));
        youtubeView.setStartTime(i.getIntExtra(YouTubeManager.PROP_START_TIME, 0));
        youtubeView.setYoutubeStateListener(new YouTubeStateListener() {
            @Override
            public void onYoutubeVideoReady(ReadableMap readableMap) {

            }

            @Override
            public void onYoutubeVideoChangeState(ReadableMap readableMap) {
                if ("videoStarted".equals(readableMap.getString("state"))) {
                    int startTs = i.getIntExtra(YouTubeManager.PROP_START_TIME, 0);
                    if (startTs != 0) {
                        youtubeView.seekTo(startTs);
                    }
                }
            }

            @Override
            public void onYoutubeVideoChangeQuality(ReadableMap readableMap) {

            }

            @Override
            public void onYoutubeVideoError(ReadableMap readableMap) {

            }
        });

        youtubeView.bindFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
