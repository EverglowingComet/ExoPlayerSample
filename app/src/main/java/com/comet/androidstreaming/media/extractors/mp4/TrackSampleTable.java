package com.comet.androidstreaming.media.extractors.mp4;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

/**
 * Sample table for a track in an MP4 file.
 */
/* package */ final class TrackSampleTable {

    /**
     * Number of samples.
     */
    public final int sampleCount;
    /**
     * Sample offsets in bytes.
     */
    public final long[] offsets;
    /**
     * Sample sizes in bytes.
     */
    public final int[] sizes;
    /**
     * Maximum sample size in {@link #sizes}.
     */
    public final int maximumSize;
    /**
     * Sample timestamps in microseconds.
     */
    public final long[] timestampsUs;
    /**
     * Sample flags.
     */
    public final int[] flags;
    /**
     * The duration of the track sample table in microseconds, or {@link C#TIME_UNSET} if the sample
     * table is empty.
     */
    public final long durationUs;

    public TrackSampleTable(
            long[] offsets,
            int[] sizes,
            int maximumSize,
            long[] timestampsUs,
            int[] flags,
            long durationUs) {
        Assertions.checkArgument(sizes.length == timestampsUs.length);
        Assertions.checkArgument(offsets.length == timestampsUs.length);
        Assertions.checkArgument(flags.length == timestampsUs.length);

        this.offsets = offsets;
        this.sizes = sizes;
        this.maximumSize = maximumSize;
        this.timestampsUs = timestampsUs;
        this.flags = flags;
        this.durationUs = durationUs;
        sampleCount = offsets.length;
    }

    /**
     * Returns the sample index of the closest synchronization sample at or before the given
     * timestamp, if one is available.
     *
     * @param timeUs Timestamp adjacent to which to find a synchronization sample.
     * @return Index of the synchronization sample, or {@link C#INDEX_UNSET} if none.
     */
    public int getIndexOfEarlierOrEqualSynchronizationSample(long timeUs) {
        // Video frame timestamps may not be sorted, so the behavior of this call can be undefined.
        // Frames are not reordered past synchronization samples so this works in practice.
        int startIndex = Util.binarySearchFloor(timestampsUs, timeUs, true, false);
        for (int i = startIndex; i >= 0; i--) {
            if ((flags[i] & C.BUFFER_FLAG_KEY_FRAME) != 0) {
                return i;
            }
        }
        return C.INDEX_UNSET;
    }

    /**
     * Returns the sample index of the closest synchronization sample at or after the given timestamp,
     * if one is available.
     *
     * @param timeUs Timestamp adjacent to which to find a synchronization sample.
     * @return index Index of the synchronization sample, or {@link C#INDEX_UNSET} if none.
     */
    public int getIndexOfLaterOrEqualSynchronizationSample(long timeUs) {
        int startIndex = Util.binarySearchCeil(timestampsUs, timeUs, true, false);
        for (int i = startIndex; i < timestampsUs.length; i++) {
            if ((flags[i] & C.BUFFER_FLAG_KEY_FRAME) != 0) {
                return i;
            }
        }
        return C.INDEX_UNSET;
    }

}
