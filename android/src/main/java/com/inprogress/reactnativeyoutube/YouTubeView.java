package com.inprogress.reactnativeyoutube;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;


public class YouTubeView extends RelativeLayout {

    private static final String TAG = "YouTubeView";

    YouTubePlayerController youtubeController;
    YouTubeStateListener youtubeStateListener;
    private YouTubePlayerFragment youTubePlayerFragment;
    public static String youtube_key;

    public YouTubeView(Context context) {
        super(context);
        init();
    }

    public YouTubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YouTubeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
    public YouTubeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private ReactContext getReactContext() {
        return (ReactContext)getContext();
    }

    public void init() {
        inflate(getContext(), R.layout.youtube_layout, this);
        youtubeController = new YouTubePlayerController(YouTubeView.this);
    }

    public void bindFragment() {
        FragmentManager fragmentManager;
        if (isReactContext()) {
            fragmentManager = getReactContext().getCurrentActivity().getFragmentManager();
        } else {
            fragmentManager = ((Activity) getContext()).getFragmentManager();
        }

        youTubePlayerFragment = (YouTubePlayerFragment) fragmentManager
                .findFragmentById(R.id.youtubeplayerfragment);
        youTubePlayerFragment.initialize(youtube_key, youtubeController);
    }


    @Override
    protected void onDetachedFromWindow() {
        try {
            youtubeController.destroy();
            FragmentManager fragmentManager;
            if (isReactContext()) {
                fragmentManager = getReactContext().getCurrentActivity().getFragmentManager();
            } else {
                fragmentManager = ((Activity) getContext()).getFragmentManager();
            }
            youTubePlayerFragment = (YouTubePlayerFragment)
                    fragmentManager.findFragmentById(R.id.youtubeplayerfragment);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(youTubePlayerFragment);
            ft.commit();
        } catch (Exception e) {
            Log.e(TAG, "onDetachedFromWindow: ", e);
        }
        super.onDetachedFromWindow();
    }

    public void seekTo(int second) {
        youtubeController.seekTo(second);
    }


    public void playerViewDidBecomeReady(YouTubePlayer player) {
        try {
            WritableMap event = Arguments.createMap();
            event.putInt("target", getId());
            event.putInt("videoLength", player.getDurationMillis());

            if (isReactContext()) {
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "ready", event);
            }

            if (youtubeStateListener != null) {
                youtubeStateListener.onYoutubeVideoReady(event);
            }

        } catch (Exception e) {

        }
    }

    public void didOnSeekTo(int fromTime, int toTime) {
        try {
            WritableMap event = Arguments.createMap();
            event.putInt("seekFrom", fromTime);
            event.putInt("seekTo", toTime);
            if (isReactContext()) {
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "seek", event);
            }
            if (youtubeStateListener != null) {
                youtubeStateListener.onYoutubeVideoSeekTo(event);
            }
        } catch (Exception e) {

        }
    }

    public void didChangeToState(String param, YouTubePlayer youTubePlayer) {
        try {
            WritableMap event = Arguments.createMap();
            event.putString("state", param);
            event.putInt("target", getId());
            if (youTubePlayer != null) {
                event.putInt("videoLength", youTubePlayer.getDurationMillis());
                event.putInt("currentTime", youTubePlayer.getCurrentTimeMillis());
            }

            if (isReactContext()) {
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "state", event);
            }

            if (youtubeStateListener != null) {
                youtubeStateListener.onYoutubeVideoChangeState(event);
            }
        } catch (Exception e) {
            Log.e(TAG, "didChangeToState: ", e);
        }
    }


    public void didChangeToQuality(String param) {
        try {
            WritableMap event = Arguments.createMap();
            event.putString("quality", param);
            event.putInt("target", getId());
            if (isReactContext()) {
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "quality", event);
            }

            if (youtubeStateListener != null) {
                youtubeStateListener.onYoutubeVideoChangeQuality(event);
            }
        } catch (Exception e) {
            Log.e(TAG, "didChangeToQuality: ", e);
        }
    }


    public void didPlayTime(int current, int duration) {
        try {
            WritableMap event = Arguments.createMap();
            event.putInt("currentTime", current);
            event.putInt("videoLength", duration);
            event.putInt("target", getId());

            if (isReactContext()) {
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "progress", event);
            }

            if (youtubeStateListener != null) {
                youtubeStateListener.onYoutubeVideoProgress(event);
            }
        } catch (Exception e) {
            Log.e(TAG, "didPlayTime: ", e);
        }
    }


    public void receivedError(String param) {
        try {
            WritableMap event = Arguments.createMap();
            event.putString("error", param);
            event.putInt("target", getId());

            if (isReactContext()) {
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "error", event);
            }

            if (youtubeStateListener != null) {
                youtubeStateListener.onYoutubeVideoError(event);
            }
        } catch (Exception e) {
            Log.e(TAG, "receivedError: ", e);
        }
    }


    public void setVideoId(String str) {
        youtubeController.setVideoId(str);
    }

    public void setInline(Boolean bool) {
        youtubeController.setPlayInline(bool);
    }

    public void setShowInfo(Boolean bool) {
        youtubeController.setShowInfo(bool);
    }

    public void setModestbranding(Boolean bool) {
        youtubeController.setModestBranding(bool);
    }

    public void setControls(Integer nb) {
        youtubeController.setControls(nb);
    }

    public void setPlay(Boolean bool) {
        youtubeController.setPlay(bool);
    }

    public void setHidden(Boolean bool) {
        youtubeController.setHidden(bool);
    }

    public void setApiKey(String apiKey) {
        youtube_key = apiKey;
    }

    public void setLoop(Boolean loop) {
        youtubeController.setLoop(loop);
    }

    public void setRelated(Boolean related) {
        youtubeController.setRelated(related);
    }

    public void setFullscreen(Boolean bool) {
        youtubeController.setFullscreen(bool);
    }

    public void setStartTime(Integer startTime) {
        youtubeController.setStartTime(startTime);
    }

    public void setYoutubeStateListener(YouTubeStateListener youtubeStateListener) {
        this.youtubeStateListener = youtubeStateListener;
    }

    public int getCurrentTime() {
        return youtubeController.getCurrentTime();
    }

    private boolean isReactContext() {
        return getContext() instanceof ReactContext;
    }
}
