# Tales of Dresden - Mobile Tour Guide Application

## Overview
Tales of Dresden is a location-based Android application that provides guided tours of Dresden's landmarks through historical narratives. The app features character-based storytelling, location-aware content, and photo collection capabilities.

## Features
- Interactive map with landmark locations
- Character-based tour guides with unique narratives
- Location-based storytelling (activates within 30m)
- Photo capture and collection system
- Real-time navigation to landmarks
- Search functionality
- User collections management

## Technical Requirements
- Android 6.0 (API Level 23) or higher
- Google Play Services
- Location Services enabled
- Camera functionality
- Internet connectivity
- Storage access

## Installation
1. Copy the app-debug.apk to your Android device
2. Enable "Install from Unknown Sources" in your device settings
3. Install the APK
4. Grant required permissions when prompted (Location, Camera, Storage)

## Database
The app uses SQLite for data storage:
- Sample data is initialized from `assets/sample_data.json`
- Database file: `assets/tales_dd_database.db`
- Tables: Characters, Landmarks, Narratives, and Character-Landmark relationships

## Project Structure
```
app/
├── src/main/
│   ├── java/com.ridwan.tales_of_dd/
│   │   ├── activities/          # Main activities
│   │   ├── fragments/           # UI fragments
│   │   ├── data/               # Database and data models
│   │   ├── utils/              # Utility classes
│   │   └── ui/                 # UI components
│   ├── res/                    # Resources
│   └── assets/                 # Database and sample data
```

## Development Team
- Waheed Ridwan
- Marcel Siedlich
- Keying Fan

## Acknowledgments
Developed as part of the Mobile Cartography course at Technical University of Dresden, under the International Master's in Cartography program.

Let me know if you'd like me to add or modify any sections!
