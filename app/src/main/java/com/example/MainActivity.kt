package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BasokaViewModel
import com.example.ui.components.BasokaHeader
import com.example.ui.components.ConfirmationDialog
import com.example.ui.screens.CapabilitiesScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.FilesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.BasokaBlack
import com.example.ui.theme.BasokaPrimary
import com.example.ui.theme.BasokaSurface
import com.example.ui.theme.BasokaSurfaceBorder
import com.example.ui.theme.BasokaSurfaceElevated
import com.example.ui.theme.BasokaTextPrimary
import com.example.ui.theme.BasokaTextSecondary
import com.example.ui.theme.BasokaTheme

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {

    private val viewModel: BasokaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BasokaTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: BasokaViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentAgent by viewModel.currentAgent.collectAsState()
    val confirmationState by viewModel.confirmationState.collectAsState()

    val tabs = listOf(
        NavTabItem("چات", Icons.Default.Chat, "nav_tab_chat"),
        NavTabItem("تواناکان", Icons.Default.AutoAwesome, "nav_tab_capabilities"),
        NavTabItem("کارەکان", Icons.Default.CheckCircle, "nav_tab_tasks"),
        NavTabItem("فایلەکان", Icons.Default.Folder, "nav_tab_files"),
        NavTabItem("ڕێکخستن", Icons.Default.Settings, "nav_tab_settings")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BasokaBlack),
        topBar = {
            BasokaHeader(
                activeAgent = currentAgent,
                onClearChat = { viewModel.clearAllChat() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BasokaSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(index) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BasokaPrimary,
                            indicatorColor = BasokaPrimary,
                            unselectedIconColor = BasokaTextSecondary,
                            unselectedTextColor = BasokaTextSecondary
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> ChatScreen(
                    viewModel = viewModel,
                    onNavigateToTasks = { viewModel.setTab(2) }
                )
                1 -> CapabilitiesScreen(
                    viewModel = viewModel,
                    onSelectPrompt = { prompt ->
                        viewModel.onPromptSuggestionClicked(prompt)
                        viewModel.setTab(0)
                    }
                )
                2 -> TasksScreen(viewModel = viewModel)
                3 -> FilesScreen(
                    viewModel = viewModel,
                    onSendPromptToChat = { prompt ->
                        viewModel.onPromptSuggestionClicked(prompt)
                        viewModel.setTab(0)
                    }
                )
                4 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Sensitive Action Confirmation Dialog
    ConfirmationDialog(
        isVisible = confirmationState.isVisible,
        prompt = confirmationState.prompt,
        onConfirm = confirmationState.onConfirm,
        onDismiss = confirmationState.onDismiss
    )
}
