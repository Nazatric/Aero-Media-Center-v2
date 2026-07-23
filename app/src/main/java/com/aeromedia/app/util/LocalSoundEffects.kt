package com.aeromedia.app.util

import androidx.compose.runtime.compositionLocalOf

/** Provided once near the root (see MainActivity) and read via
 *  `LocalSoundEffects.current` from any composable that needs to play a tap
 *  sound — avoids threading a SoundEffects instance through every
 *  component's constructor. */
val LocalSoundEffects = compositionLocalOf<SoundEffects> {
    error("LocalSoundEffects not provided — wrap content in CompositionLocalProvider(LocalSoundEffects provides ...)")
}
