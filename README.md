# Allrounder — Native Android Live Sports Streaming App (Java)

A fully dynamic, single-JSON-driven live sports streaming app built with
AndroidX Media3 ExoPlayer, Retrofit + Gson, and Material 3. Package:
`com.allrounder.app`.

## Project structure

```
Allrounder/
├── .github/workflows/android.yml   # CI: builds a debug APK on every push to main
├── app/
│   ├── build.gradle                 # Media3 ExoPlayer, Gson, Retrofit, Glide deps
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/allrounder/app/
│       │   ├── MainActivity.java          # Toolbar, banner ad, categories, event list, info sheet
│       │   ├── PlayerActivity.java         # Full-screen ExoPlayer, DRM, ads, error UI, servers
│       │   ├── models/
│       │   │   ├── AppData, AppProfile, SocialLinks, Category, Event, Team
│       │   │   ├── StreamingSource, DrmKeys
│       │   │   └── AdsConfig, VideoAdConfig, BannerAdConfig
│       │   ├── network/                    # ApiClient (Retrofit) + ApiService
│       │   ├── adapters/                   # CategoryAdapter, EventAdapter
│       │   └── utils/                      # Constants, DateUtils (countdown math)
│       └── res/
│           ├── layout/          # activity_main, activity_player, bottomsheet_*, item_*
│           ├── drawable/        # capsule/badge/overlay backgrounds, icons
│           ├── mipmap-*/        # ic_launcher.png / ic_launcher_round.png (placeholders — replace these)
│           ├── color/           # capsule_text_selector.xml
│           ├── anim/            # fade_scale_in.xml (error overlay entrance)
│           └── values/          # colors, strings, themes (dark, purple/cyan accents)
├── sample_app_data.json         # Full reference JSON with real, working test streams + ads
├── build.gradle / settings.gradle / gradle.properties
└── .gitignore
```

## How it works

1. **`Constants.CONFIG_JSON_URL`** (in `utils/Constants.java`) points at your hosted
   JSON config — swap it for your real endpoint. `MainActivity` fetches it via
   Retrofit and parses it into `AppData` with Gson.
2. **Top bar**: logo + app name pinned left, an "i" button pinned right opening a
   `BottomSheetDialog` with app info and Facebook/Telegram/YouTube links.
3. **Categories**: horizontal capsule-chip `RecyclerView` filters the vertical event
   list by `category_id`.
4. **Match cards**: VS layout, red `LIVE` badge or blue/gray `COMING SOON` badge.
5. **Tapping a card** opens a **full-screen** `PlayerActivity`:
   - System bars are hidden; swipe from an edge to reveal them.
   - Native Media3 controls overlay the video, auto-hide after 5 seconds while
     playing, and reappear on tap.
   - A custom top bar (back, LIVE/AD badge, title, servers icon) shows/hides in
     sync with the native controls.
   - **Sources**: `type` supports `"hls"`, `"mpd"`, and `"ts"` (or anything else,
     which falls back to `ProgressiveMediaSource` — covers raw MPEG-TS, continuous
     mpegts, mp4, and similar direct streams common on IPTV/MAG-style panels).
   - **DRM**: if `drm_protected` is `true`, a ClearKey `DrmSessionManager` is built
     from `key_id`/`key` via `LocalMediaDrmCallback` (no license server needed).
   - **Server switching**: tap the servers icon for a bottom sheet listing every
     `streaming_sources` entry, with a checkmark on the active one; switching stops
     and re-prepares the player with zero activity restart.
   - **Coming soon**: loops `demo_intro_url` and shows a live countdown computed
     from `start_time`.
   - **Errors**: any playback failure shows a full-screen panel with a plain-language
     message + the raw ExoPlayer error code, and **Retry** / **Change Server**
     buttons. Nothing fails silently.
   - **HTTP and HTTPS** sources both work (`network_security_config.xml` allows
     cleartext — common for self-hosted IPTV/stream panels that don't run TLS).
6. **Ads** — two safe, JSON-controlled formats, both fully optional:
   ```json
   "ads": {
     "video_preroll": {
       "enabled": true,
       "video_url": "https://yourcdn.com/ads/preroll.mp4",
       "skip_after_seconds": 5,
       "click_url": "https://sponsor-site.com"
     },
     "banner": {
       "enabled": true,
       "image_url": "https://yourcdn.com/ads/banner.jpg",
       "click_url": "https://sponsor-site.com"
     }
   }
   ```
   - `video_preroll` plays before any event opens, with a skip button that unlocks
     after `skip_after_seconds` and auto-advances when the ad ends naturally.
   - `banner` shows a dismissible image banner on the home screen, tappable through
     to `click_url`.
   - Set either block's `"enabled"` to `false`, or omit `"ads"` entirely, to turn
     ads off. **Note:** third-party "popunder"/remote-tag ad scripts are intentionally
     **not** supported — that ad pattern is banned by Google Play policy and is a
     common vector for scam redirects and malvertising. Both formats above are
     safe, standard, and fully under your control since you host the creative.

## App icon (PNG, ready for you to replace)

`res/mipmap-*/ic_launcher.png` and `ic_launcher_round.png` are placeholder PNGs at
every required density. Drop your real icon in at these exact sizes to replace them:

| Folder              | Size (px) |
|---------------------|-----------|
| mipmap-mdpi         | 48×48     |
| mipmap-hdpi         | 72×72     |
| mipmap-xhdpi        | 96×96     |
| mipmap-xxhdpi       | 144×144   |
| mipmap-xxxhdpi      | 192×192   |

Keep the filenames (`ic_launcher.png`, `ic_launcher_round.png`) exactly as-is —
no other changes needed, the manifest already points at `@mipmap/ic_launcher`.

## Building

Open the project root in Android Studio (Jellyfish+) and let it sync — no
placeholders, everything compiles as-is against `compileSdk 34` / `minSdk 23`.

### CI (GitHub Actions)

`.github/workflows/android.yml` runs on every push/PR to `main`: sets up JDK 17,
provisions Gradle via `gradle/actions/setup-gradle`, runs `gradle assembleDebug`,
and uploads `app-debug.apk` as a build artifact — no committed Gradle wrapper jar
required.

## Testing it right now

`sample_app_data.json` is filled with **real, publicly-hosted test streams**
(a genuine ClearKey-encrypted DASH asset, a public HLS stream, and a public test
video for the ad pre-roll) so you can confirm the whole app — playback, DRM,
server switching, ads, error UI — works before touching your own server. To use
it: host the file somewhere reachable (e.g. push to a public GitHub repo and use
the `raw.githubusercontent.com` link) and point `Constants.CONFIG_JSON_URL` at it.

## Notes / things to swap for production

- `Constants.CONFIG_JSON_URL` — point at your real JSON endpoint.
- If you see `ERROR_CODE_IO_NETWORK_CONNECTION_FAILED` in the error overlay, it
  means the device could not open a connection to that exact host — check the URL
  resolves in a browser, check the device/emulator has internet, and check your
  server isn't blocking the app's User-Agent or IP.
- App icon: see the table above — just replace the PNGs.
- All in-app text is English by default (this is a worldwide app).
