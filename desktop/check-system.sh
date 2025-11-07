#!/bin/bash

# Kotatsu Desktop - System Requirements Check

echo "======================================"
echo "Kotatsu Desktop System Check"
echo "======================================"
echo ""

EXIT_CODE=0

# Check Java
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
    echo "✓ Java found: $JAVA_VERSION"
    
    if [ "$JAVA_MAJOR" -lt 11 ]; then
        echo "✗ Java 11 or higher is required"
        EXIT_CODE=1
    else
        echo "✓ Java version is compatible"
    fi
else
    echo "✗ Java not found"
    echo "  Please install JDK 11 or higher"
    EXIT_CODE=1
fi

echo ""

# Check display
echo "Checking display..."
if [ -n "$DISPLAY" ] || [ -n "$WAYLAND_DISPLAY" ]; then
    echo "✓ Display server available"
else
    echo "✗ No display server found"
    echo "  Make sure you're running in a graphical environment"
    EXIT_CODE=1
fi

echo ""

# Check disk space
echo "Checking disk space..."
HOME_SPACE=$(df -h "$HOME" | awk 'NR==2 {print $4}')
echo "✓ Available space in home directory: $HOME_SPACE"

echo ""

# Check architecture
echo "Checking system architecture..."
ARCH=$(uname -m)
echo "  Architecture: $ARCH"
if [ "$ARCH" = "x86_64" ]; then
    echo "✓ 64-bit architecture supported"
else
    echo "⚠ Warning: $ARCH architecture may not be fully supported"
fi

echo ""

# Check distribution
echo "Detecting Linux distribution..."
if [ -f /etc/os-release ]; then
    . /etc/os-release
    echo "  Distribution: $NAME"
    echo "  Version: $VERSION"
fi

echo ""
echo "======================================"

if [ $EXIT_CODE -eq 0 ]; then
    echo "✓ All checks passed!"
    echo ""
    echo "You can run Kotatsu Desktop with:"
    echo "  ./desktop/build.sh run"
    echo ""
    echo "Or build packages with:"
    echo "  ./desktop/build.sh package-deb"
    echo "  ./desktop/build.sh package-rpm"
    echo "  ./desktop/build.sh package-appimage"
else
    echo "✗ Some requirements are not met"
    echo "  Please fix the issues above and try again"
fi

echo "======================================"

exit $EXIT_CODE
