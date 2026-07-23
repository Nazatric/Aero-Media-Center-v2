# Third-party notices

This file exists so nobody — including future-you — has to guess what came
from where. It covers every asset and piece of tooling in this repo that
Claude didn't write from scratch for this project.

## Gradle wrapper

`gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` are the
unmodified, official Gradle bootstrap distributed by Gradle Inc. under the
Apache License 2.0 (https://www.apache.org/licenses/LICENSE-2.0). They're
identical in every Gradle project on earth — reusing them isn't reusing
anyone's creative work, it's just the standard way a Gradle project boots.
Sourced from the validated `AeroCenter-fixed` reference project, per your
instruction to reuse its Gradle/build setup.

## Build configuration

The dependency versions and CI workflow structure in `app/build.gradle.kts`,
`build.gradle.kts`, `gradle.properties`, and
`.github/workflows/android-ci.yml` mirror the `AeroCenter-fixed` reference
project — Kotlin 2.0.21 / AGP 8.7.3 / Compose BOM 2024.12.01 / Media3 1.4.1 /
Room 2.6.1 / Coil3 3.0.4 / Navigation Compose 2.8.5, `-Xmx1536m` and no
parallel/daemon Gradle execution. These are exact version *numbers*, chosen
in that project through real trial and error against GitHub's free hosted
runners (R8 and even plain dexing will OOM on this dependency graph above
~2GB heap). Reusing known-working version numbers and CI shape is exactly
the "how Android apps are built" knowledge you asked to carry over — no UI
code, Composables, or icon assets were copied from that project.

## Crystal Project icon set

`app/src/main/res/drawable-nodpi/crystal_*.png` (4 files: settings gear,
note, music notes, clock) are unmodified icons from the **Crystal Project**
icon set:

```
TITLE:   Crystal Project Icons
AUTHOR:  Everaldo Coelho
SITE:    http://www.everaldo.com
LICENSE: LGPL
```

These came from the `crystal-icons-full.zip` you provided (a 200-icon subset
of the full ~1,400-icon Crystal Project set, widely mirrored — e.g.
github.com/niko-yanev/crystal-project — under the same LGPL terms). Only 4
were bundled here, verified by eye and given descriptive names, rather than
copying all 200 unlabeled files in. The rest of the pack is still sitting in
your upload if you want to hand-pick more later; the numbered filenames
(`crystal_001.png`, etc.) don't indicate content, so each one needs a quick
look before it's used for something specific.

One icon in the source pack, a Windows-flag glyph, was deliberately **not**
included anywhere in this project — Crystal's own license covers Everaldo's
artwork, not Microsoft's logo that one icon happens to depict.

**Not used:** `Zedge_linuxs.zip`. Despite the `.png` extension, these are
Windows `.ico` icon-resource files (multi-resolution, 16-color, an
XP/2000-era icon pack), plus a handful of unrelated files (a stock photo, an
anime-character wallpaper) mixed into the same archive. Nothing in it has
clear, checkable licensing for reuse in a public repo, so none of it is
referenced here.

## Everything else

All Kotlin/Compose source (screens, ViewModels, the `SkeuoEffects.kt` design
system, MediaStore/Room data layer, the launcher icon's vector art) was
written from scratch for this project. The visual language is *inspired by*
the general mood of pre-2013 skeuomorphic UI and the images in
`AeroMedia_Reference_Document.pdf` — brushed metal, glossy chrome, leather,
legal-pad paper — without reproducing any specific company's actual assets,
wordmarks, or trademarked chrome (see README → "Staying legal on assets").
