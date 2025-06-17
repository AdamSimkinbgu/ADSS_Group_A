# ADSS Group A - Supermarket Management System

This is a Java-based Supermarket Management System that handles employee management, transport management, site management, and inventory management.

## Group Members
* Duo D
   * Rotem Guetta     207878174 Guettar@post.bgu.ac.il
   * Gabriel Rozental 211936059 rozengab@post.BGU.ac.il
* Duo B
  * Naim Elijah      317971752 elijahn@post.bgu.ac.il
  * Bar Miyara       305551012 Miyara@post.bgu.ac.il

## Prerequisites

- **Java 23** or higher
- Junit 5

## Running the Application with Maven
To run the application using Maven, follow these steps:
1. Open a terminal and navigate to the root directory of the project.
2. Run the following command to compile and run the application:

```Bash
mvn clean compile
```
3. After the compilation is successful, run the application with:

```Bash
mvn exec:java -Dexec.mainClass="Main"
```

## Building the Application as a JAR
To build the application as a JAR file, follow these steps:
1. Open a terminal and navigate to the root directory of the project.
2. Run the following command to package the application:

```Bash
mvn clean package
```

```Bash
mvn clean javafx:run
```

3. After the build is successful, the JAR file will be located in the `target` directory with the name `adss2025_v02.jar`.

## Running the Application as a JAR

In the root directory of the project, run the following command to execute the application:

```Bash
java -jar release/adss2025_v02.jar
```

## Dependencies

The project uses the following dependencies:

- **Jackson** (version 2.16.0) - For JSON processing
- **SQLite JDBC** (version 3.45.1.0) - For database connectivity
- **JUnit Jupiter 5** (version 5.8.1) - For testing
- **Maven Exec Plugin** (version 3.0.0) - For running the application
