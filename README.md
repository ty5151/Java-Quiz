# JAVA Quiz Platform

### 1. The Core Idea

For this project, I built a standalone desktop application using Java Swing that lets users create, manage, and take multiple-choice quizzes. Instead of building a basic console app, I wanted to create a fully graphical interface that feels like a real testing environment. It handles everything from creating new quizzes to timing the user during a test and giving them immediate visual feedback on their performance.

### 2. How the Data is Stored (File Handling)

Since the goal was to keep the application lightweight and entirely offline, I deliberately avoided using a heavy SQL database. Instead, I used Java I/O to implement flat-file storage.

All the quiz data is written to and read from a single local text file (`saved_quizzes.txt`). The biggest challenge with saving complex data like multiple-choice questions in a plain text file is parsing it back out correctly. If I used a comma to separate the data, the program would crash the moment a user typed a comma inside their actual question. To solve this, I designed a custom string delimiter (`;;;`) for the file writer. This ensures that no matter what punctuation the user types in their questions or options, the `BufferedReader` can always parse the objects perfectly when the application starts up.

### 3. The Quiz Mechanics and Timer

When a user takes a quiz, the application doesn't just display the questions; it simulates a real exam environment.

* **The Timer:** I implemented a live digital countdown timer. Because UI updates can't block the main thread, the timer runs concurrently, updating the screen every second. If the timer hits zero, the application automatically interrupts the user and submits whatever answers they have currently selected.
* **Post-Quiz Review:** Once a quiz is submitted, the system doesn't just throw a final score at the user. It transitions to a review screen that reconstructs the quiz. It checks the user's answers against the `Quiz` object and uses conditional formatting—highlighting the correct options in green and the user’s incorrect choices in red so they can immediately see their mistakes.
* **Session Tracking:** While the app runs, it keeps a running tally in memory of the user's session, displaying a dashboard with their total attempts, highest score, and visual progress bars of their recent attempts.

### 4. Architecture and OOP Principles

The entire codebase was structured to strictly follow Object-Oriented Programming principles, specifically **Separation of Concerns** and **Encapsulation**. I split the project into three distinct layers so the code didn't turn into a messy, monolithic block:

1. **The Data Models:** I created classes like `Question`, `Quiz`, and `QuizResult`. These exist purely to encapsulate the data. They have private fields and use getters/setters. They have absolutely no idea that a graphical interface exists.
2. **The Business Logic (`QuizManager`):** This is the engine of the application. It manages the list of loaded quizzes and handles all the file reading and writing. By keeping this separate, if I ever wanted to switch the storage method from a text file to a database later, I would only have to touch this one class.
3. **The GUI (`QuizPlatform`):** This layer strictly handles the `javax.swing` components—the panels, buttons, layout managers, and event listeners. It never accesses the text file directly; instead, it asks the `QuizManager` for the data and simply paints it on the screen.

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
