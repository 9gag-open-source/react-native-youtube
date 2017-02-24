package com.inprogress.reactnativeyoutube.event;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * The event to be dispatched when video started
 * Created by raymond on 22/2/2017.
 */
public class VideoStateEvent extends Event<VideoStateEvent> {
    private int currentVideoTime;
    private int startTime, endTime;
    private int videoLength;
    private boolean autoPlay;
    private String type; // playing, paused

    public static final String STATE_PLAYING = "playing";
    public static final String STATE_PAUSED = "paused";
    public static final String STATE_ENDED = "ended";
    public static final String STATE_PROGRESS = "progress";

    public VideoStateEvent(int viewTag,
                           int currentVideoTime,
                           int duration,
                           int startTime, int endTime,
                           boolean autoPlay,
                           String type) {
        super(viewTag);
        this.currentVideoTime = currentVideoTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.videoLength = duration;
        this.autoPlay = autoPlay;
        this.type = type;
    }

    @Override
    public String getEventName() {
        switch (type) {
            case STATE_PLAYING:
                return "videoSourceOnPlay";
            case STATE_PAUSED:
                return "videoSourceOnPause";
            case STATE_ENDED:
                return "videoSourceOnComplete";
            case STATE_PROGRESS:
                return "videoSourceOnProgress";
            default:
                return null;
        }

    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = Arguments.createMap();
        eventData.putInt("currentVideoTime", currentVideoTime);
        eventData.putInt("startTime", startTime);
        eventData.putInt("endTime", endTime);
        eventData.putInt("videoLength", videoLength);
        eventData.putBoolean("autoPlay", autoPlay);
        eventData.putString("type", type);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
