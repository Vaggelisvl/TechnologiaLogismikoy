Requirements:
MongoDB image on docker .
Java Version 1.8

Steps:
Clone the repository to your local machine.
Modify the application-local.properties by changing the MongoDB properties values and provide those you set on installation of MongoDB on docker.
Edit Run/Debug Configurations by simply define the Active profile that app will use . Set the field with value :local
Run the application .
When you run the application for the first time you have to initialize the table roles in mongoDB .Simply make a POST call to endpoint http://localhost:8080/api/v1/map-services/auth/initialize .
If your application is running on different port make sure to replace it with the right one .

Swagger:
You can access the Swagger by visiting the url localhost:8080/swagger-ui/index.html#/ for more details 
