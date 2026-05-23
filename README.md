# LLM-Enhanced Learning Assistant App

## Project Overview

The LLM-Enhanced Learning Assistant App is an Android educational application developed for SIT708 Task 10.1D. The app is designed to help students practise beginner computing concepts through interactive quiz questions, learning hints, lesson summaries, result explanations, and progress tracking.

This version extends the original Task 6.1D application by adding profile management, quiz history, sharing functionality, and a simulated purchasing workflow.

## Features

### Account Setup and Login

The app allows users to create an account by entering a username, email, password, confirm password, and phone number. Basic validation is included to check required fields and matching email and password confirmation fields.

The login screen validates the saved username and password before allowing access to the main app.

### Interest Selection

After account creation, users can select learning interests such as:

- Algorithms
- Data Structures
- Databases
- Cloud Computing
- Artificial Intelligence
- Testing
- Cyber Security

These selected interests support a more personalised learning experience.

### Multi-Question Learning Test

The app includes a generated learning test with multiple beginner-level computing questions. The quiz currently covers concepts such as stacks, queues, and algorithms.

Each question uses multiple-choice radio buttons, and users can move through the quiz using a simple question navigation flow.

### LLM-Inspired Learning Support

The app includes simulated LLM-inspired learning utilities, including:

- AI-style hints
- Lesson summaries
- Result explanations

Hints are designed to guide the learner without directly giving away the answer. Lesson summaries explain the topic in beginner-friendly language, and result explanations help the user understand their performance after completing the test.

### Results Screen

After completing the test, the app displays:

- Final score
- Selected answers
- Correct answers
- Correct or incorrect result for each question
- AI-inspired result explanation

### History Screen

The History screen stores and displays previous quiz attempts using SharedPreferences. It shows the questions attempted, selected answers, correct answers, and final score.

This allows users to review their learning progress across multiple attempts.

### Profile Screen

The Profile screen displays user details and learning progress information. It also provides access to the History and Upgrade Account screens.

### Sharing Feature

The app uses Android's native share intent to allow users to share learning progress or profile-related information through other apps installed on the device.

### Upgrade Account and Purchasing Feature

The Upgrade Account screen includes three simulated learning plans:

- Starter
- Intermediate
- Advanced

When a user selects a plan, the app displays different payment method options, including:

- Google Pay
- Credit/Debit Card
- PayPal

The purchasing workflow is simulated using confirmation messages. No real transaction or payment processing is performed.

## Technologies Used

- Kotlin
- Android Studio
- Jetpack Compose
- XML layouts
- SharedPreferences
- Android Intent system

## Modern Android Development Practices

The app uses Kotlin for Android development and Jetpack Compose for the main learning flow. Reusable composable components are used for cards, buttons, input fields, and response sections to maintain consistency across the app.

State-based navigation is used for the main app flow, including login, setup, interests, home, quiz, and results screens. SharedPreferences is used for lightweight local data storage, including account details and quiz history.

## Project Structure

Main files used in the application include:

- `MainActivity.kt` - Main Jetpack Compose learning flow, login, setup, quiz, and results
- `ProfileActivity.kt` - Profile screen and sharing feature
- `HistoryActivity.kt` - Displays saved quiz history
- `UpgradeActivity.kt` - Upgrade plans and simulated payment workflow
- `AndroidManifest.xml` - Activity declarations and app configuration
- `activity_profile.xml` - Profile screen layout
- `activity_history.xml` - History screen layout
- `activity_upgrade.xml` - Upgrade account and payment method layout

## How to Run the App

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Connect an Android device or start an emulator.
5. Click Run to launch the application.
6. Create a new account and proceed through the app features.

## Demonstration Flow

The app demonstration should show:

1. Account creation
2. Login validation
3. Interest selection
4. Starting the generated quiz
5. Generating hints and lesson summaries
6. Completing the quiz
7. Viewing results and explanations
8. Opening the History screen
9. Opening the Profile screen
10. Using the Share feature
11. Opening the Upgrade Account screen
12. Selecting a plan and payment method

## Notes

This application is an academic prototype. The LLM responses and payment workflow are simulated for demonstration purposes. No real AI API or real payment gateway is connected in this version.
