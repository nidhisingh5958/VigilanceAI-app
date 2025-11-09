# VigilanceAI-app


Once running, try these voice commands:
- "VigilanceAI" → Activates AI
- "I'm tired" → Suggests break
- "I'm stressed" → Offers calming help
- "Find rest stop" → Navigation help
- "Play music" → Music suggestions
- "I need a break" → Break recommendations
- "Help" → General assistance

# Project Structure
com/vigilanceai/
├── MainActivity.kt 
├── data/
│   └── Models.kt 
│   └── DriveMetrics.kt 
├── service/
│   └── VigilanceAIService.kt 
│   └── VigilanceCarAppService.kt 
│   └── VigilanceCarMainScreen.kt 
├── viewmodel/
│   └── VigilanceViewModel.kt 
├── navigation/
│   └── NavGraph.kt 
│   └── Screen.kt 
└── ui/
    ├── screens/
    │   ├── AIConversationScreen.kt 
    │   ├── EmergencyActivationScreen.kt
    │   ├── WellnessScreen.kt 
    │   ├── DashboardScreen.kt
    │   ├── EmergencyScreen.kt
    │   └── CoPilotScreen.kt 
    ├── components/
    │   ├── TopBar.kt 
    │   ├── BottomBar.kt 
    │   └── WaveformAnimation.kt 
    │   └── MetricCard.kt 
    └── theme/ 
    │   └── Color.kt 
    │   └── Theme.kt 
    │   └── Type.kt 
