# UrbanScan Mobile

UrbanScan Mobile is an Android application designed to improve urban infrastructure and the environment by leveraging advanced AI-driven live detection technology. Using Tensorflow lite, the app provides real-time detection of potholes and waste in urban environments.

## Features

### Live Detection for Potholes and Waste
- **Real-Time Detection Using Camera**: Detects potholes and waste through the device's camera in real-time, offering immediate insights into urban conditions.

## Technology
UrbanScan Mobile integrates the Tensorflow Lite model. We have customized the TFlite model to suit real-time detection needs for both potholes and waste on Android devices.

## Characteristics
- **User Interface**: Simplified and intuitive mobile interface optimized for real-time analysis using the device camera.
- **Visualization and Saving**: Ability to capture of detected potholes or waste in real-time.
- **Theme Modes**: Supports both "Light" and "Dark" themes, enabling users to personalize the app experience.

For training and evaluation, UrbanScan Mobile utilizes the following datasets:
- **[TACO Dataset](https://github.com/pedropro/TACO)**: A large-scale dataset for trash detection.
- **[Pothole Detection Computer Vision Project](https://universe.roboflow.com/imacs-pothole-detection-wo8mu/pothole-detection-irkz9)**: A dataset designed to detect potholes in various road conditions.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/ValiDragan7/UrbanScanMobile.git
    ```

2. Install dependencies:
Ensure Android Studio is installed and your environment is set up for Android development.
  ```bash
  ./gradlew build
  ```

3. Install the APK on your device: Build and install the APK file to your Android device via Android Studio or by exporting it.

## Usage
Once installed, launch the UrbanScan Mobile app on your Android device to begin real-time detection of potholes and waste using the device camera.
