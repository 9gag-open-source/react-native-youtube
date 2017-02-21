package com.inprogress.reactnativeyoutube;

import com.facebook.react.bridge.ReadableMap;

/**
 * Created by raymond on 21/2/2017.
 */

public interface YouTubeStateListener {
    void onYoutubeVideoReady(ReadableMap readableMap);
    void onYoutubeVideoChangeState(ReadableMap readableMap);
    void onYoutubeVideoChangeQuality(ReadableMap readableMap);
    void onYoutubeVideoError(ReadableMap readableMap);
}
