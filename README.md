# library-app
### Demo project about an online book library
#### RunConfigs and Database directory for infrastructure and testing:
1. ./runConfigs/docker-compose.yaml
2. ./runConfigs/LibraryAppApplication.xml
3. ./data/init.sql
#### API-Doc: http://localhost:8080/swagger-ui/index.html
#### HttpCollection directory contains all REST-Clients for testing:
1. ./httpCollection/Library-App.postman_collection.json

#### Comments: 
1. There are not any Unit-Tests written, due business logic was not much complex
2. Integration-Tests written only for the Entities: Book, Category and Customer
3. Security part was mostly copied from Web in order to create a Fake JWT-Token for the authentication process
4. For resource testing don't forget to pass in Maven the ENVs
5. IntelliJ-IDEA-CE was used for development
6. Not all validation cases were considered
7. Database-Constraint Exceptions have not a readable response, but they have been handled as Internal Error Exceptions
8. Time of development and search about 16 hours