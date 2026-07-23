// Top-level build file: declares plugin versions for all modules to share.
// Pinned to a combination that's been proven to actually build end-to-end on
// a memory-constrained CI runner (~7GB RAM, free GitHub-hosted) rather than
// whatever is newest — see README -> Build & CI for the reasoning.

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
