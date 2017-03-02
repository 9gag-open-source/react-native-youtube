package com.inprogress.reactnativeyoutube.event;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by raymond on 22/2/2017.
 */

public class VideoSeekEvent extends Event<VideoSeekEvent> {
    private int seekFrom, seekTo;

    public VideoSeekEvent(int viewTag, int seekFrom, int seekTo) {
        super(viewTag);
        this.seekFrom = seekFrom;
        this.seekTo = seekTo;
    }

    @Override
    public String getEventName() {
        return "videoSourceOnSeek";
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = Arguments.createMap();
        eventData.putInt("seekFrom", seekFrom);
        eventData.putInt("seekTo", seekTo);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
