package com.allrounder.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Simple dismissible image banner ad shown on the home screen, below the
 * toolbar. Tapping it opens click_url; tapping the close (x) dismisses it
 * for the rest of the session.
 */
public class BannerAdConfig {

    public boolean enabled;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("click_url")
    public String clickUrl;
}
