

# SecretRoutes - Change Log

## Version 0.4.10

### Bug Fixes
- Fixed compatibility issue with DRM.
- Resolved auto-update trigger affecting other mods' updates.
- Corrected SSL certificate scope to avoid global setting.
- Updated color profile loading to support older profiles.

### Developer Features
- **Debug Command**: Added a debug command for streamlined troubleshooting.
- **Constants Class**: Created a constants class to simplify the debug command.
- **OnServerTick Event**: Added a server tick event for accurate timers and movement detection.

### Rendering
- Added options to toggle individual waypoint types.

### Recording
- Enabled etherwarps to activate on click (manual ping setting required; may switch to `onServerTick` in the future).
- Added hotkeys for recording routes.
