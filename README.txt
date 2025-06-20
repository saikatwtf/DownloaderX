# Saikat-DownloaderX - Build Instructions

## Overview
Saikat-DownloaderX is a Gothic-themed Android app for downloading and processing media from various sources, including encrypted MPD/M3U8 streams and YouTube videos.

## Requirements
- Android Studio Arctic Fox or newer
- Android SDK 30 (Android 11) or higher
- Java Development Kit (JDK) 8 or higher

## Build Instructions

### 1. Clone the Repository
```
git clone https://github.com/yourusername/Saikat-DownloaderX.git
cd Saikat-DownloaderX
```

### 2. Download Required Binaries
Download the following binaries and place them in the `app/src/main/assets/binaries` directory:
- ffmpeg (https://ffmpeg.org/download.html)
- mp4decrypt (https://www.bento4.com/downloads/)
- yt-dlp (https://github.com/yt-dlp/yt-dlp/releases)
- N_m3u8DL-RE (https://github.com/nilaoda/N_m3u8DL-RE/releases)

Make sure to download the appropriate versions for Android (ARM or x86 depending on your target device).

### 3. Open the Project in Android Studio
- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned repository and select it

### 4. Configure the Project
- Wait for Gradle sync to complete
- Ensure that the project is configured to use SDK 30 or higher
- Verify that all dependencies are properly resolved

### 5. Build the Project
- Click on "Build" in the menu
- Select "Build Bundle(s) / APK(s)"
- Choose "Build APK(s)"

### 6. Install the APK
- Connect your Android device to your computer
- Enable USB debugging on your device
- Click on "Run" in Android Studio and select your device

## Features
- Gothic UI with 5 switchable themes
- Horror-style animated splash screen
- Media downloader for MPD/M3U8 streams
- YouTube downloader
- GeckoView browser with media sniffing
- Video, audio, and image converter tools
- System optimization tools

## Troubleshooting
- If you encounter build errors related to dependencies, try updating the Gradle version or the dependencies in the build.gradle file
- If the app crashes when using binary tools, ensure that the binaries are properly placed in the assets directory and have the correct permissions
- For issues with GeckoView, make sure you have the correct version specified in the build.gradle file

## License
This project is licensed under the MIT License - see the LICENSE file for details.