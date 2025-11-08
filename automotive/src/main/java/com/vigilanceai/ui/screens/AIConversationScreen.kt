// Enhanced AIConversationScreen.kt
// Replace your existing AIConversationScreen.kt with this version

package com.vigilanceai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vigilanceai.data.AIConversationState
import com.vigilanceai.service.AIMessage
import com.vigilanceai.ui.components.WaveformAnimation
import com.vigilanceai.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AIConversationScreen(
    conversationState: AIConversationState,
    messages: List<AIMessage> = emptyList(), // Add messages parameter
    onStartListening: () -> Unit = {},
    onStopListening: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

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
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VigilanceAI",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            conversationState.isListening -> "Listening..."
                            conversationState.isProcessing -> "Processing..."
                            conversationState.isSpeaking -> "Speaking..."
                            else -> "Ready to assist"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cyan
                    )
                </Column>
                
                // Close button
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Alert Message Card (if triggered by emergency)
            AnimatedVisibility(
                visible = conversationState.triggerReason.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (conversationState.triggerReason) {
                            "FATIGUE", "DROWSINESS" -> Red.copy(alpha = 0.2f)
                            "STRESS" -> Color(0xFFFFA500).copy(alpha = 0.2f)
                            else -> Cyan.copy(alpha = 0.2f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = when (conversationState.triggerReason) {
                                "FATIGUE", "DROWSINESS" -> Red
                                "STRESS" -> Color(0xFFFFA500)
                                else -> Cyan
                            },
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = conversationState.triggerReason,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (conversationState.currentMessage.isNotEmpty()) {
                                Text(
                                    text = conversationState.currentMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray300
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Waveform Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                WaveformAnimation(
                    isListening = conversationState.isListening,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Conversation History
            if (messages.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Gray900.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { message ->
                            ConversationBubble(message)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // Empty state message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Say \"VigilanceAI\" to start a conversation\nor tap the microphone button below",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray400,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Voice Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Microphone Button
                Button(
                    onClick = if (conversationState.isListening) onStopListening else onStartListening,
                    modifier = Modifier.size(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (conversationState.isListening) Red else Emerald
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (conversationState.isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (conversationState.isListening) "Stop" else "Listen",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (conversationState.isListening) "Listening..." else "Tap to speak",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (conversationState.isListening) Cyan else Gray400,
                        fontWeight = FontWeight.Medium
                    )
                    if (!conversationState.isListening) {
                        Text(
                            text = "or say \"VigilanceAI\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray500
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickResponseButton(
                    text = "I'm fine",
                    onClick = { /* TODO: Send quick response */ }
                )
                QuickResponseButton(
                    text = "Need break",
                    onClick = { /* TODO: Send quick response */ }
                )
                QuickResponseButton(
                    text = "Find rest stop",
                    onClick = { /* TODO: Send quick response */ }
                )
            }
        }
    }
}

@Composable
fun ConversationBubble(message: AIMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isAI) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier.widthIn(max = 400.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isEmergency) {
                    Red.copy(alpha = 0.3f)
                } else if (message.isAI) {
                    Cyan.copy(alpha = 0.2f)
                } else {
                    Gray700
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isAI) 4.dp else 16.dp,
                bottomEnd = if (message.isAI) 16.dp else 4.dp
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (message.isEmergency) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "EMERGENCY ALERT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
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

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}