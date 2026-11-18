# Practical Quiz Platform 📝

A lightweight, offline desktop application built with Java Swing for creating, managing, and practicing custom quizzes. The project is designed with strict Object-Oriented Programming (OOP) principles, ensuring clean separation between data models, file-handling logic, and the Graphical User Interface.

## ✨ Key Features

* **Quiz Creation & Modification:** Create custom quizzes with unique names, specific time limits, and multiple-choice questions. Easily modify existing quizzes to update questions or options at any time.
* **Persistent Storage:** Quizzes are automatically saved and loaded from a local `saved_quizzes.txt` file using a safe, custom delimiter (`;;;`) to prevent text-parsing errors.
* **Practice Mode with Timer:** Take saved quizzes with a live digital countdown timer. Quizzes automatically submit when the time runs out.
* **Post-Quiz Visual Review:** Instantly review your submission with visual feedback—correct answers are highlighted in green, and incorrect choices in red.
* **Visual Performance Dashboard:** Track your session progress through a visual dashboard featuring aggregate statistics (Total Attempts, Average Score, Highest Score) and color-coded progress bars for recent attempts.

## 🛠️ Architecture & Tech Stack

* **Language:** Java (JDK 8 or higher)
* **GUI Framework:** `javax.swing` and `java.awt`
* **Storage:** Java I/O (`BufferedReader` / `BufferedWriter`) for flat-file storage.
* **Design Pattern:** The codebase is split into three main areas:
  1. **Data Models:** `Question`, `Quiz`, `QuizResult` (Encapsulation).
  2. **Business Logic:** `QuizManager` handles state and file I/O operations (Separation of Concerns).
  3. **View/GUI:** `QuizPlatform` handles dynamic Swing components and navigation.

## 🚀 How to Run

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/yourusername/quiz-platform.git](https://github.com/yourusername/quiz-platform.git)
   cd quiz-platform
<img width="852" height="738" alt="image" src="https://github.com/user-attachments/assets/0fc5e46d-6460-4567-9ea2-c33ee029416f" />
<img width="852" height="735" alt="image" src="https://github.com/user-attachments/assets/9b817af9-a4b6-4113-a859-e0874f60b4bb" />
<img width="855" height="732" alt="image" src="https://github.com/user-attachments/assets/fd5c13ed-06c6-4ca6-abf4-621148d16d4d" />
<img width="852" height="727" alt="image" src="https://github.com/user-attachments/assets/68f4428c-b334-4dd9-9aa5-073992f1c8ca" />
<img width="853" height="736" alt="image" src="https://github.com/user-attachments/assets/4e7f46ba-1fdd-48dd-8e9b-3221b5bbbf01" />
<img width="861" height="731" alt="image" src="https://github.com/user-attachments/assets/ed834043-9d19-4097-9bdb-2fe9e461b295" />
<img width="852" height="737" alt="image" src="https://github.com/user-attachments/assets/b7e8f7f0-9721-47df-bcc4-2b3e33a655e6" />
