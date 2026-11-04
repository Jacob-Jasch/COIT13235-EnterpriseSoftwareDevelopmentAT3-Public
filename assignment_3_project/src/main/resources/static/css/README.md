# CSS Modules - Refactored Structure

This directory contains the CSS modules for the application.

## Module Structure

### Foundation Modules

1. **`_variables.css`** - CSS custom properties and color scheme
   - Dark theme color definitions
   - Global design tokens
   - Consistent value management

2. **`_reset.css`** - CSS reset and base HTML styles
   - Browser default resets
   - Base typography and spacing
   - Box-sizing normalization

3. **`_base.css`** - Base browser styling
   - Custom scrollbar styling
   - Global element enhancements

### Layout Modules

4. **`_layout.css`** - Layout containers and flex structure
   - App container and main sections
   - Flexbox layouts and grid systems
   - Responsive layout foundations

5. **`_responsive.css`** - Media queries and responsive design
   - Mobile breakpoints
   - Adaptive layouts
   - Cross-device compatibility

### Component Modules

6. **`_forms.css`** - Forms, inputs, and form controls
   - Input styling and states
   - Form layouts and spacing
   - Search form interactions

7. **`_buttons.css`** - Button variants and states
   - Primary, secondary, danger buttons
   - Hover and active states
   - Favorite button toggles

8. **`_chat.css`** - Chat interface components
   - Message display and formatting
   - Chat headers and input forms
   - Live message styling

9. **`_music.css`** - Music section header and tabs
   - Tab navigation styling
   - Music section layout
   - Tab state management

10. **`_tracks-favorites.css`** - Track items and favorites functionality
    - Track display formatting
    - Favoriting interactions
    - Artist management components

11. **`_audio-player.css`** - Audio player styling
    - HTML5 audio control styling
    - Player enhancement and theming

12. **`_playlists.css`** - Complex playlist components and modals
    - Playlist creation and management
    - Modal dialogs and interactions
    - Playlist content display

13. **`_upload.css`** - Audio upload functionality styling
    - File upload form styling
    - Upload progress indicators
    - Upload validation states
    - File management interface

14. **`console-launcher.css`** - App console launcher styling
    - Console interface styling
    - Loading state animations
    - Console launcher layout
    - Professional console UI components

### Page-Specific Modules

15. **`_login.css`** - Login page specific styles
    - Authentication form styling
    - Login layout and theming

16. **`_error.css`** - Error page specific styles
    - Error message display
    - Error page layout

### Utility Modules

17. **`_utilities.css`** - Utility classes and helper styles
    - Screen reader utilities
    - Loading and error states
    - Helper classes

### Main Entry Point

18. **`styles.css`** - Main coordinator file
    - Imports all modules in dependency order
    - Manages CSS cascade and specificity

## Module Dependencies

The CSS modules are imported in a specific order to ensure proper cascade:

```
styles.css
├── _variables.css (foundation - no dependencies)
├── _reset.css (depends on: variables)
├── _base.css (depends on: variables)
├── _layout.css (depends on: variables)
├── _responsive.css (depends on: variables, layout)
├── _forms.css (depends on: variables)
├── _buttons.css (depends on: variables)
├── _chat.css (depends on: variables)
├── _music.css (depends on: variables)
├── _tracks-favorites.css (depends on: variables)
├── _audio-player.css (depends on: variables)
├── _playlists.css (depends on: variables)
├── _upload.css (depends on: variables)
├── console-launcher.css (depends on: variables)
├── _login.css (depends on: variables)
├── _error.css (depends on: variables)
├── _utilities.css (depends on: variables)
└── styles.css (overrides - depends on all above)
```