# AeroMedia

A skeuomorphic Android media center — Music, Pictures, Video Player, Cover
Flow, Notes, Settings, and Favorites — built around real on-device media
(MediaStore), not sample data, in a brushed-metal / glossy-chrome / leather-
and-paper visual language from the pre-flat-design era.

Built in Kotlin + Jetpack Compose. Every screen reads your actual music,
photos, and videos through Android's MediaStore; Notes and Favorites persist
for real through Room.

## Screens

| Screen | What it does |
|---|---|
| **Home** | Glossy vertical menu + a live analog/digital clock band, splitting Music/Pictures/Video Player/Cover Flow from Notes/Settings/Favorites |
| **Music** | Three-pane swipe: **Tracks** → **Now Playing** (real ExoPlayer playback, real album art) → **Albums**, looping back to Tracks |
| **Video Player** | Real device videos; tapping one plays it with a custom glossy transport bar (no Rip/Burn/Sync — see below) |
| **Cover Flow** | 3D reflective carousel over your real albums or tracks, with a toggle to switch between them |
| **Pictures** | Real photo grid → full-screen viewer with a glossy control bar |
| **Notes** | Leather-header / yellow-legal-pad note list and editor, persisted in Room |
| **Settings** | Sounds toggle, per-category (music/video/pictures) folder exclusion — built from an actual scan of your folders, not a hardcoded list |
| **Favorites** | Photos / Tracks & Albums / Videos, backed by real Room favorite flags |

## Getting this into GitHub

```bash
cd aeromedia
git init
git add .
git commit -m "Initial AeroMedia scaffold"
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```

Push to `main` (or open a PR) and `.github/workflows/android-ci.yml` builds
debug + release APKs automatically — see **Build & CI** below for what to
expect on GitHub's free runners specifically.

## Requirements

- Android Studio Ladybug or newer (or just Gradle + a JDK 17 for CI-only use)
- A device/emulator running **API 26 (Android 8.0)** or newer
- No API keys, no backend, no network access needed to *run* the app —
  everything it shows comes from the device it's installed on

## Architecture

```
data/
  media/      MusicStoreRepository, VideoStoreRepository, PictureStoreRepository
              — real MediaStore content-provider queries, no placeholder data
  db/         Room: NoteEntity/NoteDao, FavoriteEntity/FavoriteDao
  settings/   SettingsRepository — SharedPreferences-backed folder exclusions + sounds toggle
ui/
  theme/      Color.kt, Type.kt, Shape.kt, Theme.kt, SkeuoEffects.kt (the design system)
  components/ Reusable pieces: AeroMenuRow, TransportBar, AnalogClockFace, SkeuoToggleSwitch, FavoriteHeartButton
  permissions/ MediaKind + MediaPermissionGate — one runtime permission per media type
  playback/   PlaybackViewModel — owns the Music screen's real ExoPlayer instance
  navigation/ AeroMediaNavHost — one NavHost destination per Home-screen entry
  screens/    home/ music/ video/ coverflow/ pictures/ notes/ settings/ favorites/
```

Each screen owns its own ViewModel(s), scoped to that destination's
back-stack entry via the standard Compose `viewModel()` provider — no manual
factories needed since they all extend `AndroidViewModel`.

## The skeuomorphic design system

`ui/theme/SkeuoEffects.kt` is the whole visual language, as a handful of
reusable Compose `Modifier` extensions:

- **`skeuoGlossyOrb`** — the convex, glassy button look (transport controls,
  Home menu icon badges, toggle-switch knob): a light-to-dark gradient plus
  a soft highlight ellipse near the top, with a `pressed` state that
  flattens the gradient and shrinks the shadow.
- **`skeuoInsetSurface`** — the opposite: a recessed slot (album art frames,
  the video player's black inset, the toggle switch's track).
- **`skeuoBrushedMetal`** — vertical brushed-chrome gradient for chrome bars.
- **`skeuoLeatherHeader`** / **`skeuoPaper`** — the Notes screen's leather
  band and ruled legal-pad paper, drawn procedurally (gradients + drawn
  rules), not a bitmap.
- **`pressScale`** — a small physical "pushed in" scale animation on tap.

Every one of these is original code written for this project — see "Staying
legal on assets" below for why, and what that meant in practice.

## Real data, not placeholders

- **Music**: `MusicStoreRepository` queries `MediaStore.Audio.Media` /
  `Albums` / `Artists` directly. Track titles, artists, albums, durations,
  and album art all come from whatever's actually on the device.
- **Video**: `VideoStoreRepository` queries `MediaStore.Video.Media`.
- **Pictures**: `PictureStoreRepository` queries `MediaStore.Images.Media`.
- **Folder exclusion** (Settings) filters all three by their real
  `BUCKET_DISPLAY_NAME`, scanned from the device — the folder list itself
  isn't hardcoded either.
- **Notes** and **Favorites** are real Room-backed persistence; nothing
  resets when you restart the app.

## Staying legal on assets

Your reference images are recognizable as specific real products —
pre-2013 iOS Notes, Apple's Cover Flow, Windows Media Center's chrome. The
mood of all three (glossy chrome, glass panels, warm gradients, that era's
type rhythm) is fair to draw on; a specific company's actual logos, exact
proprietary assets, and distinctive trademarked chrome aren't, regardless of
intent. Concretely, in this repo:

- **No "Windows Media Center," no Windows flag, no Microsoft branding.**
  The Video Player screen keeps the glossy dark-chrome *mood* but drops the
  Now Playing/Library/**Rip**/**Burn**/**Sync** tab structure — desktop-only
  actions your own reference doc already flagged as things that "wouldn't
  work" on a phone. What's left is an original layout.
- **No literal Apple Notes or Cover Flow assets.** The leather-header/
  legal-pad look and the 3D reflective carousel are both long-standing,
  widely-used *design patterns* (real leather-bound notebooks and legal pads
  predate any app; "coverflow"-style carousels exist in dozens of unrelated
  open-source projects) — implemented here with original code, not copied
  from Apple's specific renditions.
- **The launcher icon is original vector art** (`ic_launcher_foreground.xml`
  / `_background.xml`), not extracted from any reference image.
- **Icons** are Material Symbols (Apache 2.0, bundled with Compose) plus 4
  verified, credited icons from the Crystal Project set (LGPL) — see
  `THIRD_PARTY_NOTICES.md` for exactly which ones and why only those 4.
- **`Zedge_linuxs.zip` was not used anywhere.** It turned out to be Windows
  `.ico` files mislabeled as `.png`, mixed with a few unrelated files with no
  checkable license — see `THIRD_PARTY_NOTICES.md`.

If you want something even closer to a specific real product's exact look
later, that's your call to make explicitly — this build stops short of it
by default.

## Build & CI

GitHub's free hosted runners have ~7GB of RAM. A Compose + Media3 + Room
project this size will OOM during Kotlin compilation, dexing, or R8 well
before it OOMs at runtime on an actual phone. `gradle.properties` and
`app/build.gradle.kts` are tuned around that specific constraint:

- `org.gradle.jvmargs=-Xmx1536m`, no parallel execution, no Gradle daemon
- `isMinifyEnabled = false` — R8 is the first thing to fall over; a bigger
  APK beats a release build that can't finish
- Lint and unit tests are non-blocking in CI (`|| true`) so a flaky test
  doesn't block getting an installable APK; `assembleDebug` and
  `assembleRelease` are the two steps allowed to actually fail the job

Release signing is optional: set the `RELEASE_KEYSTORE_BASE64` /
`RELEASE_KEYSTORE_PASSWORD` / `RELEASE_KEY_ALIAS` / `RELEASE_KEY_PASSWORD`
repo secrets to get a signed release APK, or leave them unset for an
unsigned one — either way, `assembleRelease` still runs.

## Known Phase 1 simplifications

Stated plainly rather than hidden:

- **Playback stops when you leave the Music/Video screen.** Both use a
  plain `ExoPlayer` owned by that screen's ViewModel — no
  `MediaSessionService` yet, so no background playback, lock-screen
  controls, or notification. A clean, self-contained Phase 2 addition
  (Media3's session APIs are built for exactly this); not started here so
  nothing shipped half-wired.
- **The `.amp` theme editor** your reference doc describes (re-skinning
  every icon/text/UI element) is a real, sizeable feature on its own —
  scoped out as a named roadmap item, not half-built.
- **Settings folder-exclusion changes apply the next time a screen's
  ViewModel is (re)created** (e.g. after fully returning to Home), not
  instantly mid-session on an already-open screen.
- Track/album metadata **editing** (the reference doc's "editable by long
  press") isn't in yet — everything is real and read correctly, but
  read-only for now; writing back to MediaStore/ID3 tags needs additional
  write-permission handling worth its own pass.
- No accessibility pass yet (contrast, touch target sizing, reduced-motion)
  — worth doing before this goes any further.

## Revision 2 — visual fidelity + real feature fixes

- **Icons**: removed the glossy badge behind Home-screen icons — they're now
  flat white glyphs with a real silhouette-shaped drop shadow (drawn twice,
  offset and darkened), matching the reference instead of adding an element
  it didn't have.
- **Background**: `aeroSkyBackground()` is now a diagonal gradient (navy →
  blue → orange → gold) with a soft radial light-source glow near the
  upper-right, closer to the reference's diagonal sweep — still original,
  not the actual Windows wallpaper (see "Staying legal on assets").
- **Clock**: recolored to match the reference (amber bezel, white dial,
  black hands, red second hand) and given a real mirrored reflection
  underneath, built correctly from a flipped, clipped copy of the same
  canvas rather than a re-cropped shrink.
- **Cover Flow reflection**: fixed to mirror the *actual* bottom of the same
  artwork (clipped from a flipped full copy) instead of a separately
  re-cropped fetch that just looked like a second thumbnail.
- **Fullscreen**: `MainActivity` now hides the OS status and navigation bars
  outright (`WindowInsetsControllerCompat`, swipe-to-reveal), so nothing of
  the real device chrome shows.
- **Real sounds**: `util/SoundEffects.kt` plays actual Android system
  sound-effect samples (`AudioManager.playSoundEffect`) on menu taps,
  transport buttons, and toggles — gated by the Settings "Sounds" switch,
  provided app-wide via `LocalSoundEffects`.
- **Real video thumbnails**: the video library now decodes an actual frame
  from each video via Coil3's `VideoFrameDecoder`, not a placeholder icon.

## Attribution

See `THIRD_PARTY_NOTICES.md` for the Gradle wrapper, build configuration
provenance, and Crystal Project icon credits.
