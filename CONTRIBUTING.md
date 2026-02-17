# Contributing to Movie Streamer

Thank you for your interest in contributing to the Movie Streamer Android TV app! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help others learn and grow

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- Clear description of the bug
- Steps to reproduce
- Expected vs actual behavior
- Device/emulator information
- Screenshots if applicable

### Suggesting Features

Feature requests are welcome! Please include:
- Clear description of the feature
- Use case and benefits
- Potential implementation approach

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Make your changes** following the code style guidelines
4. **Test thoroughly** on Android TV emulator/device
5. **Commit with clear messages**: `git commit -m "Add feature: description"`
6. **Push to your fork**: `git push origin feature/your-feature-name`
7. **Create a Pull Request** with a detailed description

## Development Setup

1. Install Android Studio Hedgehog or newer
2. Clone the repository
3. Get a TMDB API key (see SETUP.md)
4. Configure the API key in `MovieRepository.kt`
5. Build and run on Android TV emulator

## Code Style Guidelines

### Kotlin

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Write self-documenting code with minimal comments
- Prefer immutable data structures

### Jetpack Compose

- Keep Composables small and focused
- Extract reusable components
- Use `remember` for state management
- Follow Material Design 3 guidelines

### Architecture

- Follow MVVM pattern
- Keep ViewModels platform-independent
- Use Repository pattern for data access
- Separate concerns clearly

## Testing

- Test on actual Android TV device when possible
- Test D-Pad navigation thoroughly
- Verify focus states and animations
- Test with slow network connections
- Test error scenarios

## TV-Specific Guidelines

### Focus Management
- All interactive elements must be focusable
- Focus order should be intuitive (left-to-right, top-to-bottom)
- Clear visual feedback for focused elements

### 10-Foot UI
- Use large text sizes (minimum 16sp for body text)
- High contrast colors
- Adequate padding and spacing
- No small clickable areas

### Performance
- Optimize image loading and caching
- Handle network errors gracefully
- Minimize blocking operations
- Use lazy loading for lists

## Commit Message Guidelines

Use clear, descriptive commit messages:

- `Add: New feature or file`
- `Fix: Bug fix`
- `Update: Modify existing feature`
- `Refactor: Code restructuring`
- `Docs: Documentation changes`
- `Style: Code formatting`

Example:
```
Add: Movie search functionality with TMDB API

- Implement search screen with text input
- Add search API endpoint in TMDBApiService
- Create SearchViewModel for state management
- Add keyboard navigation support
```

## Areas for Contribution

### High Priority
- Unit tests for ViewModels
- Integration tests for Repository
- Search functionality
- Multiple content categories
- User preferences/settings

### Medium Priority
- Watchlist feature
- Continue watching functionality
- Movie details screen with cast info
- Subtitle support
- Different video quality options

### Low Priority
- Animations and transitions
- Custom themes
- Parental controls
- User profiles

## Legal Compliance

**Important**: All contributions must:
- Use only legal content sources
- Respect copyright and licensing
- Not facilitate piracy
- Follow TMDB API terms of service

Do not submit code that:
- Scrapes copyrighted content
- Violates content provider terms
- Includes hardcoded credentials
- Contains malicious code

## Documentation

When contributing:
- Update README.md if adding features
- Document complex logic with comments
- Add KDoc for public APIs
- Update ARCHITECTURE.md for structural changes

## Questions?

- Check existing issues and documentation
- Ask in issue comments
- Be specific about your question

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

Thank you for contributing to Movie Streamer! 🎬
