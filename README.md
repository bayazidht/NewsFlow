# NewsFlow: Latest News 📰

![GitHub last commit](https://img.shields.io/github/last-commit/bayazidht/NewsFlow)
![GitHub language count](https://img.shields.io/github/languages/count/bayazidht/NewsFlow)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

## 🌟 Overview

**News Flow** is a modern, personalized news aggregator app built with MVVM architecture. It fetches real-time updates via RSS feeds, featuring offline bookmarking, smart notifications, and category-based filtering for a seamless reading experience.

## 🖼️ App Screenshots: A Quick Tour

Take a look at the core functionalities and interface of NewsFlow:

| Light Theme | Dark Theme | 
| :---: | :---: |
|<img src="https://github.com/user-attachments/assets/db5db10b-e7bd-4612-9147-968857a55f1a"/> | <img src="https://github.com/user-attachments/assets/262bc007-142a-4778-9b5a-77ddb9feeef5"/> | 


## ✨ Key Features

* **Real-time Tracking:** Instantly record and monitor income and expenses.
* **Layered Architecture:** Built using clean, modern architecture principles for maintainability.
* **Cloud Integration:** Real-time data synchronization across devices (using Firebase/Firestore).
* **Offline Caching:** Seamless operation even without an internet connection.
* **Graphical Summary:** Visual representation (Bar Charts, Pie Charts) of monthly and category-wise spending.

## 🛠️ Technology Stack

* **Language:** [Kotlin](https://kotlinlang.org/) - Modern, concise, and safe programming language.
* **Architecture:** **MVVM (Model-View-ViewModel)** with Clean Architecture principles.
* **UI Framework:** [Material Design 3](https://m3.material.io/) - For a modern and adaptive user interface.
* **Local Database:** [Room Database](https://developer.android.com/training/data-storage/room) - For offline caching and bookmarking news.
* **Background Processing:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - For periodic news fetching and background notifications.
* **Image Loading:** [Glide](https://github.com/bumptech/glide) - For smooth and efficient image loading and caching.
* **Networking:** `HttpURLConnection` & Custom XML Pull Parser for RSS Feed processing.
* **Concurrency:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - For asynchronous tasks and non-blocking IO operations.

## 🤝 Credits & Contributors

* **Syed Bayazid Hossain** - *Main Developer & Architect* - (https://github.com/bayazidht)
* **Open Source Libraries:**
    * [Glide](https://github.com/bumptech/glide) for image processing.
    * [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for background tasks.
    * [Material Components](https://github.com/material-components/material-components-android) for UI elements.
* **News Sources:** RSS Feeds provided by various global news publishers (Reuters, The Verge, CNBC, etc.).

## 📂 Project Structure

```text
com.bayazidht.newsflow
├── data
│   ├── local        # Room Database, DAOs, and Offline Logic
│   ├── remote       # RSS Parser and News Source configurations
│   ├── model        # Data classes (NewsItem, NotificationItem)
│   └── repository   # Data management layer
├── ui
│   ├── activity     # Main and Detail screens
│   ├── fragment     # Home, Trending, and Bookmark fragments
│   └── adapter      # RecyclerView adapters for news lists
└── worker           # WorkManager classes for background tasks 

```


# 🚀 Getting Started

Follow these steps to set up and run the **NewsFlow** application on your local development environment.

### 📋 Prerequisites

Before you begin, ensure you have the following installed:

-   **Android Studio:** Jellyfish | 2023.3.1 or newer.
    
-   **JDK:** Java Development Kit 17 or higher.
    
-   **Gradle:** Version 8.0 or higher (Managed via Gradle Wrapper).
    
-   **Android Device/Emulator:** Running API Level 24 (Android 7.0) or higher.
    

----------

### ⚙️ Installation and Setup

1.  **Clone the Repository** Open your terminal or command prompt and run:
    
    Bash
    
    ```
    git clone https://github.com/bayazidht/NewsFlow.git
    cd NewsFlow
    
    ```
    
2.  **Open in Android Studio**
    
    -   Launch Android Studio.
        
    -   Select **File > Open...** and navigate to the cloned directory.
        
    -   Click **OK** and wait for the IDE to finish the Gradle Sync process.
        
3.  **Check Dependencies** Ensure all dependencies are downloaded correctly. If you see any sync errors:
    
    -   Go to **File > Invalidate Caches / Restart...**
        
    -   Select **Invalidate and Restart**.
        
4.  **Prepare Your Device**
    
    -   **Physical Device:** Enable **USB Debugging** in Developer Options.
        
    -   **Emulator:** Create an AVD (Android Virtual Device) via **Device Manager** in Android Studio.
        
5.  **Build and Run**
    
    -   Select your device from the target selector in the top toolbar.
        
    -   Click the **Run** button (Green Triangle icon) or press `Shift + F10`.
        
    -   **Alternatively, run via CLI:**
        
    
    Bash
    
    ```
    ./gradlew installDebug
    
    ```
    

----------

### 🛠️ Troubleshooting Tips

-   **Sync Failed:** Ensure your internet connection is active as Gradle needs to download libraries like **Glide**, **Room**, and **Material components**.
    
-   **Build Errors:** If the build fails after a sync, try **Build > Clean Project** followed by **Build > Rebuild Project**.
    
-   **Missing Images:** Since the app uses RSS feeds, ensure your device has internet access to fetch live news and thumbnails.
