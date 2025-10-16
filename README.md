TBD

this app will connect to database host on aws with the info below:

spring.datasource.url=jdbc:postgresql://<host>:5432/ifsw-db
spring.datasource.username=ifsw-user
spring.datasource.password=<password>

Access the Swagger UI at http://localhost:8080/swagger-ui.html

View the OpenAPI documentation at http://localhost:8080/v3/api-docs


Publish Vehicle API
GET /api/v1/vehicles/vehicle-id

Dockerize
build image:
docker build -t insurance-app .

run image:
docker run -d -p 8080:8080 --name insurance-container insurance-app

view log:
docker logs -f insurance-container

stop container:
docker stop insurance-container

start container:
docker start insurance-container


You can customize the application using environment variables:
docker run -p 8080:8080 \
-e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db \
-e SPRING_DATASOURCE_USERNAME=your-username \
-e SPRING_DATASOURCE_PASSWORD=your-password \
insurance-app
