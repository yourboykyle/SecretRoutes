

# How to Contribute

This guide is tailored to help Java developers get involved with the **SecretRoutes** mod. It covers setting up your development environment, building the project, and contributing to its growth.

---

## Development Environment Setup

### Required Tools

- **Java 8**: Minecraft 1.8.9 requires Java 8. [Download Java 8 here](https://www.java.com/download/ie_manual.jsp).
- **IntelliJ IDEA**: We recommend [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/) 

---

### Step 1: Download and Set Up IntelliJ

1. **Download IntelliJ**:
    - Use the **Community Edition** from the [JetBrains Website](https://www.jetbrains.com/idea/download/).

### Step 2: Clone the SecretRoutes Repository

1. **Fork the Repository**:
    - Visit the SecretRoutes repo at [SecretRoutes GitHub](https://github.com/yourboykyle/SecretRoutes) and fork it.
    - In your forked repository, click **Fork** > **Create fork** (leave settings as is).
2. **Open IntelliJ** and Link GitHub:
    - In IntelliJ, set up your GitHub account by going to Settings > GitHub.
    - Then go to **New > Project from Version Control > Git**, and paste your fork URL.

### Step 3: Configure IntelliJ for SecretRoutes

1. **Set up JDK 17**:
    - While Minecraft 1.8.9 uses Java 8, JDK 17 is often required to build the project with Gradle. Go to the **Gradle** tab (elephant icon on the right) and set the Gradle JVM to JDK 17 in settings.
2. **Run Configuration**:
    - IntelliJ will create a run configuration for Minecraft.
    - Open the run configuration and set the JDK to **Java 1.8** specifically for running Minecraft.

---

### Step 4: Building with Gradle

1. **Build the Project**:
    - In IntelliJ, use Gradle’s **Build** task to compile the project. 
2. **Extract Version**:
    - Gradle will automatically capture `mod_version` from the properties file, ensuring consistent versioning.

---

## Creating a Pull Request

1. **Create a New Branch**:
    - In IntelliJ, click the branch dropdown at the top, then select **New Branch**.
    - Name it to reflect the changes, like `feature-map-update` or `fix-rendering-issue`.

2. **Make Changes**:
    - Code your updates within your branch and follow the existing code style for consistency.

3. **Commit and Push**:
    - Commit with a detailed message and push to your GitHub fork.

4. **Submit a Pull Request (PR)**:
    - Go to your fork on GitHub and click to create a Pull Request.
    - Prefix the title with the change type (e.g., `Feature`, `Fix`) and include details in the PR description, like dependencies and relevant changelog items.

**Tips**:
- If your PR depends on another PR, mention it in the description (e.g., "Relies on #123").
- Use [Learn Git Branching](https://learngitbranching.js.org/) for git basics and practice.
