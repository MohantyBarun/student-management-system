# 📦 Understanding `pom.xml` in a Spring Boot Project

This document explains each important section of the `pom.xml` file used in this project.

---

## 🔹 1. Project Coordinates

```xml
<groupId>com.student</groupId>
<artifactId>StudentService</artifactId>
<version>0.0.1-SNAPSHOT</version>
```

### 📌 Explanation:

* **groupId** → Organization or package name (usually reverse domain)
* **artifactId** → Project name (JAR name)
* **version** → Version of the project

---

## 🔹 2. Model Version

```xml
<modelVersion>4.0.0</modelVersion>
```

### 📌 Explanation:

Defines the version of the Maven POM model.
`4.0.0` is the standard version used in all modern Maven projects.

---

## 🔹 3. Parent (Spring Boot)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.4</version>
</parent>
```

### 📌 Explanation:

* Provides default configurations for Spring Boot
* Manages dependency versions automatically
* Reduces boilerplate

---

## 🔹 4. Properties

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

### 📌 Explanation:

Used to define reusable values like Java version.

---

## 🔹 5. Dependencies

```xml
<dependencies>

    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Database Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

### 📌 Explanation:

* **spring-boot-starter-web** → REST APIs
* **spring-boot-starter-data-jpa** → Database operations
* **postgresql** → DB driver
* **lombok** → Reduces boilerplate code
* **starter-test** → Testing support (JUnit, Mockito)

---

## 🔹 6. Build Section

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 📌 Explanation:

* Used to build and run the project
* Enables:

    * `mvn spring-boot:run`
    * Packaging into executable JAR

---

## 🔹 7. Dependency Scope

| Scope      | Meaning                                 |
| ---------- | --------------------------------------- |
| `compile`  | Default, included everywhere            |
| `provided` | Available at compile time, not packaged |
| `runtime`  | Needed only at runtime                  |
| `test`     | Used only for testing                   |

---

## 🔹 8. SNAPSHOT vs Stable Version

* `SNAPSHOT` → Development version (unstable)
* Stable (e.g., `3.4.4`) → Recommended for production and resume projects

---

## 🎯 Summary

The `pom.xml` file:

* Manages dependencies
* Configures build lifecycle
* Defines project structure
* Enables Spring Boot auto-configuration

---

## 💬 Interview Tip

> “pom.xml is the core configuration file in Maven that manages dependencies, build lifecycle, and project metadata.”

---


