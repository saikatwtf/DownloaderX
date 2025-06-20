# Saikat-DownloaderX

A Gothic-themed Android app for downloading and processing media from various sources, including encrypted MPD/M3U8 streams and YouTube videos.

## Features

- **Gothic UI**: Black-pink themed interface with 5 switchable themes (Light, Dark, AMOLED, Retro Green, Hacker Neon)
- **Horror-style Splash Screen**: Animated splash with Gothic scenery, spiders, bats, and mysterious symbols
- **Media Downloader**:
  - MPD/M3U8 stream downloading with N_m3u8DL-RE
  - Decryption support with mp4decrypt
  - Format selection (MKV/MP4)
  - Subtitle support
  - Automatic retry and fallback system
- **Live M3U8 Scheduler**: Schedule recordings with start/end times
- **YouTube Downloader**: Download videos and playlists with format detection
- **GeckoView Browser**: Built-in browser with media sniffing and Firefox extension support
- **Converter Tools**:
  - Video editor (trim, merge, convert)
  - Audio editor (trim, merge)
  - Image converter (HEIC to JPG)
- **System Tools**:
  - Junk Cleaner
  - Phone Booster
  - Photo Cleaner (blurry/duplicate detection)
  - Battery Saver
  - File Recovery Tool

## Requirements

- Android 11 or higher
- Android Studio Arctic Fox or newer

## Build Instructions

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/Saikat-DownloaderX.git
   ```

2. Open the project in Android Studio.

3. Download the required binaries and place them in the `app/src/main/assets/binaries` directory:
   - ffmpeg
   - mp4decrypt
   - yt-dlp
   - N_m3u8DL-RE

4. Build the project:
   - Click on "Build" in the menu
   - Select "Build Bundle(s) / APK(s)"
   - Choose "Build APK(s)"

5. Install the APK on your device.

## Usage

1. **Media Downloader**:
   - Enter the URL of the MPD/M3U8 stream
   - Enter decryption keys in KEY:ID format if needed
   - Select output format (MKV/MP4)
   - Enable subtitles and logging if needed
   - Click "Download"

2. **Live M3U8 Scheduler**:
   - Enter the URL of the live stream
   - Set start and end times in IST
   - Click "Schedule Recording"

3. **YouTube Downloader**:
   - Enter the YouTube URL
   - Enable format detection or playlist download if needed
   - Click "Download"

4. **Browser**:
   - Browse websites
   - Click "Sniff Media" to detect media streams
   - Manage extensions and bookmarks

5. **Converter**:
   - Use the video editor to trim, merge, or convert videos
   - Use the audio editor to trim or merge audio files
   - Convert HEIC images to JPG

6. **System Tools**:
   - Clean junk files
   - Boost phone performance
   - Clean blurry or duplicate photos
   - Enable battery saver mode
   - Recover deleted files

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- [N_m3u8DL-RE](https://github.com/nilaoda/N_m3u8DL-RE)
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)
- [FFmpeg](https://ffmpeg.org/)
- [mp4decrypt](https://www.bento4.com/)
- [GeckoView](https://mozilla.github.io/geckoview/)