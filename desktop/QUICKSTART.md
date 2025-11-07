# Kotatsu Desktop - Quick Start Guide

## Installation on Linux

### Option 1: Build from Source (Recommended for Development)

```bash
# Navigate to the project directory
cd /path/to/Kotatsu

# Run the application directly
./desktop/build.sh run

# Or build packages
./desktop/build.sh package-deb    # For Debian/Ubuntu
./desktop/build.sh package-rpm    # For Fedora/RHEL
./desktop/build.sh package-appimage  # Universal Linux
```

### Option 2: Using Gradle Directly

```bash
# Run the application
./gradlew :desktop:run

# Create DEB package
./gradlew :desktop:packageDeb

# Create RPM package
./gradlew :desktop:packageRpm

# Create AppImage
./gradlew :desktop:packageAppImage
```

## First Launch

When you first launch Kotatsu Desktop:

1. **Theme Selection** - Choose between Light, Dark, or System theme in Settings
2. **Download Directory** - Configure where manga downloads will be saved
3. **Browse Sources** - Access 1200+ manga sources from the Browse tab
4. **Library** - Add manga to your library for easy access

## File Locations

### Configuration
- Settings: `~/.kotatsu/settings.json`
- Data: `~/.kotatsu/data/`
- Downloads: `~/Downloads/Kotatsu/` (default)

### Logs
Application logs are written to standard output. To save logs:

```bash
./gradlew :desktop:run > kotatsu.log 2>&1
```

## System Requirements

### Minimum
- **OS**: Any Linux distribution (64-bit)
- **RAM**: 512 MB
- **Storage**: 100 MB for application + space for manga
- **Java**: JDK 11 or higher

### Recommended
- **OS**: Ubuntu 20.04+, Fedora 35+, or equivalent
- **RAM**: 2 GB or more
- **Storage**: 1 GB + space for manga library
- **Java**: JDK 17 or higher
- **Display**: 1280x800 or higher resolution

## Features

### Currently Implemented
- ✅ Modern Material 3 UI
- ✅ Multi-tab navigation (Home, Library, Browse, History, Settings)
- ✅ Theme customization (Light/Dark/System)
- ✅ Settings persistence
- ✅ Network client setup

### In Development
- ⏳ Manga source browsing and search
- ⏳ Manga reader with page navigation
- ⏳ Download manager
- ⏳ Local database for library
- ⏳ Reading history tracking
- ⏳ Bookmarks and favorites

## Troubleshooting

### Application won't start
```bash
# Check Java version
java -version

# Should show Java 11 or higher
# If not, install JDK 11+
```

### Build errors
```bash
# Clean and rebuild
./gradlew clean
./gradlew :desktop:build
```

### Missing dependencies
```bash
# Ensure Gradle wrapper is executable
chmod +x gradlew

# Download dependencies
./gradlew :desktop:dependencies
```

## Development

### Project Structure
```
desktop/
├── build.gradle                 # Build configuration
├── build.sh                     # Build helper script
└── src/main/kotlin/org/koitharu/kotatsu/desktop/
    ├── Main.kt                 # Entry point
    ├── core/
    │   ├── di/                 # Dependency injection
    │   ├── network/            # HTTP client
    │   └── prefs/              # Settings
    └── ui/
        ├── navigation/         # Navigation
        ├── screens/            # UI screens
        └── theme/              # Material theme
```

### Adding Features

1. **Network requests**: Use `NetworkClient` in `core/network/`
2. **Settings**: Extend `AppSettings` in `core/prefs/`
3. **New screens**: Add to `ui/screens/` and update `AppNavigation.kt`
4. **Dependencies**: Use `AppContainer` for dependency injection

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly on Linux
5. Submit a pull request

See [CONTRIBUTING.md](../CONTRIBUTING.md) for detailed guidelines.

## Support

- **Issues**: https://github.com/KotatsuApp/Kotatsu/issues
- **Discussions**: https://github.com/KotatsuApp/Kotatsu/discussions
- **Discord**: https://discord.gg/NNJ5RgVBC5
- **Telegram**: https://t.me/kotatsuapp

## License

GNU General Public License v3.0 - See [LICENSE](../LICENSE)
