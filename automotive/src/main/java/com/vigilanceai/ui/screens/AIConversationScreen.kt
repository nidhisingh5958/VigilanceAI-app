package com.vigilanceai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vigilanceai.data.AIConversationState
import com.vigilanceai.ui.theme.*

@Composable
fun AIConversationScreen(
    conversationState: AIConversationState,
    onStartListening: () -> Unit = {},
    onStopListening: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    // Breathing animation for AI avatar
    val infiniteTransition = rememberInfiniteTransition(label = "ai_breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    // Listening pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Cyan.copy(alpha = 0.1f),
                        Color.Black.copy(alpha = 0.95f)
                    ),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(40.dp)
        ) {
            // AI Avatar with Animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(if (conversationState.isListening) pulseScale else breathingScale)
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Cyan.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                // Main AI circle
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Cyan, Blue)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Alert Message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (conversationState.triggerReason) {
                            "FATIGUE" -> "FATIGUE DETECTED"
                            "DROWSINESS" -> "DROWSINESS DETECTED"
                            "STRESS" -> "HIGH STRESS DETECTED"
                            else -> "ATTENTION REQUIRED"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = Red,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = conversationState.currentMessage.ifEmpty {
                            "I've noticed you might need a break. Let's talk about how you're feeling."
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Voice Interaction Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Listen Button
                Button(
                    onClick = if (conversationState.isListening) onStopListening else onStartListening,
                    modifier = Modifier
                        .size(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (conversationState.isListening) Red else Emerald
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (conversationState.isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (conversationState.isListening) "Stop" else "Listen",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Status Text
                Text(
                    text = if (conversationState.isListening) "Listening..." else "Tap to speak",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (conversationState.isListening) Cyan else Gray400,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Quick Response Options
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickResponseButton(
                    text = "I'm fine",
                    onClick = { /* Handle response */ }
                )
                QuickResponseButton(
                    text = "Need break",
                    onClick = { /* Handle response */ }
                )
                QuickResponseButton(
                    text = "Find rest stop",
                    onClick = { /* Handle response */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dismiss Button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Continue Driving",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gray400
                )
            }
        }
    }
}

@Composable
fun QuickResponseButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Gray800.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}