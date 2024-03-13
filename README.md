# Booking Service

This service is responsible for checking the availability of containers and booking them.

## Steps to Run Locally

1. Clone this repository from GitHub using the following command:

    ```shell
    git clone https://github.com/preethamrp/booking-service.git
    ```

2. Navigate to the cloned directory:

    ```shell
    cd booking-service
    ```

3. Run the Docker Compose file to spin up two containers - one for MongoDB and another for the booking-service application:

    ```shell
    docker-compose build
    docker-compose up
    ```

4. Once the containers are started up, use the Postman collection to run the APIs.

## Steps to Run API

### API to Check Availability
- Use the postman collection **"Booking Service.postman_collection.json"** present in the root folder and import it to the Postman App.
- 
- This API checks the availability of containers. However, since it uses an internal dummy API (that does not exist), it will always throw an error.

- **Request:**
    ```http
    POST http://localhost:8080/api/bookings/checkAvailability
    Content-Type: application/json
    
    {
        "containerSize": 20,
        "destination": "USA",
        "origin": "India",
        "containerType": "DRY",
        "quantity": 5
    }
    ```

- **Response:**
    ```json
    {
        "error": "An error occurred. Please try again later"
    }
    ```

### API to Book Container

- This API books a container and returns the booking reference.

- **Request:**
    ```http
    POST http://localhost:8080/api/bookings/book
    Content-Type: application/json
    
    {
   
    "containerType": "DRY",
    "containerSize": 20,
    "origin": "India",
    "destination": "Singapore",
    "quantity": 50,
    "timestamp": "2020-10-12T13:53:09Z"
    }
    ```

- **Response:**
    ```json
    {
        "bookingRef": "957000001"
    }
    ```

Feel free to reach out if you have any questions or need further assistance!
