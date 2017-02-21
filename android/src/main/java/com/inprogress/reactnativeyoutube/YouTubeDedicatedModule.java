package com.inprogress.reactnativeyoutube;

import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

/**
 * Created by raymond on 20/2/2017.
 */
@ReactModule(name = YouTubeDedicatedModule.REACT_CLASS)
public class YouTubeDedicatedModule extends ReactContextBaseJavaModule {

    static final String REACT_CLASS = "RNYoutubeModuleAndroid";

    public YouTubeDedicatedModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void startYouTube(ReadableMap map) {
        String apiKey = map.hasKey(YouTubeManager.PROP_API_KEY)
                ? map.getString(YouTubeManager.PROP_API_KEY)
                : "";
        String videoId = map.hasKey(YouTubeManager.PROP_VIDEO_ID)
                ? map.getString(YouTubeManager.PROP_VIDEO_ID)
                : "";
        boolean play = map.hasKey(YouTubeManager.PROP_PLAY)
                && map.getBoolean(YouTubeManager.PROP_PLAY);
        boolean hidden = map.hasKey(YouTubeManager.PROP_HIDDEN)
                && map.getBoolean(YouTubeManager.PROP_HIDDEN);
        boolean inline = map.hasKey(YouTubeManager.PROP_INLINE)
                && map.getBoolean(YouTubeManager.PROP_INLINE);
        boolean rel = map.hasKey(YouTubeManager.PROP_REL)
                && map.getBoolean(YouTubeManager.PROP_REL);
        boolean modestBranding = map.hasKey(YouTubeManager.PROP_MODESTBRANDING)
                && map.getBoolean(YouTubeManager.PROP_MODESTBRANDING);
        boolean loop = map.hasKey(YouTubeManager.PROP_LOOP)
                && map.getBoolean(YouTubeManager.PROP_LOOP);
        /**
         * 1 is the youtube default theme, check {@link YouTubePlayerController#updateControls()}
         */
        int controls = map.hasKey(YouTubeManager.PROP_CONTROLS)
                ? map.getInt(YouTubeManager.PROP_CONTROLS)
                : 1;

        boolean showInfo = !map.hasKey(YouTubeManager.PROP_SHOW_INFO)
                || map.getBoolean(YouTubeManager.PROP_SHOW_INFO); // Default showInfo
        boolean fullScreen = !map.hasKey(YouTubeManager.PROP_FULLSCREEN)
                || map.getBoolean(YouTubeManager.PROP_FULLSCREEN); // Default fullscreen


        Intent intent = new Intent(getCurrentActivity(), YoutubePlayerActivity.class);

        intent.putExtra(YouTubeManager.PROP_API_KEY, apiKey);
        intent.putExtra(YouTubeManager.PROP_VIDEO_ID, videoId);
        intent.putExtra(YouTubeManager.PROP_PLAY, play);
        intent.putExtra(YouTubeManager.PROP_HIDDEN, hidden);
        intent.putExtra(YouTubeManager.PROP_INLINE, inline);
        intent.putExtra(YouTubeManager.PROP_REL, rel);
        intent.putExtra(YouTubeManager.PROP_MODESTBRANDING, modestBranding);
        intent.putExtra(YouTubeManager.PROP_LOOP, loop);
        intent.putExtra(YouTubeManager.PROP_CONTROLS, controls);
        intent.putExtra(YouTubeManager.PROP_SHOW_INFO, showInfo);
        intent.putExtra(YouTubeManager.PROP_FULLSCREEN, fullScreen);

        getCurrentActivity().startActivity(intent);
    }
}
