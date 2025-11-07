#!/bin/bash

# Kotatsu Desktop Build Script for Linux

set -e

echo "======================================"
echo "Kotatsu Desktop Build Script"
echo "======================================"
echo ""

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install JDK 11 or higher"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Error: Java 11 or higher is required (found Java $JAVA_VERSION)"
    exit 1
fi

echo "✓ Java version check passed"
echo ""

# Build options
BUILD_TYPE=${1:-run}

case $BUILD_TYPE in
    "run")
        echo "Running Kotatsu Desktop..."
        ./gradlew :desktop:run
        ;;
    "build")
        echo "Building Kotatsu Desktop..."
        ./gradlew :desktop:build
        echo ""
        echo "✓ Build complete!"
        ;;
    "package-deb")
        echo "Creating DEB package..."
        ./gradlew :desktop:packageDeb
        echo ""
        echo "✓ DEB package created!"
        echo "Location: desktop/build/compose/binaries/main/deb/"
        ;;
    "package-rpm")
        echo "Creating RPM package..."
        ./gradlew :desktop:packageRpm
        echo ""
        echo "✓ RPM package created!"
        echo "Location: desktop/build/compose/binaries/main/rpm/"
        ;;
    "package-appimage")
        echo "Creating AppImage..."
        ./gradlew :desktop:packageAppImage
        echo ""
        echo "✓ AppImage created!"
        echo "Location: desktop/build/compose/binaries/main/app/"
        ;;
    "package-all")
        echo "Creating all packages..."
        ./gradlew :desktop:packageDeb :desktop:packageRpm :desktop:packageAppImage
        echo ""
        echo "✓ All packages created!"
        ;;
    "clean")
        echo "Cleaning build artifacts..."
        ./gradlew clean
        echo "✓ Clean complete!"
        ;;
    *)
        echo "Usage: $0 [command]"
        echo ""
        echo "Commands:"
        echo "  run              - Run the application (default)"
        echo "  build            - Build the application"
        echo "  package-deb      - Create Debian/Ubuntu package"
        echo "  package-rpm      - Create Fedora/RHEL package"
        echo "  package-appimage - Create AppImage"
        echo "  package-all      - Create all packages"
        echo "  clean            - Clean build artifacts"
        echo ""
        exit 1
        ;;
esac
