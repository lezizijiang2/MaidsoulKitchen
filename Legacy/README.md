# MaidSoul Kitchen Legacy Module

This module provides legacy compatibility support for MaidSoul Kitchen mod, specifically for Minecraft 1.20.1 and Forge.

## Features

- **Enhanced Class Analysis**: Advanced class, method, and field analysis for better mod compatibility
- **Mixin Management**: Dual control system for mixin loading based on mod availability
- **JarJar Support**: Legacy version compatibility through jarred dependencies  
- **Legacy Task System**: Compatibility layer for older mod versions

## Build Instructions

From the Legacy directory:

```bash
./gradlew build
```

## Integration

This module integrates with the main MaidSoul Kitchen mod by providing:

- Legacy task implementations for 1.20.1 compatibility
- Enhanced mixin plugin system for better mod detection
- Compatibility bridges for various cooking-related mods

## Configuration

The module automatically detects available mods and enables/disables features accordingly through the enhanced mixin system.