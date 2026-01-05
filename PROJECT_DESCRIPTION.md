# PushupBruh - Simple Daily Pushup Tracker

## 🎯 The Big Idea

**PushupBruh is a beautifully simple app built around one core concept: tracking your daily pushups in sets of 20.**

Instead of complex logging and counting, we believe in the power of **consistent, manageable sets**. Just tap the +20 button and you're done for the day. No math, no counting, no excuses - just pure progress tracking.

## 🚀 Core Philosophy

### The Power of 20
- **Achievable**: 20 pushups is a challenging but doable daily goal
- **Measurable**: Clear progress tracking without complex metrics
- **Consistent**: Builds habit through repetition
- **Simple**: One tap to log your entire day's progress

### Less is More
- **No Overthinking**: No complex workout logging
- **No Distractions**: Focus on the core habit
- **No Barriers**: Lowest friction possible for daily tracking
- **No Guilt**: Miss a day? Just start fresh tomorrow

## 📱 Key Features

### 🏠 Home Screen
- **Today's Count**: Big, bold display of current pushups
- **+20 Button**: One-tap logging with haptic feedback
- **Cooldown Protection**: 1-second cooldown prevents accidental double-taps
- **Calendar Access**: Quick navigation to monthly view

### 📅 Calendar View
- **Monthly Overview**: See all your pushup data at a glance
- **Dark Theme Support**: Beautiful theme-aware colors
- **Future Date Protection**: Can't accidentally log future pushups
- **Visual Feedback**: Clear distinction between past and future dates

### 📊 Data Visualization
- **Orange Histogram**: Clean line graph showing your progress
- **Past Data Only**: Graph stops at today, no future visualization
- **Theme Integration**: Colors adapt to light/dark mode
- **Clean Design**: No clutter, just your progress trend

### 📝 Day Details
- **Individual Day View**: Tap any calendar date for details
- **Edit Capability**: Modify past entries when needed
- **Navigation Controls**: Easy movement between days
- **Future Protection**: Edit button disabled for future dates

## 🎨 Design System

### Color Palette
- **Primary Orange**: `#FF8C42` - Your pushup progress
- **Accent Blue**: `#3498DB` - Interactive elements  
- **Text Primary**: Theme-aware (dark/light)
- **Text Secondary**: `#95A5A6` - Subtle information

### Typography
- **Bold Numbers**: Large, readable pushup counts
- **Clean Sans**: Modern, friendly font family
- **Hierarchical**: Clear visual hierarchy
- **Accessible**: High contrast ratios

### Layout Principles
- **Card-Based**: Organized, scannable content
- **Generous Spacing**: Room to breathe and tap
- **Consistent Padding**: Unified spacing system
- **Touch-Friendly**: Minimum 48dp touch targets

## 🌙 Theme System

### Light Theme
- **Clean Whites**: Bright, fresh appearance
- **Subtle Grays**: Professional, easy on eyes
- **Orange Accents**: Consistent brand colors
- **High Contrast**: Excellent readability

### Dark Theme
- **Deep Blacks**: True dark mode experience
- **White Text**: Crisp, readable text
- **Orange Highlights**: Consistent accent colors
- **Reduced Eye Strain**: Comfortable for night use

### Theme Implementation
- **Automatic**: Follows system theme settings
- **Runtime Switching**: No app restart required
- **Component-Level**: Every view respects theme
- **Future-Proof**: Easy to extend color system

## 🔒 Data Protection

### Future Date Prevention
- **Calendar Grid**: Future cells unclickable and semi-transparent
- **Navigation**: Cannot scroll to future dates
- **Editing**: Edit button disabled for future dates
- **Graph**: No visualization of future data

### Data Integrity
- **Local Storage**: All data stored on device
- **No Cloud Dependencies**: Works offline completely
- **Backup System**: Automatic local backup creation
- **Restore Capability**: Recover from backup if needed

## 📱 User Experience

### Interaction Patterns
- **One-Handed**: All controls reachable with thumb
- **Quick Actions**: Most common tasks one tap away
- **Visual Feedback**: Button states, loading indicators
- **Haptic Response**: Vibration confirms actions

### Error Prevention
- **Cooldown Timer**: Prevents double-logging
- **Future Blocking**: Cannot accidentally log future dates
- **Graceful Degradation**: App works without vibration
- **Input Validation**: Prevents invalid data entry

## 🛠️ Technical Architecture

### Component Structure
```
MainActivity.kt          - Home screen with +20 button
CalendarActivity.kt       - Monthly calendar view
DayDetailActivity.kt      - Individual day editing
HistogramView.kt          - Custom graph component
DataManager.kt            - Data persistence layer
```

### Key Technologies
- **Kotlin**: Modern, safe Android development
- **Material Design**: Consistent Android patterns
- **Custom Views**: Tailored visualization components
- **Theme System**: Dynamic color management
- **Robolectric**: Comprehensive unit testing

### Data Model
- **Date Keys**: `YYYY-MM-DD` format for consistency
- **Simple Storage**: SharedPreferences for reliability
- **JSON Backup**: Human-readable backup format
- **Auto-Restore**: Seamless data recovery

## 🎯 Future Development Guidelines

### Component Replacement Rules
When replacing any component, maintain these core principles:

#### Calendar Component
- **Grid Layout**: 7-column weekly structure
- **Theme Colors**: Use `R.color.accent_orange` for data
- **Future Protection**: Disable future dates visually and functionally
- **Cell Styling**: Theme-aware backgrounds and text colors

#### Graph/Chart Component  
- **Orange Line**: Use `R.color.accent_orange` for data lines
- **Past Data Only**: Never visualize future dates
- **Theme Integration**: Respect light/dark theme colors
- **Clean Design**: Minimal axes, focus on data trend

#### Button/Interaction Component
- **Haptic Feedback**: 100ms vibration on successful actions
- **Cooldown Pattern**: 1-second disable for critical actions
- **Visual States**: Alpha changes for disabled states
- **Error Handling**: Graceful fallback when features unavailable

#### Data Management Component
- **Date Format**: Always use `YYYY-MM-DD` keys
- **Local First**: Store everything locally first
- **Backup Strategy**: JSON format for human readability
- **Theme Colors**: Use defined color resources, never hardcode

### Styling Reference
- **Primary Action**: `R.color.accent_orange` (#FF8C42)
- **Secondary Action**: `R.color.accent_blue` (#3498DB)  
- **Text Primary**: `R.color.text_primary` (theme-aware)
- **Text Secondary**: `R.color.text_secondary` (#95A5A6)
- **Background**: `R.color.background_surface` (theme-aware)
- **Calendar Grid**: `R.color.background_calendar_grid` (theme-aware)

### Interaction Standards
- **Critical Actions**: Implement 1-second cooldown
- **Future Dates**: Always disable editing and navigation
- **Visual Feedback**: Use alpha 0.5f for disabled states
- **Error Recovery**: Never crash, always fallback gracefully

## 🚀 Getting Started

### Development Setup
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on device/emulator

### Design System Usage
- Always use theme color resources
- Implement future date protection
- Follow the 20-pushup philosophy
- Maintain simple, clean interfaces

### Testing Strategy
- Unit tests for core logic
- UI tests for user interactions
- Theme testing for both light/dark
- Device testing for vibration handling

---

**PushupBruh: Making daily fitness tracking beautifully simple. One set of 20 pushups at a time.** 💪🎯
