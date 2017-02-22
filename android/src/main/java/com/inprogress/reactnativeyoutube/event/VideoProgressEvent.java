package com.inprogress.reactnativeyoutube.event;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by raymond on 22/2/2017.
 */

public class VideoProgressEvent extends Event<VideoProgressEvent> {
    private int currentVideoTime;
    private int videoLength;

    public VideoProgressEvent(int viewTag, int currentVideoTime, int videoLength) {
        super(viewTag);
        this.currentVideoTime = currentVideoTime;
        this.videoLength = videoLength;
    }

    @Override
    public String getEventName() {
        return "videoSourceOnProgress";
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = Arguments.createMap();
        eventData.putInt("currentVideoTime", currentVideoTime);
        eventData.putInt("videoLength", videoLength);
        eventData.putDouble("progress", currentVideoTime / videoLength);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
