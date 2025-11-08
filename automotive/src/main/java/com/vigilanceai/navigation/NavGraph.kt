// Updated NavGraph.kt
// Replace your existing NavGraph.kt with this version

package com.vigilanceai.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vigilanceai.ui.components.BottomBar
import com.vigilanceai.ui.components.TopBar
import com.vigilanceai.ui.screens.*
import com.vigilanceai.viewmodel.VigilanceViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: VigilanceViewModel
) {
    var currentScreen by remember { mutableStateOf("Dashboard") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
    ) {
        // Top Bar
        TopBar()

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationButton(
                text = "Dashboard",
                isSelected = currentScreen == "Dashboard",
                onClick = {
                    currentScreen = "Dashboard"
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )

            NavigationButton(
                text = "Wellness",
                isSelected = currentScreen == "Wellness",
                onClick = {
                    currentScreen = "Wellness"
                    navController.navigate(Screen.Wellness.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                    }
                }
            )

            NavigationButton(
                text = "Emergency",
                isSelected = currentScreen == "Emergency",
                onClick = {
                    currentScreen = "Emergency"
                    navController.navigate(Screen.Emergency.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                    }
                }
            )

            NavigationButton(
                text = "Co-Pilot",
                isSelected = currentScreen == "CoPilot",
                onClick = {
                    currentScreen = "CoPilot"
                    navController.navigate(Screen.CoPilot.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content Area
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(viewModel)
                }
                composable(Screen.Wellness.route) {
                    WellnessScreen(viewModel)
                }
                composable(Screen.Emergency.route) {
                    EmergencyScreen(viewModel)
                }
                composable(Screen.CoPilot.route) {
                    CoPilotScreen(viewModel)
                }
            }
        }

        // Bottom Bar
        BottomBar()
    }

    // Overlay Screens with AI Service Integration
    val emergencyState by viewModel.emergencyActivation.collectAsState()
    val aiConversationState by viewModel.aiConversationState.collectAsState()
    
    // Collect AI messages from service
    val aiMessages by (viewModel.conversationMessages ?: remember { 
        mutableStateOf(emptyList()) 
    }).collectAsState()

    // Emergency Activation Overlay
    if (emergencyState.isTriggered) {
        EmergencyActivationScreen(
            emergencyData = emergencyState,
            onCancelEmergency = { viewModel.cancelEmergency() },
            onConfirmEmergency = {
                // Confirm emergency and contact services
                viewModel.speakToDriver("Emergency confirmed. Contacting emergency services now.")
            }
        )
    }

    // AI Conversation Overlay - Now with message history
    if (aiConversationState.isActive) {
        AIConversationScreen(
            conversationState = aiConversationState,
            messages = aiMessages,
            onStartListening = { viewModel.startListening() },
            onStopListening = { viewModel.stopListening() },
            onDismiss = { viewModel.dismissAIConversation() }
        )
    }
}

@Composable
fun NavigationButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                Color(0xFF06B6D4) 
            else 
                Color(0xFF374151)
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.height(48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}