# Body Tracking App

This is a personal body monitoring Android application based on the **U.S. Navy Seal body fat estimation method**. The app allows users to log daily measurements and track changes in their physical condition over time.

## Description

The app focuses on providing an accurate estimate of body fat percentage using a widely recognized method used by the **U.S. Navy Seals**. This method calculates body fat percentage using specific body measurements rather than relying on expensive or complex tools.

The Navy method typically uses the following measurements:
- **Neck circumference**
- **Waist circumference**
- **Height**
- For women, additional **hip circumference** may be used

These values are plugged into a formula that estimates body fat percentage, making it practical and accessible for everyday tracking.

With this app, users can:
- Enter their daily measurements
- Record their weight
- Check their body fat
- Select the date using a user-friendly interface
- View the average weekly weight (Sunday to Friday)

All data is stored locally using a reliable persistence layer built with Jetpack components.

## Built With

- **Kotlin**
- **Android Studio**
- **SQLite**
- **Jetpack (Room, ViewModel, LiveData, etc.)**
- **MPAndroidChart**
- **Gradle**

## Installation

To get started with the project, clone this git on the Android Studio and start the app:

```bash
git clone https://github.com/PedroA27/Body-Tracking.git
