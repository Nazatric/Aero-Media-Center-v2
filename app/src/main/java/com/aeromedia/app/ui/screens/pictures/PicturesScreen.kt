package com.aeromedia.app.ui.screens.pictures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aeromedia.app.ui.permissions.MediaKind
import com.aeromedia.app.ui.permissions.MediaPermissionGate
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.skeuoBrushedMetal

@Composable
fun PicturesScreen(viewModel: PicturesViewModel) {
    MediaPermissionGate(
        kind = MediaKind.IMAGES,
        rationale = "To show your real photos, AeroMedia needs permission to read images from this device.",
    ) {
        viewModel.loadIfNeeded()
        val uiState by viewModel.uiState.collectAsState()

        if (uiState.viewerIndex != null) {
            PhotoViewer(viewModel = viewModel, index = uiState.viewerIndex!!, total = uiState.photos.size, uri = uiState.photos[uiState.viewerIndex!!].contentUri, name = uiState.photos[uiState.viewerIndex!!].displayName)
        } else {
            PhotoGrid(uiState = uiState, onPhotoClick = viewModel::openViewer)
        }
    }
}

@Composable
private fun PhotoGrid(uiState: PicturesUiState, onPhotoClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Text(
            text = "Pictures",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            modifier = Modifier.padding(20.dp),
        )
        if (uiState.photos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pictures found on this device", color = AeroColors.textSecondary)
            }
            return
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(4.dp),
        ) {
            items(uiState.photos, key = { it.id }) { photo ->
                val index = uiState.photos.indexOf(photo)
                AsyncImage(
                    model = photo.contentUri,
                    contentDescription = photo.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onPhotoClick(index) },
                )
            }
        }
    }
}

/** Full-screen photo viewer with a glossy, dark-chrome control bar — the
 *  reference doc explicitly asks for this to be original work "inspired by
 *  Windows Media Center" rather than modeled on any single reference image. */
@Composable
private fun PhotoViewer(
    viewModel: PicturesViewModel,
    index: Int,
    total: Int,
    uri: android.net.Uri,
    name: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            AsyncImage(
                model = uri,
                contentDescription = name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .skeuoBrushedMetal()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = AeroColors.surfaceCharcoal,
                modifier = Modifier.clickable { viewModel.closeViewer() },
            )
            Text(
                text = name,
                color = AeroColors.surfaceCharcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            )
            Text(
                text = "${index + 1} / $total",
                color = AeroColors.surfaceCharcoal,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(end = 12.dp),
            )
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Previous",
                tint = AeroColors.surfaceCharcoal,
                modifier = Modifier.clickable { viewModel.previousPhoto() },
            )
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "Next",
                tint = AeroColors.surfaceCharcoal,
                modifier = Modifier.padding(start = 16.dp).clickable { viewModel.nextPhoto() },
            )
        }
    }
}
