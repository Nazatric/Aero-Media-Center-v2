package com.aeromedia.app.ui.screens.coverflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aeromedia.app.ui.permissions.MediaKind
import com.aeromedia.app.ui.permissions.MediaPermissionGate
import com.aeromedia.app.ui.theme.AeroColors
import kotlin.math.abs

/**
 * The reference doc's Cover Flow screen: a 3D reflective carousel with a
 * small top-right icon that swaps between browsing by Album and by Track.
 * Built from scratch with Compose's Pager + graphicsLayer (a well-known,
 * widely-used technique for this exact effect, not tied to any one app).
 */
@Composable
fun CoverFlowScreen(viewModel: CoverFlowViewModel) {
    MediaPermissionGate(
        kind = MediaKind.AUDIO,
        rationale = "Cover Flow browses your real albums and tracks, so AeroMedia needs permission to read music from this device.",
    ) {
        viewModel.loadIfNeeded()
        val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .statusBarsPadding(),
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text(
                    text = if (uiState.mode == CoverFlowMode.ALBUMS) "Cover Flow — Albums" else "Cover Flow — Tracks",
                    style = MaterialTheme.typography.titleMedium,
                    color = AeroColors.textPrimary,
                )
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = "Switch between albums and tracks",
                    tint = AeroColors.textPrimary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(50))
                        .clickable { viewModel.toggleMode() }
                        .padding(4.dp),
                )
            }

            if (uiState.cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nothing to show yet", color = AeroColors.textSecondary)
                }
                return@Column
            }

            val pagerState = rememberPagerState(initialPage = 0, pageCount = { uiState.cards.size })

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fixed(160.dp),
                    contentPadding = PaddingValues(horizontal = 96.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) { page ->
                    val card = uiState.cards[page]
                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                    Column(
                        modifier = Modifier
                            .graphicsLayer {
                                cameraDistance = 16f * density
                                rotationY = (pageOffset * -40f).coerceIn(-70f, 70f)
                                val scale = 1f - (abs(pageOffset) * 0.22f).coerceIn(0f, 0.4f)
                                scaleX = scale
                                scaleY = scale
                                translationX = -pageOffset * 46f
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AsyncImage(
                            model = card.artworkUri,
                            contentDescription = card.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        )
                        // Reflection: a full copy of the same artwork, flipped
                        // around its own center and viewed through a clipped
                        // half-height window — shows the actual mirrored
                        // bottom of the art (not a separately re-cropped
                        // fetch), which is what makes it read as a real
                        // reflection instead of a second thumbnail.
                        Box(
                            modifier = Modifier
                                .size(width = 160.dp, height = 96.dp)
                                .clipToBounds(),
                        ) {
                            AsyncImage(
                                model = card.artworkUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp)
                                    .graphicsLayer { scaleY = -1f }
                                    .clip(RoundedCornerShape(4.dp))
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Black),
                                            ),
                                        )
                                    },
                            )
                        }
                    }
                }
            }

            val current = uiState.cards.getOrNull(pagerState.currentPage)
            if (current != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(current.title, color = AeroColors.textPrimary, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(current.subtitle, color = AeroColors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
