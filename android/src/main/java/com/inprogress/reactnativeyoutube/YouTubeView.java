package com.inprogress.reactnativeyoutube;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.youtube.player.YouTubePlayerFragment;


public class YouTubeView extends RelativeLayout {

    YouTubePlayerController youtubeController;
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
        if (getContext() instanceof ThemedReactContext) {
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
            FragmentManager fragmentManager = getReactContext().getCurrentActivity().getFragmentManager();
            youTubePlayerFragment = (YouTubePlayerFragment)
                    fragmentManager.findFragmentById(R.id.youtubeplayerfragment);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(youTubePlayerFragment);
            ft.commit();
        } catch (Exception e) {
        }
        super.onDetachedFromWindow();
    }

    public void seekTo(int second) {
        youtubeController.seekTo(second);
    }


    public void playerViewDidBecomeReady() {
        try {
            WritableMap event = Arguments.createMap();
            ReactContext reactContext = (ReactContext) getContext();
            event.putInt("target", getId());
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "ready", event);
        } catch (Exception e) {

        }
    }


    public void didChangeToState(String param) {
        try {
            WritableMap event = Arguments.createMap();
            event.putString("state", param);
            event.putInt("target", getId());
            ReactContext reactContext = (ReactContext) getContext();
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "state", event);
        } catch (Exception e) {

        }
    }


    public void didChangeToQuality(String param) {
        WritableMap event = Arguments.createMap();
        event.putString("quality", param);
        event.putInt("target", getId());
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "quality", event);
    }


    public void didPlayTime(String current, String duration) {
        WritableMap event = Arguments.createMap();
        event.putString("currentTime", current);
        event.putString("duration", duration);
        event.putInt("target", getId());
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "progress", event);
    }


    public void receivedError(String param) {
        WritableMap event = Arguments.createMap();
        ReactContext reactContext = (ReactContext) getContext();
        event.putString("error", param);
        event.putInt("target", getId());
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "error", event);
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
}
