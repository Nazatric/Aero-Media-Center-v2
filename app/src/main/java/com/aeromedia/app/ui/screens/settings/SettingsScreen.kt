package com.aeromedia.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aeromedia.app.ui.components.SkeuoToggleSwitch
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.aeroSkyBackground

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    viewModel.loadIfNeeded()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .aeroSkyBackground()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            modifier = Modifier.padding(20.dp),
        )

        SettingsRow(title = "Sounds", subtitle = "UI tap and navigation sounds") {
            SkeuoToggleSwitch(checked = uiState.soundsEnabled, onCheckedChange = viewModel::setSoundsEnabled)
        }

        SectionHeader("Exclude music folders")
        FolderList(folders = uiState.musicFolders, excluded = uiState.excludedMusicFolders, onToggle = viewModel::toggleMusicFolder)

        SectionHeader("Exclude video folders")
        FolderList(folders = uiState.videoFolders, excluded = uiState.excludedVideoFolders, onToggle = viewModel::toggleVideoFolder)

        SectionHeader("Exclude picture folders")
        FolderList(folders = uiState.pictureFolders, excluded = uiState.excludedPictureFolders, onToggle = viewModel::togglePictureFolder)

        SectionHeader("Theme")
        SettingsRow(title = "Theme editor", subtitle = "Re-skin every icon, text label, and UI element via a .amp file — planned, not in this build yet") {}
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = AeroColors.textSecondary,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 6.dp),
    )
}

@Composable
private fun SettingsRow(title: String, subtitle: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = AeroColors.textPrimary)
            Text(subtitle, color = AeroColors.textSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        trailing()
    }
    HorizontalDivider(color = AeroColors.divider, thickness = 0.5.dp)
}

@Composable
private fun FolderList(folders: List<String>, excluded: Set<String>, onToggle: (String) -> Unit) {
    if (folders.isEmpty()) {
        Text(
            "No folders found yet",
            color = AeroColors.textSecondary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        )
        return
    }
    Column {
        folders.forEach { folder ->
            val isExcluded = folder in excluded
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggle(folder) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (isExcluded) Icons.Filled.RadioButtonUnchecked else Icons.Filled.CheckCircle,
                    contentDescription = if (isExcluded) "Excluded" else "Included",
                    tint = if (isExcluded) AeroColors.textSecondary else AeroColors.accentGreen,
                )
                Text(
                    text = folder,
                    color = AeroColors.textPrimary,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
    }
}
