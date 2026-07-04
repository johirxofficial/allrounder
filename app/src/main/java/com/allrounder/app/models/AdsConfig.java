package com.allrounder.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Container for all ad formats driven by the JSON config. Both fields are
 * optional and independently toggled with their own "enabled" flag.
 */
public class AdsConfig {

    @SerializedName("video_preroll")
    public VideoAdConfig videoPreroll;

    @SerializedName("banner")
    public BannerAdConfig banner;
}
