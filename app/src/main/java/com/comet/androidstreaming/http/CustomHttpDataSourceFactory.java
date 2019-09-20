package com.comet.androidstreaming.http;

import android.support.annotation.Nullable;

import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;

public class CustomHttpDataSourceFactory extends BaseFactory {

    private final String userAgent;
    private final TransferListener listener;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final boolean allowCrossProtocolRedirects;

    public CustomHttpDataSourceFactory(String userAgent) {
        this(userAgent, null);
    }

    public CustomHttpDataSourceFactory(String userAgent, TransferListener listener) {
        this(userAgent, listener, CustomHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                CustomHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, false);
    }

    public CustomHttpDataSourceFactory(
            String userAgent,
            int connectTimeoutMillis,
            int readTimeoutMillis,
            boolean allowCrossProtocolRedirects) {
        this(
                userAgent,
                /* listener= */ null,
                connectTimeoutMillis,
                readTimeoutMillis,
                allowCrossProtocolRedirects);
    }

    public CustomHttpDataSourceFactory(
            String userAgent,
            TransferListener listener,
            int connectTimeoutMillis,
            int readTimeoutMillis,
            boolean allowCrossProtocolRedirects) {
        this.userAgent = Assertions.checkNotEmpty(userAgent);
        this.listener = listener;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
    }

    @Override
    protected CustomHttpDataSource createDataSourceInternal(
            @Nullable HttpDataSource.RequestProperties defaultRequestProperties) {
        CustomHttpDataSource dataSource = new CustomHttpDataSource(
                        userAgent,
                        null,
                        listener,
                        connectTimeoutMillis,
                        readTimeoutMillis,
                        allowCrossProtocolRedirects,
                        defaultRequestProperties);
        return dataSource;
    }
}

