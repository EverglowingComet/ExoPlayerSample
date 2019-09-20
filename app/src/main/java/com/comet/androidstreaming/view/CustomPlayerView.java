package com.comet.androidstreaming.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.comet.androidstreaming.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;


public class CustomPlayerView extends PlayerView {

    public static final int STATUS_NORMAL = 100;
    public static final int STATUS_ERROR = 101;
    public static final int STATUS_LOADING = 102;

    public MediaSource mMediaSourceSet = null;

    private ProgressBar mStreamingInProgress;
    private FrameLayout mMainControl;
    private ImageButton mRefreshButton;

    public CustomPlayerView(Context context) {
        this(context, null);
    }

    public CustomPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStreamingInProgress = findViewById(R.id.streaming_in_progress);
        mMainControl = findViewById(R.id.main_control);
        mRefreshButton = findViewById(R.id.refresh_button);
        if (mStreamingInProgress != null && mMainControl != null) {
            //mStreamingInProgress.setstyle
            mMainControl.setVisibility(View.VISIBLE);
            mStreamingInProgress.setVisibility(View.GONE);
        }
        if (mRefreshButton != null) {
            mRefreshButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMediaSourceSet != null) {
                        ExoPlayer viewPlayer = (ExoPlayer) getPlayer();
                        viewPlayer.stop();
                        viewPlayer.prepare(mMediaSourceSet, true, false);
                    }
                }
            });
        }
    }

    public void showLoading(boolean loading) {

        if (mStreamingInProgress != null && mMainControl != null) {

            mMainControl.setVisibility(loading ? View.GONE : View.VISIBLE);
            mStreamingInProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    public void changeRefresh(int status) {
        switch (status) {
            case STATUS_NORMAL:
                mRefreshButton.setImageResource(R.drawable.refresh_normal);
                break;
            case STATUS_LOADING:
                mRefreshButton.setImageResource(R.drawable.refresh_yellow);
                break;
            case STATUS_ERROR:
                mRefreshButton.setImageResource(R.drawable.refresh_error);
                break;
        }
    }
}
