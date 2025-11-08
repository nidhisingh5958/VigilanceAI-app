// VigilanceViewModel.kt
// Update your existing ViewModel to integrate with VigilanceAIService

package com.vigilanceai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vigilanceai.data.AIConversationState
import com.vigilanceai.data.EmergencyActivation
import com.vigilanceai.data.WellnessMetrics
import com.vigilanceai.service.AIState
import com.vigilanceai.service.VigilanceAIService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class VigilanceViewModel : ViewModel() {
    
    // Reference to AI Service (will be set from MainActivity)
    private var aiService: VigilanceAIService? = null
    
    // Existing metrics flow
    private val _metrics = MutableStateFlow(WellnessMetrics())
    val metrics: StateFlow<WellnessMetrics> = _metrics.asStateFlow()
    
    // Existing emergency activation flow
    private val _emergencyActivation = MutableStateFlow(EmergencyActivation())
    val emergencyActivation: StateFlow<EmergencyActivation> = _emergencyActivation.asStateFlow()
    
    // AI Conversation State - Updated to integrate with service
    private val _aiConversationState = MutableStateFlow(AIConversationState())
    val aiConversationState: StateFlow<AIConversationState> = _aiConversationState.asStateFlow()
    
    // AI Service state observers
    val aiServiceState: StateFlow<AIState>?
        get() = aiService?.aiState
    
    val isAIActivated: StateFlow<Boolean>?
        get() = aiService?.isActivated
    
    val conversationMessages: StateFlow<List<com.vigilanceai.service.AIMessage>>?
        get() = aiService?.conversationMessages
    
    init {
        // Start simulated metric updates
        startMetricSimulation()
        
        // Start monitoring for fatigue detection
        startFatigueMonitoring()
    }
    
    // Bind AI Service
    fun bindAIService(service: VigilanceAIService) {
        aiService = service
        
        // Observe AI state changes
        viewModelScope.launch {
            service.aiState.collect { state ->
                updateAIConversationState(state)
            }
        }
        
        // Observe activation changes
        viewModelScope.launch {
            service.isActivated.collect { isActivated ->
                _aiConversationState.update { it.copy(isActive = isActivated) }
            }
        }
        
        // Start continuous listening for wake word
        service.startContinuousListening()
    }
    
    private fun updateAIConversationState(state: AIState) {
        _aiConversationState.update { 
            it.copy(
                isListening = state == AIState.LISTENING,
                isProcessing = state == AIState.PROCESSING,
                isSpeaking = state == AIState.SPEAKING
            )
        }
    }
    
    // Voice interaction controls
    fun startListening() {
        aiService?.startContinuousListening()
    }
    
    fun stopListening() {
        aiService?.stopListening()
    }
    
    fun dismissAIConversation() {
        aiService?.deactivateAI()
        _aiConversationState.update { it.copy(isActive = false) }
    }
    
    // Trigger AI conversation with specific reason
    fun triggerAIConversation(reason: String, message: String = "") {
        _aiConversationState.update {
            it.copy(
                isActive = true,
                triggerReason = reason,
                currentMessage = message.ifEmpty {
                    when (reason) {
                        "FATIGUE" -> "I've noticed you might need a break. Let's talk about how you're feeling."
                        "DROWSINESS" -> "I've detected signs of drowsiness. Your safety is important. How are you feeling?"
                        "STRESS" -> "I sense you might be stressed. Would you like to talk about it?"
                        else -> "I'm here to help. How can I assist you?"
                    }
                }
            )
        }
        
        // Also trigger the AI service emergency response
        aiService?.triggerEmergencyResponse(reason, message)
    }
    
    // Emergency handling
    fun triggerEmergency(type: String, location: String = "Unknown") {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        
        _emergencyActivation.value = EmergencyActivation(
            isTriggered = true,
            triggerType = type,
            location = location,
            timestamp = timestamp,
            emergencyContacted = false,
            responseTime = "Estimating..."
        )
        
        // Trigger AI emergency response
        aiService?.triggerEmergencyResponse(type, "Emergency detected at $location")
        
        // Simulate emergency services contact
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _emergencyActivation.update {
                it.copy(
                    emergencyContacted = true,
                    responseTime = "5-7 minutes"
                )
            }
        }
    }
    
    fun cancelEmergency() {
        _emergencyActivation.value = EmergencyActivation()
        aiService?.speak("Emergency cancelled. Glad you're okay. Let me know if you need anything.")
    }
    
    // Fatigue monitoring with AI integration
    private fun startFatigueMonitoring() {
        viewModelScope.launch {
            metrics.collect { currentMetrics ->
                // Check PERCLOS (eye closure)
                if (currentMetrics.perclos > 70) {
                    triggerAIConversation(
                        "DROWSINESS",
                        "Critical drowsiness detected. PERCLOS at ${currentMetrics.perclos}%"
                    )
                } else if (currentMetrics.perclos > 50) {
                    triggerAIConversation(
                        "FATIGUE",
                        "Elevated drowsiness detected. PERCLOS at ${currentMetrics.perclos}%"
                    )
                }
                
                // Check emotion state for stress
                if (currentMetrics.emotionState.contains("Stressed", ignoreCase = true) ||
                    currentMetrics.emotionState.contains("Anxious", ignoreCase = true)) {
                    triggerAIConversation(
                        "STRESS",
                        "High stress levels detected in facial analysis"
                    )
                }
                
                // Check heart rate
                if (currentMetrics.heartRate > 100) {
                    triggerAIConversation(
                        "STRESS",
                        "Elevated heart rate detected: ${currentMetrics.heartRate} BPM"
                    )
                }
            }
        }
    }
    
    // Simulate real-time metrics (replace with actual sensor data)
    private fun startMetricSimulation() {
        viewModelScope.launch {
            var perclosValue = 12
            var heartRate = 72
            var emotionIndex = 0
            val emotions = listOf(
                "Calm & Focused" to 85,
                "Alert" to 92,
                "Slightly Tired" to 75,
                "Calm" to 88
            )
            
            while (true) {
                kotlinx.coroutines.delay(3000) // Update every 3 seconds
                
                // Simulate varying metrics
                perclosValue = (perclosValue + (-3..3).random()).coerceIn(8, 95)
                heartRate = (heartRate + (-2..2).random()).coerceIn(60, 120)
                
                val (emotion, confidence) = emotions[emotionIndex % emotions.size]
                emotionIndex++
                
                _metrics.value = WellnessMetrics(
                    perclos = perclosValue,
                    heartRate = heartRate,
                    emotionState = emotion,
                    emotionConfidence = confidence,
                    laneKeeping = if (perclosValue < 30) "Stable" else "Drifting",
                    steeringInput = if (perclosValue < 40) "Smooth" else "Erratic",
                    speedControl = "Steady"
                )
            }
        }
    }
    
    // Manual AI activation (for testing or user-initiated)
    fun activateAI() {
        aiService?.let { service ->
            if (service.isActivated.value) {
                // Already activated, just open conversation
                _aiConversationState.update { it.copy(isActive = true) }
            } else {
                // Activate and open conversation
                service.startContinuousListening()
                _aiConversationState.update {
                    it.copy(
                        isActive = true,
                        currentMessage = "How can I assist you today?"
                    )
                }
            }
        }
    }
    
    // Speak through AI
    fun speakToDriver(message: String) {
        aiService?.speak(message)
    }
    
    override fun onCleared() {
        super.onCleared()
        // Clean up if needed
        aiService?.stopListening()
    }
}