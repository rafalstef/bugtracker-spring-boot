## Bugtracker REST API with Spring Boot, Spring Security, JWT and MySQL

This repository contains the source code of a Bugtracker Web Application built with Spring Boot. This application allows users to report and track bugs in a software system.

### Getting Started

To get started with the application, clone the repository to your local machine and install the required dependencies. You will also need to have Java 11 and Maven installed on your machine.

1. **Clone the application and install dependencies**

   ```bash
   git clone https://github.com/rafalstef/bugtracker-spring-boot.git
   cd bugtracker-spring-boot
   mvn install
   ```

2. **Create MySQL database**

   ```bash
   create database bugtracker
   ```

3. **Change MySQL username and password as per your MySQL installation**

   - create `"/src/main/resources/application.properties"` file.

   - change `spring.datasource.username` and `spring.datasource.password` properties as per your mysql installation

4. **Create fake SMTP server**

   - create fake SMTP server eg. using mailtrap.io
   - in `"/src/main/resources/application.properties"` change `spring.mail` properties

5. **Run the app**

   After installing the dependencies, you can run the application with the following command:

   ```bash
   mvn spring-boot:run
   ```

   The application will start running at http://localhost:8080.

### API Endpoints

This project includes Swagger documentation, which provides an interactive interface for exploring the API endpoints and their functionality. To access the Swagger documentation, start the application and navigate to http://localhost:8080/swagger-ui/index.html#/.
