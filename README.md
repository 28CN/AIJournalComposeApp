1. Introduction

The AI Journal Companion is a simple Android application that helps users reflect on their daily experiences.
Users can write a short journal entry, and the app will return the detected emotion and a short advice.

Main features:

Write journal entries and analyze with AI

Save and view past entries

Sort and search entries by emotion

Drag-and-drop to delete entries

View a Pie Chart of emotion distribution

Access a Help page with basic instructions

2. Installation
Option A: Run from Android Studio

Clone the GitHub repository:

git clone https://github.com/28CN/AIJournalComposeApp


Open the project in Android Studio.

Connect an Android device or use the emulator.

Run the app with Run ▶ button.

Option B: Install APK (if provided)

Copy the AIJournal.apk file to your device.

Enable Install from unknown sources in settings.

Tap the APK to install.

3. Using the App
3.1 Write Screen

Enter your journal text in the input box.

Press Analyze.

The app will display:

Emotion detected

Advice generated

The entry is saved automatically to the History page.


3.2 History Screen

Shows a list of all saved entries with date/time.

Use the Sort menu to select Bubble, Insertion, or Selection sort.

Use the Search menu to find entries by emotion (Binary Tree, HashMap, or Doubly Linked List).

Drag an entry to the delete target area to remove it.


3.3 Chart Screen

Displays a Pie Chart showing the distribution of emotions across entries.

The chart updates when new entries are added or deleted.


3.4 Help Screen

Accessed through the bottom menu.

Shows the local help.html page with FAQs and guidance.


4. Tips & Notes

Use short and clear sentences in journal entries for best analysis results.

Internet connection is not required once the Ollama model is running locally.

If the backend (Ktor + Ollama) is not running, the Analyze function will show an error.
