package com.inprogress.reactnativeyoutube;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.Timer;
import java.util.TimerTask;


public class YouTubePlayerController implements
        YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.OnFullscreenListener {

    private static final String TAG = "YouTubePlayerController";

    String videoId = null;

    YouTubePlayer mYouTubePlayer;
    YouTubeView mYouTubeView;

    private boolean isLoaded = false;
    private boolean play = false;
    private boolean hidden = false;
    private boolean related = false;
    private boolean modestBranding = false;
    private int controls = 1;
    private boolean showInfo = true;
    private boolean loop = false;
    private boolean playInline = false;
    private boolean fullscreen = true;
    private int startTime = 0;

    private int mLastCheckTime;
    private Timer mCheckProgressTimerTask;

    public YouTubePlayerController(YouTubeView youTubeView) {
        this.mYouTubeView = youTubeView;
        mCheckProgressTimerTask = new Timer("youtube-checkprogress");
        mCheckProgressTimerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mYouTubeView == null ||
                            mYouTubePlayer == null ||
                            !mYouTubePlayer.isPlaying()) return;
                    mLastCheckTime = mYouTubePlayer.getCurrentTimeMillis();
                    mYouTubeView.didPlayTime(mLastCheckTime, mYouTubePlayer.getDurationMillis());
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                }
            }
        }, 0, 500);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        mYouTubePlayer = youTubePlayer;
        // Intall listeners on the youtube player
        mYouTubePlayer.setPlayerStateChangeListener(this);
        mYouTubePlayer.setPlaybackEventListener(this);
        mYouTubePlayer.setOnFullscreenListener(this);

        // Update config
        mYouTubePlayer.setShowFullscreenButton(fullscreen);

        // Emit 'onReady' event for player
        mYouTubeView.playerViewDidBecomeReady(youTubePlayer);
        setLoaded(true);

        if (!wasRestored) {
            // Load/start the video in case it was initially provided
            if (videoId != null) {
                if (isPlay()) {
                    if (startTime != 0) {
                        mYouTubePlayer.loadVideo(videoId, startTime * 1000); // Start time is in second, but require millisecond
                    } else {
                        mYouTubePlayer.loadVideo(videoId);
                    }

                    if (!isPlayInline()) {
                        mYouTubePlayer.setFullscreen(true);
                    }
                }
                else {
                    if (startTime != 0) {
                        mYouTubePlayer.cueVideo(videoId, startTime * 1000); // Start time is in second, but require millisecond
                    } else {
                        mYouTubePlayer.cueVideo(videoId);
                    }
                }
            }
            updateControls();
        } else {
            mYouTubePlayer.loadVideo(videoId, startTime * 1000);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        mYouTubeView.receivedError(youTubeInitializationResult.toString());
    }


    @Override
    public void onPlaying() {
        mYouTubeView.didChangeToState("playing", mYouTubePlayer);

        // When inline playback is not allowed, transition the
        // player to full-screen.
        if (!isPlayInline()) {
            mYouTubePlayer.setFullscreen(true);
        }
    }

    @Override
    public void onPaused() {
        mYouTubeView.didChangeToState("paused", mYouTubePlayer);
    }

    @Override
    public void onStopped() {
        mYouTubeView.didChangeToState("stopped", mYouTubePlayer);
    }


    @Override
    public void onBuffering(boolean b) {
        if (b)
            mYouTubeView.didChangeToState("buffering", mYouTubePlayer);

        //Trick to remove when YouTube will patch it
        ProgressBar progressBar;
        try {
            // As of 2016-02-16, the ProgressBar is at position 0 -> 3 -> 2 in the view tree of the Youtube Player Fragment
            ViewGroup child1 = (ViewGroup)mYouTubeView.getChildAt(0);
            ViewGroup child2 = (ViewGroup)child1.getChildAt(3);
            progressBar = (ProgressBar)child2.getChildAt(2);
        } catch (Throwable t) {
            // As its position may change, we fallback to looking for it
            progressBar = findProgressBar(mYouTubeView);
        }

        int visibility = b ? View.VISIBLE : View.INVISIBLE;
        if (progressBar != null) {
            progressBar.setVisibility(visibility);
        }
    }

    private ProgressBar findProgressBar(View view) {
        if (view instanceof ProgressBar) {
            return (ProgressBar)view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                ProgressBar res = findProgressBar(viewGroup.getChildAt(i));
                if (res != null) return res;
            }
        }
        return null;
    }

    @Override
    public void onSeekTo(int time) {
        Log.d(TAG, "onSeekTo: " + time + ", lastCheck=" + mLastCheckTime);
        mYouTubeView.didOnSeekTo(mLastCheckTime, time);
    }

    @Override
    public void onLoading() {
        mYouTubeView.didChangeToState("loading", mYouTubePlayer);
    }

    @Override
    public void onLoaded(String s) {
        mYouTubeView.didChangeToState("loaded", mYouTubePlayer);
    }

    @Override
    public void onAdStarted() {
        mYouTubeView.didChangeToState("adStarted", mYouTubePlayer);
    }

    @Override
    public void onVideoStarted() {
        mYouTubeView.didChangeToState("videoStarted", mYouTubePlayer);
    }

    @Override
    public void onVideoEnded() {
        mYouTubeView.didChangeToState("ended", mYouTubePlayer);
        if (isLoop()) {
            mYouTubePlayer.loadVideo(videoId);
            mYouTubePlayer.play();
        }
        else {
            mYouTubePlayer.setFullscreen(false);
        }
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        mYouTubeView.receivedError(errorReason.toString());
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        mYouTubeView.didChangeToState(isFullscreen ? "fullscreenMode" : "windowMode", mYouTubePlayer);

        // When exiting full-screen mode and inline playback is not enabled
        // then pause the video playback.
        if (!isPlayInline() && !isFullscreen) {
            mYouTubePlayer.pause();
        }
    }

    public void seekTo(int second) {
        if (isLoaded()) {
            mYouTubePlayer.seekToMillis(second * 1000);
        }
    }

    public void updateControls() {
        switch (controls) {
            case 0:
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                break;
            case 1:
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                break;
            case 2:
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                break;
        }
    }

    /**
     * GETTER &SETTER
     **/

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * PROPS
     */

    public void setVideoId(String str) {
        videoId = str;
        if (isLoaded()) {
            if (videoId == null) {
                mYouTubePlayer.pause();
            }
            else if (isPlay()) {
                mYouTubePlayer.loadVideo(videoId);
                mYouTubePlayer.play();
            }
            else {
                mYouTubePlayer.cueVideo(videoId);
            }
        }
    }

    public void setPlay(boolean play) {
        this.play = play;

        if (isLoaded()) {
            if (this.play && !mYouTubePlayer.isPlaying()) {
                mYouTubePlayer.play();
                if (!isPlayInline()) {
                    mYouTubePlayer.setFullscreen(true);
                }
            }
            else if (!this.play && mYouTubePlayer.isPlaying()){
                mYouTubePlayer.pause();
                mYouTubePlayer.setFullscreen(false);
            }
        }
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setControls(Integer controls) {
        if (controls >= 0 && controls <= 2) {
            this.controls = Integer.valueOf(controls);
            if (isLoaded())
                updateControls();
        }
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (isLoaded()) {
            mYouTubePlayer.setShowFullscreenButton(fullscreen);
        }
    }

    //TODO
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    //TODO
    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    //TODO
    public void setRelated(boolean related) {
        this.related = related;
    }

    //TODO
    public void setModestBranding(boolean modestBranding) {
        this.modestBranding = modestBranding;
    }

    //TODO
    public void setPlayInline(boolean playInline) {
        this.playInline = playInline;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public boolean isPlay() {
        return play;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isRelated() {
        return related;
    }

    public boolean isModestBranding() {
        return modestBranding;
    }

    public int getControls() {
        return controls;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isPlayInline() {
        return playInline;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getCurrentTime() {
        if (mYouTubePlayer == null) return 0;
        return mYouTubePlayer.getCurrentTimeMillis() / 1000;
    }

    public void destroy() {
        mCheckProgressTimerTask.purge();
        mCheckProgressTimerTask.cancel();
        mCheckProgressTimerTask = null;
    }
}
