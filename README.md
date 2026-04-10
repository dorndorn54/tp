# ItemTasker

**ItemTasker** is a Command-Line Interface (CLI) warehouse inventory and ticketing system. Designed for Inventory Managers, it provides an agile, local application to manage Stock Keeping Units (SKUs) across a physical warehouse grid and track operational tasks associated with each item.

## Setting up in Intellij

Prerequisites: **JDK 17** (use the exact version), and update Intellij to the most recent version.

1. **Ensure Intellij JDK 17 is defined as an SDK**, as described [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). This step is not needed if you have used JDK 17 in a previous IntelliJ project.
2. **Import the project _as a Gradle project_**, as described [here](https://se-education.org/guides/tutorials/intellijImportGradleProject.html).

**Warning:** Keep the `src/main/java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Build automation using Gradle

* This project uses Gradle for build automation and dependency management. It includes a basic build script as well (i.e. the `build.gradle` file).
* If you are new to Gradle, refer to the [Gradle Tutorial at se-education.org/guides](https://se-education.org/guides/tutorials/gradle.html).

## Testing

### I/O redirection tests

* To run _I/O redirection_ tests (aka _Text UI tests_), navigate to the `text-ui-test` folder and run the `runtest(.bat/.sh)` script.

### JUnit tests

* This project includes a comprehensive suite of JUnit tests. To run them, you can use Intellij's built-in test runner or execute `./gradlew test` in your terminal.
* If you are new to JUnit, refer to the [JUnit Tutorial at se-education.org/guides](https://se-education.org/guides/tutorials/junit.html).

## Checkstyle

* A sample CheckStyle rule configuration is provided in this project to ensure code quality and consistency.
* If you are new to Checkstyle, refer to the [Checkstyle Tutorial at se-education.org/guides](https://se-education.org/guides/tutorials/checkstyle.html).

## CI using GitHub Actions

The project uses [GitHub Actions](https://github.com/features/actions) for Continuous Integration (CI). When you push a commit to this repo or open a Pull Request against it, GitHub Actions will run automatically to build the project, run all tests, and verify Checkstyle compliance.

## Documentation

The `/docs` folder contains the project documentation, including the **User Guide** and **Developer Guide**.

Steps for publishing documentation to the public via GitHub Pages:
1. Go to your fork (or the team fork) on GitHub.
2. Click on the `Settings` tab.
3. Scroll down to the `Pages` section on the left sidebar.
4. Under `Build and deployment`, set the `Source` to `Deploy from a branch`.
5. Select the `master` (or `main`) branch and the `/docs` folder, then click `Save`.
6. Optionally, use the `Choose a theme` button to apply a visual theme to your documentation.