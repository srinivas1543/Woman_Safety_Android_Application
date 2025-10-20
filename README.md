# 👩‍🦰 Woman Safety Android Application

The **Woman Safety App** is an Android-based emergency alert system designed to enhance women’s safety. With just one click, users can send their **current location**, **SOS message**, and even a **recorded voice clip** to their **trusted contacts** in case of danger.

---

## 🚀 Features

- 📍 **Live Location Sharing** – Sends the user’s current GPS location via SMS to emergency contacts.  
- 📞 **One-Tap SOS Alert** – Instantly sends an SOS message and makes an emergency call.  
- 🎙️ **Voice Recording** – Records a short voice clip during emergencies and uploads it to **Firebase Storage**.  
- 🔗 **Share Recording Link** – Automatically sends the downloadable link of the recorded audio to contacts via SMS.  
- 🧑‍🤝‍🧑 **Emergency Contacts Management** – Add, view, and delete emergency contacts from a local database.  
- 🔋 **Battery Level Info** – Sends current battery status in the SOS message.  
- 🧭 **Simple UI** – User-friendly interface built using Android Studio and XML layouts.  

---

## 🛠️ Tech Stack

- **Language:** Java  
- **Framework:** Android SDK  
- **Database:** SQLite (for storing contacts locally)  
- **Cloud Storage:** Firebase Storage  
- **APIs:** Google Location Services API  
- **IDE:** Android Studio  

---

## ⚙️ How It Works

1. The user registers emergency contacts within the app.  
2. On pressing the **SOS button**, the app:
   - Gets the current location.  
   - Sends an SOS message and location link via SMS.  
   - Starts recording a short voice message and uploads it to Firebase.  
   - Sends the recording URL to the same contacts.  
   - Optionally, initiates an emergency phone call.  

---

## 🧩 Project Structure

