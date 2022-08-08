# Script runner
REST API wrapper around the GraalJs JavaScript interpreter

### How to run
* unzip archive
* cd into `danyliuk/script-runner` directory
* make sure you are using JDK 17 and Maven 3.8.x
* build the project by running `mvn clean package`
* run the application by `mvn spring-boot:run`

You can use postman and/or curl for testing API. Also, you can test API in your browser by following address:
`http://localhost:8080/swagger-ui/index.html`

Here are some endpoints you can call:

##### Add new script
`POST http://localhost:8080/script`
`Content-Type: text/plain`

`Response: HTTP 200`
`Location header: /script/1`

You can use an optional parameter 'blocking' for blocking execution and receive script output in response body:
`http://localhost:8080/script?blocking=true`

##### Retrieve a list of previously added scripts
`GET http://localhost:8080/script`

`Response: HTTP 200`
`Response body: array of script info in JSON`

Optional parameter `status` for filtering list by script's status. Possible values: 'queued', 'executing', 'completed', 'failed', 'stopped'.
Optional parameter `sort` for sorting in descending order. Possible values 'id', 'time'

`http://localhost:8080/script?status=stopped&sort=id`

##### Get detailed script info
`GET http://localhost:8080/script/{id}`

`Response: HTTP 200`
`Response body: detailed script info in JSON`

##### Stop running script
`POST http://localhost:8080/stop/{id}`

`Response: HTTP 200`

##### Delete inactive script by id
`DELETE http://localhost:8080/script/{id}`

if script removed successfully:
`Response: HTTP 200`

if id is not present in list:
`Response: HTTP 404`

if script is still executing or queued
`Response: HTTP 400`
