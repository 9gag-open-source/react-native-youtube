package com.inprogress.reactnativeyoutube;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.inprogress.reactnativeyoutube.event.VideoErrorEvent;
import com.inprogress.reactnativeyoutube.event.VideoSeekEvent;
import com.inprogress.reactnativeyoutube.event.VideoStateEvent;

/**
 * Created by raymond on 20/2/2017.
 */

public class YoutubePlayerActivity extends ReactActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = "YoutubePlayerActivity";

    private YouTubeView youtubeView;
    private int startTs;
    private EventDispatcher mEventDispatcher;
    private boolean isFinishByBack;
    private boolean hasSeekTriggered;
    private boolean isBufferingFirstTime = true;
    private boolean isAfterBuffering;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        mEventDispatcher = getReactInstanceManager().getCurrentReactContext().getNativeModule(UIManagerModule.class).getEventDispatcher();
        youtubeView = (YouTubeView) findViewById(R.id.youtubeView);

        final Intent i = getIntent();
        final boolean loop;
        final boolean autoPlay;
        final int originalStartTs = i.getIntExtra(YouTubeManager.PROP_START_TIME, 0);

        final int viewId = i.getIntExtra(YouTubeManager.PROP_INTERNAL_VIEW_ID, 0);

        if (savedInstanceState != null) {
            startTs = savedInstanceState.getInt(YouTubeManager.PROP_START_TIME);
        } else {
            startTs = originalStartTs;
        }

        youtubeView.setApiKey(i.getStringExtra(YouTubeManager.PROP_API_KEY));
        youtubeView.setVideoId(i.getStringExtra(YouTubeManager.PROP_VIDEO_ID));
        youtubeView.setPlay(autoPlay = i.getBooleanExtra(YouTubeManager.PROP_PLAY, false));
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
                // Log.d(TAG, "onYoutubeVideoChangeState: " + readableMap);

                String state = readableMap.hasKey("state") ? readableMap.getString("state") : "";
                switch (state) {
                    case "playing": {
                        // TODO: 22/2/2017
                        if ((!hasSeekTriggered && !isAfterBuffering) || isBufferingFirstTime) {
                            int videoLength;
                            int currentVideoTime = readableMap.getInt("currentTime") / 1000;
                            int endTime = videoLength = readableMap.getInt("videoLength") / 1000; // TODO: End time suppose to be able to specify in the future
                            mEventDispatcher.dispatchEvent(new VideoStateEvent(viewId, currentVideoTime,
                                    videoLength, originalStartTs, endTime,
                                    autoPlay, VideoStateEvent.STATE_PLAYING));
                        }
                        isBufferingFirstTime = false;
                        // So, after first "playing" is triggered, we know that it has called "buffering" state, mark as false here
                        isAfterBuffering = false;
                        hasSeekTriggered = false;
                        break;
                    }
                    case "buffering": {
                        isAfterBuffering = true;
                        break;
                    }
                    case "paused": {
                        int videoLength;
                        int currentVideoTime = readableMap.getInt("currentTime") / 1000;
                        int endTime = videoLength = readableMap.getInt("videoLength") / 1000; // TODO: End time suppose to be able to specify in the future

                        String ouputEventName = null;
                        if (isFinishByBack) {
                            ouputEventName = VideoStateEvent.STATE_CANCELLED;
                        } else {
                            ouputEventName = VideoStateEvent.STATE_PAUSED;
                        }

                        if (currentVideoTime < videoLength) {
                            mEventDispatcher.dispatchEvent(new VideoStateEvent(viewId, currentVideoTime,
                                    videoLength, originalStartTs, endTime,
                                    autoPlay, ouputEventName));
                        }

                        break;
                    }
                    case "ended": {
                        if (!loop) {
                            int videoLength;
                            int currentVideoTime = readableMap.getInt("currentTime") / 1000;
                            int endTime = videoLength = readableMap.getInt("videoLength") / 1000; // TODO: End time suppose to be able to specify in the future
                            mEventDispatcher.dispatchEvent(new VideoStateEvent(viewId, currentVideoTime,
                                    videoLength, originalStartTs, endTime,
                                    autoPlay, VideoStateEvent.STATE_ENDED));
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
                mEventDispatcher.dispatchEvent(new VideoErrorEvent(viewId, readableMap.getString("error")));
            }

            @Override
            public void onYoutubeVideoProgress(ReadableMap readableMap) {
                /**
                 * {@link YouTubeView#didPlayTime(int, int)} The time is in millisecond, change it to second
                 */
                int currentVideoTime = readableMap.getInt("currentTime") / 1000;
                int videoLength;
                int endTime = videoLength = readableMap.getInt("videoLength") / 1000;


                mEventDispatcher.dispatchEvent(new VideoStateEvent(viewId, currentVideoTime,
                        videoLength, originalStartTs, endTime,
                        autoPlay, VideoStateEvent.STATE_PROGRESS));
            }

            @Override
            public void onYoutubeVideoSeekTo(ReadableMap readableMap) {
                int seekFrom = readableMap.getInt("seekFrom") / 1000;
                int seekTo = readableMap.getInt("seekTo") / 1000;
                mEventDispatcher.dispatchEvent(new VideoSeekEvent(viewId, seekFrom, seekTo));

                hasSeekTriggered = true;
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
            youtubeView.setFullscreen(false);
        } else {
            isFinishByBack = true;
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
