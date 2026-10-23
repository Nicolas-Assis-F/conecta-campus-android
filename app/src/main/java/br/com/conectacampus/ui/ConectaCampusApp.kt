package br.com.conectacampus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.conectacampus.model.HelpPost
import br.com.conectacampus.ui.components.PostFormDialog
import br.com.conectacampus.ui.screens.HomeScreen
import br.com.conectacampus.ui.screens.LoginScreen
import br.com.conectacampus.ui.screens.MyPostsScreen
import br.com.conectacampus.ui.screens.ProfileScreen

private data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun ConectaCampusApp(appViewModel: AppViewModel = viewModel()) {
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error, state.message) {
        val notice = state.error ?: state.message
        if (notice != null) {
            appViewModel.clearNotice()
            snackbar.showSnackbar(notice)
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (state.currentUser == null) {
            LoginScreen(
                isLoading = state.isLoading,
                firebaseConfigured = state.firebaseConfigured,
                onLogin = appViewModel::login,
                onRegister = appViewModel::register,
                onDemo = appViewModel::enterDemo,
            )
        } else {
            MainArea(
                state = state,
                onSavePost = appViewModel::savePost,
                onDeletePost = appViewModel::deletePost,
                onToggleResolved = appViewModel::toggleResolved,
                onToggleFavorite = appViewModel::toggleFavorite,
                onLogout = appViewModel::logout,
            )
        }
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun MainArea(
    state: AppUiState,
    onSavePost: (br.com.conectacampus.model.PostDraft, String?) -> Boolean,
    onDeletePost: (HelpPost) -> Unit,
    onToggleResolved: (HelpPost) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val currentUser = requireNotNull(state.currentUser)
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"
    val items = listOf(
        NavItem("home", "Mural", Icons.Default.Home),
        NavItem("mine", "Meus posts", Icons.AutoMirrored.Filled.ViewList),
        NavItem("profile", "Perfil", Icons.Default.Person),
    )
    var showForm by remember { mutableStateOf(false) }
    var editingPost by remember { mutableStateOf<HelpPost?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == "home" || currentRoute == "mine") {
                ExtendedFloatingActionButton(
                    onClick = {
                        editingPost = null
                        showForm = true
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Publicar") },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
        ) {
            composable("home") {
                HomeScreen(
                    posts = state.posts,
                    favoriteIds = state.favoriteIds,
                    userName = currentUser.name,
                    onToggleFavorite = onToggleFavorite,
                )
            }
            composable("mine") {
                MyPostsScreen(
                    posts = state.posts.filter { it.authorId == currentUser.uid },
                    onEdit = {
                        editingPost = it
                        showForm = true
                    },
                    onDelete = onDeletePost,
                    onToggleResolved = onToggleResolved,
                )
            }
            composable("profile") {
                ProfileScreen(
                    user = currentUser,
                    postCount = state.posts.count { it.authorId == currentUser.uid },
                    favoriteCount = state.favoriteIds.size,
                    firebaseConfigured = state.firebaseConfigured,
                    onLogout = onLogout,
                )
            }
        }
    }

    if (showForm) {
        PostFormDialog(
            post = editingPost,
            onDismiss = { showForm = false },
            onSave = { draft ->
                if (onSavePost(draft, editingPost?.id)) showForm = false
            },
        )
    }
}

