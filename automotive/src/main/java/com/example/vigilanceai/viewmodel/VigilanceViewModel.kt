package com.example.vigilanceai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vigilanceai.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    // Simulate real-time updates
    fun startMonitoring() {
        viewModelScope.launch {
            // Add periodic updates here if needed
        }
    }

    fun updateMetrics(newMetrics: DriverMetrics) {
        _metrics.value = newMetrics
    }

    fun addInteraction(interaction: InteractionHistory) {
        _interactionHistory.value = listOf(interaction) + _interactionHistory.value
    }
}