# Kotatsu Desktop

A Linux desktop port of Kotatsu, the free and open-source manga reader.

## Features

- **1200+ Manga Sources** - Access manga from various online sources
- **Material 3 Design** - Modern and clean UI for desktop
- **Offline Reading** - Download manga for offline access
- **Reading History** - Track your reading progress
- **Customizable** - Dark/Light theme support

## Building from Source

### Prerequisites

- JDK 11 or higher
- Gradle 8.0+

### Build Commands

```bash
# Build the application
./gradlew :desktop:build

# Run the application
./gradlew :desktop:run

# Create distributable packages
./gradlew :desktop:packageDeb      # For Debian/Ubuntu
./gradlew :desktop:packageRpm      # For Fedora/RHEL
./gradlew :desktop:packageAppImage # AppImage format
```

## Installation

### Debian/Ubuntu

```bash
sudo dpkg -i desktop/build/compose/binaries/main/deb/kotatsu_9.4.1_amd64.deb
```

### Fedora/RHEL

```bash
sudo rpm -i desktop/build/compose/binaries/main/rpm/kotatsu-9.4.1.x86_64.rpm
```

### AppImage

```bash
chmod +x kotatsu-9.4.1.AppImage
./kotatsu-9.4.1.AppImage
```

## Running

After installation, you can launch Kotatsu from your application menu or run:

```bash
kotatsu
```

## Configuration

Configuration files are stored in:
```
~/.kotatsu/
├── settings.json    # Application settings
└── data/           # Database and cache
```

## Development

This is a Kotlin/JVM application using:
- **Compose for Desktop** - UI framework
- **OkHttp** - Networking
- **Kotlinx Serialization** - JSON handling
- **SQLite** - Local database
- **Kotatsu Parsers** - Manga source parsers

## Project Structure

```
desktop/
├── src/main/kotlin/org/koitharu/kotatsu/desktop/
│   ├── Main.kt                           # Application entry point
│   ├── core/
│   │   ├── di/AppContainer.kt           # Dependency injection
│   │   ├── network/NetworkClient.kt     # HTTP client wrapper
│   │   └── prefs/AppSettings.kt         # Settings management
│   └── ui/
│       ├── navigation/AppNavigation.kt  # Navigation logic
│       ├── screens/                     # UI screens
│       └── theme/Theme.kt               # Material 3 theme
└── build.gradle                         # Build configuration
```

## License

GNU General Public License v3.0 - See [LICENSE](../LICENSE) for details.

## Contributing

This is a community-maintained desktop port. Contributions are welcome!

See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

## Original Project

This is a desktop adaptation of the Android app:
- Original repository: https://github.com/KotatsuApp/Kotatsu
- Parsers library: https://github.com/KotatsuApp/kotatsu-parsers

## Known Limitations

This initial port includes:
- ✅ Basic UI structure
- ✅ Settings management
- ✅ Network client setup
- ⏳ Manga source integration (in progress)
- ⏳ Local database (in progress)
- ⏳ Reader implementation (in progress)
- ⏳ Download manager (in progress)

## Roadmap

- [ ] Implement manga source browsing
- [ ] Add local database with Room
- [ ] Create manga reader view
- [ ] Implement download manager
- [ ] Add bookmarks and favorites
- [ ] Implement sync functionality
- [ ] Add search functionality
- [ ] Improve UI/UX

## Support

For issues and feature requests, please use the GitHub issue tracker.
