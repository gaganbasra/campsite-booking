# campsite-booking

A REST API service that will manage the campsite reservations.

### Business Requirement
An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems perfect and it was
decided to open it up for the general public to experience the pristine uncharted territory.
The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it
was decided to come up with an online web application to manage the reservations. You are responsible for design and development of a REST
API service that will manage the campsite reservations.
#### Booking Constraints
* The campsite will be free for all.
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
* Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM
#### System Requirements
* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite
along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
* The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
modification/cancellation of an existing reservation
* Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
* Provide appropriate error messages to the caller to indicate the error cases.
* In general, the system should be able to handle large volume of requests for getting the campsite availability.
* There are no restrictions on how reservations are stored as as long as system constraints are not violated.

### URL
* URL to access Campsite Booking service: **http://localhost:8183/campsite/booking/**

### API Docs
* Swagger UI **http://localhost:8183/swagger-ui.html#/booking-controller**

#### With IntelliJ or other IDE
```bash
$ git clone https://github.com/gaganbasra/campsite-booking.git
```
```text
* Import as gradle project
* Enable `annotations processors` for lombok annotations
* Run BookingApplication.java
```

#### With Gradle
```bash
$ git clone https://github.com/gaganbasra/campsite-booking.git
$ cd campsite-booking
$ ./gradlew bootRun
```
#### With Executable JAR
```bash
git clone https://github.com/gaganbasra/campsite-booking.git
cd campsite-booking
./gradlew build
java -jar build/lib/campsite-booking-0.0.1-SNAPSHOT.jar
```

### Accessing Data in H2 Database
#### H2 Console
URL to access H2 console: **http://localhost:8183/h2-console**

Table Name: **bookings**

Fill the login form as follows and click on Connect:
* Saved Settings: **Generic H2 (Embedded)**
* Setting Name: **Generic H2 (Embedded)**
* Driver class: **org.h2.Driver**
* JDBC URL: **jdbc:h2:mem:upgradedb**
* User Name: **upgrade**
* Password: **upgrade**

### Spring Actuator Endpoints
* Health: `http://localhost:8183/actuator/health`
* Env: `http://localhost:8183/actuator/env`
* Metrics: `http://localhost:8183/actuator/metrics`
* and couple more

### Postman Collection
![campsite-booking postman collection](resources/campsite-booking.postman_collection.json)

### Concurrency Test
Then execute the following command to send three concurrent HTTP POST requests:
```Bash
$ curl -o -X POST http://localhost:8183/campsite/booking -H 'Content-Type: application/json' -d '{
      "arrivalDate": "2019-12-18",
      "departureDate": "2019-12-20",
      "fullName": "Victor John1",
      "email": "a@a.com"
  }' & curl -o -X POST http://localhost:8183/campsite/booking -H 'Content-Type: application/json' -d '{
      "arrivalDate": "2019-12-18",
      "departureDate": "2019-12-20",
      "fullName": "Victor John2",
      "email": "a@a.com"
  }' & curl -o -X POST http://localhost:8183/campsite/booking -H 'Content-Type: application/json' -d '{
      "arrivalDate": "2019-12-18",
      "departureDate": "2019-12-20",
      "fullName": "Victor John3",
      "email": "a@a.com"
  }' & curl -o -X POST http://localhost:8183/campsite/booking -H 'Content-Type: application/json' -d '{
      "arrivalDate": "2019-12-18",
      "departureDate": "2019-12-20",
      "fullName": "Victor John4",
      "email": "a@a.com"
  }' & curl -o -X POST http://localhost:8183/campsite/booking -H 'Content-Type: application/json' -d '{
      "arrivalDate": "2019-12-18",
      "departureDate": "2019-12-20",
      "fullName": "Victor John5",
      "email": "a@a.com"
  }' 
```
The response should be as follows, only one booking should be created:
```json
{"referenceNumber":"<n>","campsiteNumber":1,"arrivalDate":"2019-12-18","departureDate":"2019-12-20","fullName":"Victor John<x>","email":"a@a.com"}
["Intended booking dates are not available, please check dates availability"]
["Intended booking dates are not available, please check dates availability"]
["Intended booking dates are not available, please check dates availability"]
["Intended booking dates are not available, please check dates availability"]
```
### Load Test
Basic load test done via using Apache Bench, 500 requests per sec.
```
C:\Users\gagandeep.basra> ab.exe -n 10000 -c 500 -k http://localhost:8183/campsite/booking/availability
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:
Server Hostname:        localhost
Server Port:            8183

Document Path:          /campsite/booking/availability
Document Length:        378 bytes

Concurrency Level:      500
Time taken for tests:   9.894 seconds
Complete requests:      10000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      4830000 bytes
HTML transferred:       3780000 bytes
Requests per second:    1010.76 [#/sec] (mean)
Time per request:       494.678 [ms] (mean)
Time per request:       0.989 [ms] (mean, across all concurrent requests)
Transfer rate:          476.75 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1  11.3      0     507
Processing:   113  473 233.9    414    1055
Waiting:        2  299 237.5    220    1049
Total:        113  474 234.1    414    1056

Percentage of the requests served within a certain time (ms)
  50%    414
  66%    523
  75%    678
  80%    735
  90%    816
  95%    898
  98%   1019
  99%   1027
 100%   1056 (longest request)

C:\Users\gagandeep.basra>
```
### Unit and Integration Test Coverage
URL: **http://localhost:63342/campsite-booking/resources/test-coverage.html?_ijt=91fv4cqimo0fphmq2re2sg5uba**

![Test Coverage Snapshot](/resources/code-coverage.PNG)