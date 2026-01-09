# 💬 ChatUp

A simple, real-time messaging application built with Kotlin and Firebase for Android. ChatUp allows users to create conversations, send messages and interact through a clean user interface. 

---

## 🚀 Features

- 🔐 User authentication with email/password and Google-login  
- 📜 View list of clickable conversations  
- 👥 User list  
- 💬 Send and receive real-time messages  
- 📩 User presence indicators (seen, delivered)  
- 🍔 Navigation drawer with user info and logout  
- 🔍 Search users and start new chats  

---

## 📱 Screenshots
![Login screen](login.png)
![Startmenu](startmenu.png)
![Chat](chat.png)
![Navigation](navigation.png)

---

## 💻 Technologies Used

- **Language:** Kotlin  
- **Architecture:** MVVM (Model-View-ViewModel)  
- **Backend:** Firebase  
  - Firebase Authentication  
  - Cloud Firestore  
- **UI:** ViewBinding, RecyclerView, Fragments  
- **Android Jetpack Components**  
- **Coroutine** for async operations  
- **Material Design Components**  

---

## 📂 Project Structure

com.example.chatup
│
├── activities
│ ├── StartMenuActivity.kt
│ ├── ChatActivity.kt
│ ├── MainActivity.kt
│ ├── LoginActivity.kt
│ ├── RegisterActivity.kt
│ └── SettingsActivity.kt
│
├── fragments
│ ├── ConversationListFragment.kt
│ ├── UsersFragment.kt
│ └── ProfileFragment.kt
│
├── adapters
│ ├── ConversationListAdapter.kt
│ ├── UserAdapter.kt
│ └── ChatRecyclerViewAdapter.kt
│
├── viewmodel
│ ├── AuthViewModel.kt
│ ├── ChatViewModel.kt
│ ├── ConversationListViewModel.kt
│ └── UsersViewModel.kt
│
├── data
│ ├── User.kt
│ ├── ChatMessage.kt
│ └── ConversationList.kt
│
├── repository
│ └── AuthRepository.kt
│
├── utils
│ ├── TimeUtils.kt
│ └── Constants.kt
│
└── firebase
└── FirebaseManager.kt

---

## 🛠️ Usage

1. Register a new account or log in  
2. Navigate through conversations via the navigation drawer  
3. Tap a conversation to open chat screen  
4. Send messages and see live updates  
5. Use search to find users and start a new conversation  
6. Manage profile and settings from the menu  

---
