package com.allrounder.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Video pre-roll ad config. When enabled, PlayerActivity plays this as a
 * skippable pre-roll before any live/coming-soon content.
 */
public class VideoAdConfig {

    public boolean enabled;

    @SerializedName("video_url")
    public String videoUrl;

    /** Seconds before the skip button becomes tappable. */
    @SerializedName("skip_after_seconds")
    public int skipAfterSeconds = 5;

    /** Optional sponsor link opened if the user taps "Visit Sponsor". */
    @SerializedName("click_url")
    public String clickUrl;
}
