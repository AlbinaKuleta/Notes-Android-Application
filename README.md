# **One Note Application**

**One Note Application** is a secure and user-friendly Android app designed to simplify note management while ensuring **robust data protection**. It offers features such as **user registration**, **email verification**, **OTP-based authentication**, **profile management**, **notifications**, and **CRUD operations for notes**. The app is tailored to meet the needs of users who value both functionality and security in managing their personal notes.

## **Features**

### **Secure User Authentication:**
- **Register** using a valid email address.
- **Login** requires:
  - **Email verification**.
  - **OTP (One Time Password)** sent to the registered email.
- **Change password** option available in case the user forgets their credentials.

### **Notifications:**
- Users receive a **notification** with the **title** and **creation date** whenever they add a note.

### **Notes Management:**
- **Create, Read, Update, and Delete (CRUD)** notes with ease.

### **User Profile:**
- Users can **view** and **update** their profile details (e.g., email, phone number).
- Users can **permanently delete** their profile and associated data (**this action is irreversible**).

## **Technologies and Frameworks Used**
- **Programming Language**: Java
- **Android Version**: Android 12 (Google Inc, System Image x86_64)
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication (with OTP and email verification)
- **Notifications**: Android Notification API
- **Development Tools**:
  - Android Studio(Ladybug//2024.2.1)
  - Gradle Build System
- **Additional Services**:
  - Firebase Cloud Messaging (for notifications)
  - Firebase Authentication (for user management)

## **Project Structure**

### **Main Directories and Files:**
- **`app/`**: Contains the main application logic, resources, and configurations.

#### **Java Files:**
- **`NotificationAdapter.java`**: Adapter class for managing notifications.
- **`NotificationData.java`**: Class for handling notification data.
- **`src/androidTest/`:**
  - **`ExampleInstrumentedTest.java`**: Instrumented test example.
- **`src/main/`:**
  - **`AnimationActivity.java`**: Manages animations in the application.
  - **`DeleteProfileActivity.java`**: Handles profile deletion functionality.
  - **`ForgotPasswordActivity.java`**: Enables password recovery.
  - **`JavaMailAPI.java`**: Handles email-related operations (e.g., OTP delivery).
  - **`LoginActivity.java`**: Manages user login.
  - **`MainActivity.java`**: The main activity of the app.
  - **`Note.java`**: Model class representing a note.
  - **`NoteAdapter.java`**: Adapter for displaying notes in a list or grid.
  - **`NotesActivity.java`**: Handles CRUD operations for notes.
  - **`NotificationsActivity.java`**: Displays user notifications.
  - **`NotificationsAdapter.java`**: Adapter for managing notifications display.
  - **`OtpVerificationActivity.java`**: Handles OTP verification during login.
  - **`ReadWriteUserDetails.java`**: Model class for reading and writing user details.
  - **`RegisterActivity.java`**: Manages user registration.
  - **`UpdateEmailActivity.java`**: Allows users to update their email.
  - **`UpdateProfileActivity.java`**: Enables users to update their profile details.
  - **`UserProfileActivity.java`**: Displays and manages the user profile.
  - **`utils/`:**
    - **`NotificationUtils.java`**: Utility class for managing notifications.
- **`src/test/`:**
  - **`ExampleUnitTest.java`**: Example unit test.

### **Gradle Files:**
- **`build.gradle`** (Module Level and Project Level): Specifies dependencies and build configurations.
- **`gradle.properties`**: Contains project-wide Gradle settings.
- **`settings.gradle`**: Configures the Gradle build system.
- **`google-services.json`**: Configuration file for integrating Firebase services.
- **`proguard-rules.pro`**: Configuration for code shrinking and obfuscation.

## **Getting Started**

### **Prerequisites**
- Android Studio installed on your computer.
- Android SDK version 31 (Android 12) or higher.
- A Firebase project set up with:
  - **Firebase Authentication**
  - **Firebase Realtime Database**
  - **Firebase Cloud Messaging** (for notifications)

### **Setup Instructions**
1. **Clone the Repository**:
   ```bash
   git clone git@github.com:AlbinaKuleta/Notes-Android-Application.git

## **Open in Android Studio**
1. **Launch Android Studio**.
2. **Open the cloned project**.

## **Sync Project with Gradle**
- Ensure all dependencies are downloaded by **syncing with Gradle files**.

## **Add Firebase Configuration**
- Replace the existing `google-services.json` file in the `app/` directory with your own Firebase configuration file.

## **Run the App**
1. **Connect** an emulator or a physical device running **Android 12** or later.
2. Click on **"Run"** in Android Studio to launch the application.

## **Usage**

### **Register**
- Enter a **valid email address** and user details.
- Verify the email to **complete the registration process**.

### **Login**
- Enter the registered **email** and **password**.
- Authenticate using the **OTP** received in your email.
- Access your **notes securely**.

### **Add Notes**
- Create notes, and a **notification** will confirm the addition with the note's **title** and **creation date**.

### **Manage Profile**
- Update user details like **email** or **phone number**.
- Delete your profile and all associated data (**this action is irreversible**).

### **Password Recovery**
- Reset your password via **email** if forgotten.

## **Contributions**
Contributions are welcome! If you have suggestions or improvements, feel free to **fork the repository**, make changes, and submit a **pull request**.

