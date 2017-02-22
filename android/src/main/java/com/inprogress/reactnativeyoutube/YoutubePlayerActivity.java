package com.inprogress.reactnativeyoutube;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ReadableMap;

/**
 * Created by raymond on 20/2/2017.
 */

public class YoutubePlayerActivity extends ReactActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = "YoutubePlayerActivity";

    private YouTubeView youtubeView;
    private int startTs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        youtubeView = (YouTubeView) findViewById(R.id.youtubeView);

        final Intent i = getIntent();
        final boolean loop;

        if (savedInstanceState != null) {
            startTs = savedInstanceState.getInt(YouTubeManager.PROP_START_TIME);
        } else {
            startTs = i.getIntExtra(YouTubeManager.PROP_START_TIME, 0);
        }

        youtubeView.setApiKey(i.getStringExtra(YouTubeManager.PROP_API_KEY));
        youtubeView.setVideoId(i.getStringExtra(YouTubeManager.PROP_VIDEO_ID));
        youtubeView.setPlay(i.getBooleanExtra(YouTubeManager.PROP_PLAY, false));
        youtubeView.setHidden(i.getBooleanExtra(YouTubeManager.PROP_HIDDEN, false));
        youtubeView.setInline(i.getBooleanExtra(YouTubeManager.PROP_INLINE, false));
        youtubeView.setRelated(i.getBooleanExtra(YouTubeManager.PROP_REL, false));
        youtubeView.setModestbranding(i.getBooleanExtra(YouTubeManager.PROP_MODESTBRANDING, false));
        youtubeView.setLoop(loop = i.getBooleanExtra(YouTubeManager.PROP_LOOP, false));
        youtubeView.setControls(i.getIntExtra(YouTubeManager.PROP_CONTROLS, 1));
        youtubeView.setShowInfo(i.getBooleanExtra(YouTubeManager.PROP_SHOW_INFO, true));
        youtubeView.setFullscreen(i.getBooleanExtra(YouTubeManager.PROP_FULLSCREEN, true));
        youtubeView.setStartTime(startTs);
        youtubeView.setYoutubeStateListener(new YouTubeStateListener() {
            @Override
            public void onYoutubeVideoReady(ReadableMap readableMap) {

            }

            @Override
            public void onYoutubeVideoChangeState(ReadableMap readableMap) {
                String state = readableMap.hasKey("state") ? readableMap.getString("state") : "";
                switch (state) {
                    case "ended": {
                        if (!loop) {
                            finish();
                        }
                        break;
                    }
                    default:
                        break;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (youtubeView != null) {
            outState.putInt(YouTubeManager.PROP_START_TIME, youtubeView.getCurrentTime());
        }
    }

    @Override
    public void onBackPressed() {
        if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation ||
                ActivityInfo.SCREEN_ORIENTATION_USER == getResources().getConfiguration().orientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == newConfig.orientation ||
                ActivityInfo.SCREEN_ORIENTATION_USER == newConfig.orientation) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}
