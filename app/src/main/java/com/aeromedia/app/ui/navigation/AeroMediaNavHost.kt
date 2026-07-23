package com.aeromedia.app.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aeromedia.app.ui.components.MiniPlayerBar
import com.aeromedia.app.ui.playback.PlaybackViewModel
import com.aeromedia.app.ui.screens.coverflow.CoverFlowScreen
import com.aeromedia.app.ui.screens.coverflow.CoverFlowViewModel
import com.aeromedia.app.ui.screens.favorites.FavoritesScreen
import com.aeromedia.app.ui.screens.favorites.FavoritesViewModel
import com.aeromedia.app.ui.screens.home.HomeScreen
import com.aeromedia.app.ui.screens.music.MusicPagerScreen
import com.aeromedia.app.ui.screens.music.MusicViewModel
import com.aeromedia.app.ui.screens.notes.NotesScreen
import com.aeromedia.app.ui.screens.notes.NotesViewModel
import com.aeromedia.app.ui.screens.pictures.PicturesScreen
import com.aeromedia.app.ui.screens.pictures.PicturesViewModel
import com.aeromedia.app.ui.screens.settings.SettingsScreen
import com.aeromedia.app.ui.screens.settings.SettingsViewModel
import com.aeromedia.app.ui.screens.video.VideoPlayerScreen
import com.aeromedia.app.ui.screens.video.VideoViewModel

private object Routes {
    const val HOME = "home"
    const val MUSIC = "music"
    const val PICTURES = "pictures"
    const val VIDEO = "video"
    const val COVER_FLOW = "coverflow"
    const val NOTES = "notes"
    const val SETTINGS = "settings"
    const val FAVORITES = "favorites"
}

/**
 * One NavHost, one composable per Home-screen destination — each screen owns
 * its own ViewModel(s) scoped to that destination's back-stack entry, except
 * [PlaybackViewModel]: it's requested here, at the top of the composable
 * tree (Activity-scoped, since this function is called directly from
 * MainActivity's setContent), specifically so it — and the [MiniPlayerBar]
 * driven by it — survive navigating away from Music. That bar is what the
 * reference screenshot shows at the bottom of *every* screen, Home included.
 * It's hidden specifically on the Music screen itself, which already has its
 * own full transport controls one swipe away in its Now Playing pane.
 */
@Composable
fun AeroMediaNavHost(navController: NavHostController = rememberNavController()) {
    val playbackViewModel: PlaybackViewModel = viewModel()
    val currentRoute by navController.currentBackStackEntryAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.weight(1f),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenMusic = { navController.navigate(Routes.MUSIC) },
                    onOpenPictures = { navController.navigate(Routes.PICTURES) },
                    onOpenVideoPlayer = { navController.navigate(Routes.VIDEO) },
                    onOpenCoverFlow = { navController.navigate(Routes.COVER_FLOW) },
                    onOpenNotes = { navController.navigate(Routes.NOTES) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                )
            }
            composable(Routes.MUSIC) {
                val musicViewModel: MusicViewModel = viewModel()
                MusicPagerScreen(musicViewModel = musicViewModel, playbackViewModel = playbackViewModel)
            }
            composable(Routes.PICTURES) {
                PicturesScreen(viewModel = viewModel<PicturesViewModel>())
            }
            composable(Routes.VIDEO) {
                VideoPlayerScreen(viewModel = viewModel<VideoViewModel>())
            }
            composable(Routes.COVER_FLOW) {
                CoverFlowScreen(viewModel = viewModel<CoverFlowViewModel>())
            }
            composable(Routes.NOTES) {
                NotesScreen(viewModel = viewModel<NotesViewModel>())
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(viewModel = viewModel<SettingsViewModel>())
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen(viewModel = viewModel<FavoritesViewModel>())
            }
        }

        if (currentRoute?.destination?.route != Routes.MUSIC) {
            MiniPlayerBar(
                playbackViewModel = playbackViewModel,
                onExpand = { navController.navigate(Routes.MUSIC) },
            )
        }
    }
}
