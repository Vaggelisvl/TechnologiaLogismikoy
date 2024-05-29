# Project Description

This project is a Java-based web application that provides authentication and map services. It is built using Spring Boot, MongoDB, and JWT for authentication. The project is organized into multiple modules, each serving a specific purpose.

## Modules

### Authentication Module

This module handles user registration and authentication. It uses JSON Web Tokens (JWT) to secure the endpoints. The `AuthService` interface and its implementation `AuthServiceImpl` provide the main functionality for user authentication. The `AuthController` handles HTTP requests related to authentication.

### JWT Module

This module is responsible for generating and validating JWTs. It includes utility classes like `JwtUtils` and `AuthTokenFilter` for processing JWTs.

### User Details Service

This module is responsible for loading user-related data. It is used by Spring Security to handle user authentication.
1. **Locating User Data:**  The primary responsibility of the UserDetailsServiceImpl is to fetch user data based on a username. It does this in the loadUserByUsername method. In this method, it uses the UserRepository to fetch a User entity from the database.  
2. **Building UserDetails:**  Once the User entity is fetched, it is used to construct a UserDetailsImpl object (which implements the UserDetails interface from Spring Security). This object encapsulates all the details that Spring Security needs to perform authentication and authorization checks.  
3. **Handling User Not Found:**  If the UserRepository cannot find the user in the database, the loadUserByUsername method throws a UsernameNotFoundException. This exception is caught by Spring Security and handled appropriately (usually resulting in an authentication failure).  
4. **Role Assignment:**  The UserDetailsImpl object also includes the roles assigned to the user. These roles are used by Spring Security to make authorization decisions. The roles are fetched from the RoleRepository and assigned to the UserDetailsImpl object.  
5. **Password Checking:**  The UserDetailsServiceImpl does not check the user's password. This is handled separately by Spring Security's authentication provider. The UserDetailsServiceImpl only provides the user's stored password to the authentication provider for this purpose.
### Exception Handling

This module handles exceptions that may occur during the execution of the application. It includes classes like `TokenException` and `ExceptionHandlerController`.

### Data Import Module

The Data Import Module is a crucial component of the application that is specifically designed to handle the importation of data from CSV files. This module is primarily responsible for two main tasks:

1. **Importing Points of Interest (POIs):** Points of Interest are specific locations that might be of interest to users. These could be landmarks, tourist attractions, or any specific geographical locations. The module reads data from a CSV file where each row represents a unique POI. Each POI has several attributes such as title, description, latitude, longitude, keywords, and associated categories. The module parses the CSV file, validates the data, and then stores these POIs into the database for later retrieval and use.

2. **Importing Categories:** Categories represent the classification of POIs. For instance, a POI could belong to the category "Museum", "Restaurant", or "Park". The module imports categories from a separate CSV file. Each row in this file represents a unique category. The module reads the file, validates the data, and stores the categories in the database. These categories are then used to classify POIs and facilitate user search operations.

The Data Import Module is designed to handle large volumes of data and ensures that the data is correctly parsed and valid before storing it in the database. It plays a vital role in populating the application's database with initial data and can also be used to update the database with new POIs and categories as they become available.

### Map Services

Search Functionality: This is one of the core features of the Map Services module.
It allows users to search for POIs based on various criteria. The search can be performed based on the title of the POI, keywords associated with the POI, or categories the POI belongs to. The search functionality also supports filtering by distance, allowing users to find POIs within a certain radius.
### Swagger Configuration

This module is responsible for the API documentation using Swagger.

### Repositories

This module interacts with the MongoDB database. It includes repositories for entities like User, Role, PointOfInterest, and Category.

### Domain

This module includes all the domain entities used in the application.

## Usage

The application provides endpoints for user registration, user authentication, importing data, and searching points of interest. The Swagger UI provides a user-friendly interface to interact with these endpoints.


## Requirements:
MongoDB image on docker .
Java Version 11

Steps:
Clone the repository to your local machine.
Modify the application-local.properties by changing the MongoDB properties values and provide those you set on installation of MongoDB on docker.
Edit Run/Debug Configurations by simply define the Active profile that app will use . Set the field with value :local
Run the application .
When you run the application for the first time you have to initialize the table roles in mongoDB .Simply make a POST call to endpoint http://localhost:8080/api/v1/map-services/auth/initialize .
If your application is running on different port make sure to replace it with the right one .

Swagger:
You can access the Swagger by visiting the url localhost:8080/swagger-ui/index.html#/ for more details 
