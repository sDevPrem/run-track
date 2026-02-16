#!/bin/bash
# Script to verify Gradle build configuration

echo "Verifying Gradle build configuration..."
echo "==========================================="
echo ""

cd "$(dirname "$0")"

echo "Step 1: Cleaning project..."
./gradlew clean

echo ""
echo "Step 2: Building shared module..."
./gradlew :shared:build --stacktrace

echo ""
echo "Step 3: Building app module..."
./gradlew :app:assembleDebug --stacktrace

echo ""
echo "Build verification complete!"

