package com.comet.androidstreaming;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.comet.androidstreaming.media.CustomExtractorMediaSource;
import com.comet.androidstreaming.media.CustomExtractorsFactory;
import com.comet.androidstreaming.http.CustomHttpDataSourceFactory;
import com.comet.androidstreaming.view.CustomPlayerView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.ContentDataSource;

public class MainActivity extends AppCompatActivity {
    private CustomPlayerView playerView;
    private ExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private int mPlayerStatus = CustomPlayerView.STATUS_NORMAL;

    private ImageButton mRefreshButton;
    private MediaSource mMediaSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshButton = findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaSource != null) {
                    player.stop();
                    player.prepare(mMediaSource, true, false);
                }
            }
        });
        playerView = findViewById(R.id.player_view);
        initializePlayer();
        Uri uri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
        preparePlayer(uri);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        playerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                mPlayerStatus = isLoading ? CustomPlayerView.STATUS_LOADING : CustomPlayerView.STATUS_NORMAL;
                changeRefresh(mPlayerStatus);
                playerView.showLoading(isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                playerView.showLoading(playbackState == ExoPlayer.STATE_BUFFERING);
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    mPlayerStatus = CustomPlayerView.STATUS_LOADING;
                } else {
                    mPlayerStatus = CustomPlayerView.STATUS_NORMAL;
                }
                changeRefresh(mPlayerStatus);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                mPlayerStatus = CustomPlayerView.STATUS_ERROR;
                changeRefresh(mPlayerStatus);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        // these are reused for both media sources we create below

        String lastPath = uri.getLastPathSegment();

        CustomHttpDataSourceFactory defaultHttpDataSourceFactory = new CustomHttpDataSourceFactory("ExoPlayer");

        if(lastPath.contains("mp3") || lastPath.contains("mp4")){

            return new CustomExtractorMediaSource(uri, defaultHttpDataSourceFactory, new CustomExtractorsFactory(), null, null);

        }/*else if(lastPath.contains("m3u8")){

            return HlsMediaSource.Factory(defaultHttpDataSourceFactory)
                    .createMediaSource(uri)

        }else{
            return new DashMediaSource(uri, new DefaultDashChunkSource.Factory(defaultHttpDataSourceFactory), null, null)
            val dashChunkSourceFactory = new ExtractorMediaSource(uri, defaultHttpDataSourceFactory, new CustomExtractorsFactory(), null, null);
            DefaultDashChunkSource.Factory(defaultHttpDataSourceFactory)

            return DashMediaSource.Factory(dashChunkSourceFactory, defaultHttpDataSourceFactory)
                    .createMediaSource(uri)

        }*/
        return null;
    }

    private void preparePlayer(Uri uri) {
        MediaSource source = buildMediaSource(uri);
        mMediaSource = source;
        if (source != null) {
            player.prepare(source, true, false);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    public void changeRefresh(int status) {
        switch (status) {
            case CustomPlayerView.STATUS_NORMAL:
                mRefreshButton.setImageResource(R.drawable.refresh_normal);
                break;
            case CustomPlayerView.STATUS_LOADING:
                mRefreshButton.setImageResource(R.drawable.refresh_yellow);
                break;
            case CustomPlayerView.STATUS_ERROR:
                mRefreshButton.setImageResource(R.drawable.refresh_error);
                break;
        }
    }
}
