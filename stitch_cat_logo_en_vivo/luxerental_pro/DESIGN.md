---
name: LuxeRental Pro
colors:
  surface: '#fbf9f9'
  surface-dim: '#dcd9da'
  surface-bright: '#fbf9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f4'
  surface-container: '#f0edee'
  surface-container-high: '#eae7e8'
  surface-container-highest: '#e4e2e3'
  on-surface: '#1b1b1c'
  on-surface-variant: '#44474b'
  inverse-surface: '#303031'
  inverse-on-surface: '#f3f0f1'
  outline: '#74777c'
  outline-variant: '#c4c6cc'
  surface-tint: '#545f6c'
  primary: '#05101a'
  on-primary: '#ffffff'
  primary-container: '#1a2530'
  on-primary-container: '#818c9a'
  inverse-primary: '#bcc8d6'
  secondary: '#00677d'
  on-secondary: '#ffffff'
  secondary-container: '#50d9fe'
  on-secondary-container: '#005c70'
  tertiary: '#0c100f'
  on-tertiary: '#ffffff'
  tertiary-container: '#212525'
  on-tertiary-container: '#888c8b'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e4f3'
  primary-fixed-dim: '#bcc8d6'
  on-primary-fixed: '#111d27'
  on-primary-fixed-variant: '#3d4854'
  secondary-fixed: '#b3ebff'
  secondary-fixed-dim: '#4cd6fb'
  on-secondary-fixed: '#001f27'
  on-secondary-fixed-variant: '#004e5f'
  tertiary-fixed: '#e0e3e2'
  tertiary-fixed-dim: '#c4c7c6'
  on-tertiary-fixed: '#181c1c'
  on-tertiary-fixed-variant: '#434847'
  background: '#fbf9f9'
  on-background: '#1b1b1c'
  surface-variant: '#e4e2e3'
typography:
  headline-lg:
    fontFamily: Montserrat
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Montserrat
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Montserrat
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-lg:
    fontFamily: Montserrat
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Roboto
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Roboto
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Roboto
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.1px
  headline-lg-mobile:
    fontFamily: Montserrat
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  baseline: 8px
  margin-mobile: 16px
  gutter: 12px
  padding-card: 16px
  padding-list-item: 12px 16px
---

## Brand & Style
The design system is engineered for a high-end rental management environment, prioritizing professional reliability and operational clarity. The aesthetic follows **Corporate Modernism**, blending the structured principles of Material Design 3 with a refined, elegant palette.

The target audience consists of property managers and rental agents who require a serious, high-utility interface that remains legible under varying outdoor lighting conditions. The UI evokes a sense of "premium efficiency"—it is sophisticated enough for luxury assets yet robust enough for daily logistical workflows. 

Key visual characteristics include:
- **High-legibility surfaces** to reduce eye strain during field operations.
- **Intentional color hits** that guide the user toward primary actions without visual clutter.
- **Rationalized spacing** that balances information density with touch-friendly ergonomics.

## Colors
The color strategy employs a deep **Night Blue** as the foundational anchor, used for Top App Bars and structural navigation to establish authority and focus. 

**Vibrant Turquoise** serves as the interactive catalyst, reserved exclusively for Floating Action Buttons (FABs), primary Call-to-Actions (CTAs), and active states to ensure high discoverability.

The **Light Pearl Grey** background is a functional choice for outdoor readability, providing a softer alternative to stark white that reduces glare while maintaining high contrast with text.

**Status Palette:**
- **Occupado/Arrendado:** Soft Red (#E57373) – High visibility for restricted assets.
- **Disponible:** Mint Green (#81C784) – Positive reinforcement for ready inventory.
- **En Mantenimiento:** Yellow (#FFD54F) – Cautionary indicator for temporary unavailability.

## Typography
This design system utilizes a dual-typeface system to bridge the gap between elegance and utility.

**Montserrat** is used for all headings and titles. Its geometric construction provides a modern, premium feel that distinguishes the brand identity within the app.

**Roboto** is utilized for body text, lists, and form inputs. As the native Android typeface, it ensures maximum readability, particularly for dense data lists and technical rental specifications.

- **Headlines:** Bold weights with slightly tightened letter-spacing for a high-impact, editorial look.
- **Body:** Standard weights with generous line-height to ensure ease of reading during rapid scrolling.
- **Labels:** Medium weights used for metadata and status indicators to maintain hierarchy.

## Layout & Spacing
The layout adheres to an **8dp grid system** consistent with Material Design 3. 

- **Mobile Viewport:** Uses a 4-column fluid grid with 16px side margins and 12px gutters.
- **Safe Areas:** Strict adherence to the bottom navigation bar height (80px) and top status bar areas.
- **Rhythm:** Vertical rhythm is maintained through 8px increments. Component containers use 16px internal padding as the default standard for comfort.
- **Touch Targets:** All interactive elements maintain a minimum hit area of 48x48dp.

## Elevation & Depth
Depth is communicated through **Tonal Layers** and **Ambient Shadows** to create a structured hierarchy without excessive visual weight.

- **Level 0 (Background):** Light Pearl Grey (#F4F7F6), flat.
- **Level 1 (Cards/Cards):** White surface with a very soft, diffused shadow (Blur: 8px, Y: 2px, Opacity: 4% Black). This "Soft Elevation" makes rental items feel tactile and distinct from the background.
- **Level 2 (Bottom Sheets):** Higher elevation with a 12% scrim overlay on the background to focus user attention on modal tasks.
- **Floating Action Button (FAB):** Features the highest elevation with a 15% shadow density in the primary Night Blue color to make it pop against the turquoise surface.

## Shapes
The shape language is **Rounded (Level 2)** to balance corporate seriousness with modern approachability.

- **Standard Components:** Buttons, Input Fields, and Cards use a 0.5rem (8px) corner radius.
- **Large Components:** Bottom Sheets and Dialogs use a 1rem (16px) radius on top corners only.
- **Status Indicators:** Use a fully rounded (pill) shape to distinguish metadata from interactive buttons.
- **FAB:** Follows the MD3 "Squircle" or heavily rounded 1.5rem (24px) corner radius.

## Components
Consistent implementation of these core components ensures a unified user experience:

### Cards
Rental item cards feature the "Soft Elevation" profile. They should include a dedicated slot for the **Status Pill** (Top Right) and clear typography hierarchy for the asset name and price.

### Bottom Navigation Bar
Container color is White or a very light tint of Night Blue (98% lightness). Icons use Night Blue for inactive states and Vibrant Turquoise for the active state with a subtle tonal pill background.

### Floating Action Button (FAB)
The primary FAB is Vibrant Turquoise (#00B4D8). It should be used for the most frequent action (e.g., "Add New Rental").

### Sync Indicator
The Google Sheets synchronization status is represented by a **Cloud with Check** icon. 
- **Location:** Top App Bar (Right-aligned).
- **Style:** 24dp size. When active/synced, the icon should be Night Blue. If syncing is in progress, use a subtle rotation or pulse animation.

### Input Fields
Filled style with a bottom-only border (MD3 style). Background is a 5% opacity version of Night Blue to provide contrast against the Pearl Grey background.

### Bottom Sheets
Used for filtering rental lists or quick-editing asset status. They should feature a "grab handle" at the top and utilize a 16px corner radius.