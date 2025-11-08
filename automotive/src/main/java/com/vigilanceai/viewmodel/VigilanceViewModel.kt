package com.vigilanceai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vigilanceai.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class VigilanceViewModel : ViewModel() {

    private val _metrics = MutableStateFlow(DriverMetrics())
    val metrics: StateFlow<DriverMetrics> = _metrics.asStateFlow()

    private val _emergencyStatus = MutableStateFlow(EmergencyStatus())
    val emergencyStatus: StateFlow<EmergencyStatus> = _emergencyStatus.asStateFlow()

    private val _currentSuggestion = MutableStateFlow(
        CoPilotSuggestion(
            title = "Time for a break?",
            description = "You've been driving for 2 hours. There's a rest stop 5km ahead with good reviews.",
            icon = "coffee",
            primaryAction = "Navigate",
            secondaryAction = "Remind in 30min"
        )
    )
    val currentSuggestion: StateFlow<CoPilotSuggestion> = _currentSuggestion.asStateFlow()

    private val _interactionHistory = MutableStateFlow(listOf(
        InteractionHistory("10:30", "Suggested caffeine break - Accepted"),
        InteractionHistory("09:15", "Played focus playlist - Stress reduced by 15%"),
        InteractionHistory("08:45", "Morning greeting - Trip started")
    ))
    val interactionHistory: StateFlow<List<InteractionHistory>> = _interactionHistory.asStateFlow()

    private val _emergencyActivation = MutableStateFlow(EmergencyActivation())
    val emergencyActivation: StateFlow<EmergencyActivation> = _emergencyActivation.asStateFlow()

    private val _aiConversationState = MutableStateFlow(AIConversationState())
    val aiConversationState: StateFlow<AIConversationState> = _aiConversationState.asStateFlow()

    // Simulate real-time updates
    fun startMonitoring() {
        viewModelScope.launch {
            // Start detection simulation
            simulateDetection()
        }
    }

    fun updateMetrics(newMetrics: DriverMetrics) {
        _metrics.value = newMetrics
    }

    fun addInteraction(interaction: InteractionHistory) {
        _interactionHistory.value = listOf(interaction) + _interactionHistory.value
    }

    // Emergency Detection and Activation
    fun triggerEmergency(type: String, location: String = "Current Location") {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _emergencyActivation.value = EmergencyActivation(
            isTriggered = true,
            triggerType = type,
            location = location,
            timestamp = timestamp
        )
        
        // Simulate contacting emergency services
        viewModelScope.launch {
            delay(3000) // 3 second delay
            _emergencyActivation.value = _emergencyActivation.value.copy(
                emergencyContacted = true,
                responseTime = "8-12 minutes"
            )
        }
    }

    fun cancelEmergency() {
        _emergencyActivation.value = EmergencyActivation()
    }

    // AI Conversation Management
    fun startAIConversation(reason: String) {
        _aiConversationState.value = AIConversationState(
            isActive = true,
            triggerReason = reason,
            currentMessage = getInitialMessage(reason)
        )
    }

    fun startListening() {
        _aiConversationState.value = _aiConversationState.value.copy(isListening = true)
    }

    fun stopListening() {
        _aiConversationState.value = _aiConversationState.value.copy(isListening = false)
    }

    fun dismissAIConversation() {
        _aiConversationState.value = AIConversationState()
    }

    private fun getInitialMessage(reason: String): String {
        return when (reason) {
            "FATIGUE" -> "I've detected signs of fatigue. How are you feeling right now?"
            "DROWSINESS" -> "You seem drowsy. Would you like me to find a safe place to rest?"
            "STRESS" -> "Your stress levels are elevated. Let's take a moment to address this."
            else -> "I'm here to help. How can I assist you?"
        }
    }

    // Simulate real-time monitoring
    fun simulateDetection() {
        viewModelScope.launch {
            // Simulate fatigue detection after 10 seconds
            delay(10000)
            val currentMetrics = _metrics.value.copy(fatigue = 85, isDrowsy = true)
            _metrics.value = currentMetrics
            startAIConversation("FATIGUE")
        }
    }

    fun simulateAccident() {
        val currentMetrics = _metrics.value.copy(accidentDetected = true)
        _metrics.value = currentMetrics
        triggerEmergency("ACCIDENT DETECTED")
    }
}