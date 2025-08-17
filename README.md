# ReCalc 🧮  
*A simple yet powerful calculator app built with Kotlin and Android.*

![Platform](https://img.shields.io/badge/platform-Android-green.svg) 
![Language](https://img.shields.io/badge/language-Kotlin-blue.svg) 
![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)

---

## ✨ Features
- 🌓 **Dark & Light Mode**  
  - Follows **system theme** automatically.  
  - Can be toggled manually using the **Dark Mode button**.  

- 🔢 **Calculator Functions**  
  - Supports `+`, `-`, `×`, `÷`, `%` (modulo).  
  - Handles operator precedence correctly.  
  - Prevents malformed inputs (no double operators, etc.).  

- 🎨 **Custom UI**  
  - Custom drawables for buttons & backgrounds.  
  - Smooth button press animations.  
  - Adaptive app icon.  

- 📱 **Responsive Design**  
  - Dynamic text resizing in the display area.  
  - Works across different screen sizes.  

---

## 📸 Screenshots
| Light Mode | Dark Mode |
|------------|-----------|
| <img src="Screenshot from 2025-08-17 12-25-51.png" width="250"/> | <img src="Screenshot from 2025-08-17 12-24-30.png" width="250"/> |

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/ReCalc.git

```

2. Open in Android Studio

File → Open → select the project folder.

3. Build & Run

Connect an emulator or Android device.

Press ▶️ Run to install the app.


📂 Project Structure
```bash
app/
 ├─ src/
 │   ├─ main/
 │   │   ├─ java/com/example/recalc/   # Kotlin source files
 │   │   ├─ res/                       # Resources (layouts, drawables, colors, themes)
 │   │   └─ AndroidManifest.xml
 │   └─ test/                          # Unit tests
 ├─ build.gradle.kts
 └─ README.md
```

⚙️ Tech Stack

Language: Kotlin

UI: XML layouts, custom drawables

Architecture: Single-Activity

Tools: Android Studio, Gradle

🛠️ Future Improvements

Landscape Orientation

Calculation History
