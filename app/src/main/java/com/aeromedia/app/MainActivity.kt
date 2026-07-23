package com.aeromedia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aeromedia.app.ui.navigation.AeroMediaNavHost
import com.aeromedia.app.ui.theme.AeroMediaTheme
import com.aeromedia.app.util.LocalSoundEffects
import com.aeromedia.app.util.SoundEffects

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        setContent {
            val context = LocalContext.current
            val soundEffects = remember { SoundEffects(context) }
            CompositionLocalProvider(LocalSoundEffects provides soundEffects) {
                AeroMediaTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AeroMediaNavHost()
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Re-apply after the user pulls a system bar back with an edge swipe —
        // otherwise it stays revealed until the Activity is recreated.
        if (hasFocus) hideSystemBars()
    }

    /** True fullscreen: no OS status bar (clock/network/battery) or
     *  navigation bar, matching the reference's custom in-app chrome instead
     *  of the device's own. Swipe from an edge to reveal them briefly. */
    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
