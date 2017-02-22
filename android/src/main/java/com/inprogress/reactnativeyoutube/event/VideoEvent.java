package com.inprogress.reactnativeyoutube.event;

import android.support.annotation.StringDef;

import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by raymond on 22/2/2017.
 */

public class VideoEvent extends Event<VideoEvent> {
    @StringDef(value = {
            EVENT_VIDEO_ON_PLAY, EVENT_VIDEO_ON_PAUSE,
            EVENT_VIDEO_ON_PROGRESS, EVENT_VIDEO_ON_SEEK,
            EVENT_VIDEO_ON_COMPLETE, EVENT_VIDEO_ON_ERROR
    })
    public @interface VideoEventType{}

    public static final String EVENT_VIDEO_ON_PLAY = "videoSourceOnPlay";
    public static final String EVENT_VIDEO_ON_PAUSE = "videoSourceOnPause";
    public static final String EVENT_VIDEO_ON_PROGRESS = "videoSourceOnProgress";
    public static final String EVENT_VIDEO_ON_SEEK = "videoSourceOnSeek";
    public static final String EVENT_VIDEO_ON_COMPLETE = "videoSourceOnComplete";
    public static final String EVENT_VIDEO_ON_ERROR = "videoSourceOnError";

    private @VideoEventType String eventName;

    private VideoEvent(int viewTag, String eventName, Builder builder) {
        super(viewTag);
        this.eventName = eventName;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {

    }

    public static class Builder {
        int startTime, endTime;
        int currentVideoTime, duration;
        boolean autoPlay;


        public VideoEvent build() {
            return null;
        }
    }
}

