package com.inprogress.reactnativeyoutube.event;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by raymond on 22/2/2017.
 */

public class VideoErrorEvent extends Event<VideoErrorEvent> {

    public static final String ERR_YOUTUBE_NOT_AVAILABLE = "YOUTUBE_NOT_AVAILABLE";
    private String error;

    public VideoErrorEvent(int viewTag, String error) {
        super(viewTag);
        this.error = error;
    }

    @Override
    public String getEventName() {
        return "videoSourceOnError";
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = Arguments.createMap();
        eventData.putString("error", error);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
