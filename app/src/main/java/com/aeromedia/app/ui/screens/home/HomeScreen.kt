package com.aeromedia.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeromedia.app.R
import com.aeromedia.app.ui.components.AeroMenuBitmapIcon
import com.aeromedia.app.ui.components.AeroMenuRow
import com.aeromedia.app.ui.components.AnalogClockFace
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.aeroSkyBackground
import com.aeromedia.app.util.AeroClock
import kotlinx.coroutines.delay
import java.util.Calendar

/**
 * The AeroMedia Home / Start screen: a vertical flat-icon menu over a
 * dusk-sky gradient, with a clock band splitting the top group (Music /
 * Pictures / Video Player / Cover Flow) from the bottom group (Notes /
 * Settings / Favorites) — matching the reference screenshot's layout. Runs
 * fullscreen/edge-to-edge (see MainActivity) so there's no OS status bar cutting
 * into it. There's no separate "Video Library" row: per the reference doc,
 * that content lives inside Video Player instead.
 */
@Composable
fun HomeScreen(
    onOpenMusic: () -> Unit,
    onOpenPictures: () -> Unit,
    onOpenVideoPlayer: () -> Unit,
    onOpenCoverFlow: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFavorites: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .aeroSkyBackground(),
    ) {
        BrandHeader()

        AeroMenuRow(label = "Music", onClick = onOpenMusic) {
            AeroMenuBitmapIcon(painterResource(R.drawable.crystal_music_notes))
        }
        AeroMenuRow(icon = Icons.Filled.Photo, label = "Pictures", onClick = onOpenPictures)
        AeroMenuRow(icon = Icons.Filled.Movie, label = "Video Player", onClick = onOpenVideoPlayer)
        AeroMenuRow(icon = Icons.Filled.ViewCarousel, label = "Cover Flow", onClick = onOpenCoverFlow)

        ClockBand(modifier = Modifier.padding(vertical = 20.dp))

        AeroMenuRow(label = "Notes", onClick = onOpenNotes) {
            AeroMenuBitmapIcon(painterResource(R.drawable.crystal_note))
        }
        AeroMenuRow(label = "Settings", onClick = onOpenSettings) {
            AeroMenuBitmapIcon(painterResource(R.drawable.crystal_settings_gear))
        }
        AeroMenuRow(icon = Icons.Filled.Star, label = "Favorites", onClick = onOpenFavorites)
    }
}

/** Slim branded header replacing the reference's top status area — the logo
 *  and app name are honest app branding; the reference's signal/wifi/battery
 *  icons are the *real device's* status, which this app already reports
 *  accurately via the real system status bar when it's not hidden, so they
 *  aren't reproduced here as static fakes that could show wrong info. */
@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(26.dp),
        )
        Text(
            text = "AeroMedia",
            color = AeroColors.textPrimary,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

/** The wide strip showing the live time, date, "phone off" status text, and
 *  a reflective analog clock — matches the reference's clock-band proportions. */
@Composable
private fun ClockBand(modifier: Modifier = Modifier) {
    val now by produceState(initialValue = Calendar.getInstance().time) {
        while (true) {
            value = Calendar.getInstance().time
            delay(1000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .height(88.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = AeroClock.formatTimeOnly(now),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Light,
                    color = AeroColors.textPrimary,
                )
                Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)) {
                    Text(
                        text = AeroClock.formatAmPm(now),
                        style = MaterialTheme.typography.labelSmall,
                        color = AeroColors.textSecondary,
                    )
                    Text(
                        text = AeroClock.formatShortDate(now),
                        style = MaterialTheme.typography.labelSmall,
                        color = AeroColors.textSecondary,
                    )
                }
            }
            Text(
                text = "PHONE OFF",
                style = MaterialTheme.typography.labelSmall,
                color = AeroColors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        AnalogClockFace(sizeDp = 78, showReflection = true)
    }
}
