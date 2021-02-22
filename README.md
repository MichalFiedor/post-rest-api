# Rest API Post Application
The Post Application once a day, or at the user's request `http://localhost:8080/posts/REST` download posts data 
from public API  `https://jsonplaceholder.typicode.com/posts`.
The retrieved data is stored in the local `MySql` database. In case the database is empty, the posts 
are saved. When the database contains data, posts are updated, ignoring posts edited by user
or deleted posts. 

Before running the application, create a local database named `post_api` and in `application.properites`
infill `spring.datasource.username` and `spring.datasource.password`.

## Get List of Posts

### Request

`GET /posts/`

    curl -X GET "http://localhost:8080/posts" -H "accept: */*"

### Success Response

  * **Code:** 200 <br />
    **Content:** `[
                    {
                      "id": 1,
                      "title": "sunt aut facere repellat",
                      "body": "quia et suscipit\nsuscipit"
                    },
                    {
                      "id": 2,
                      "title": "qui est esse",
                      "body": "est rerum tempore vitae\  aperiam"
                    }]`
    
### Error Response
    
      * **Code:** 404 NOT FOUND <br />
        **Content:**  {
                          "timestamp": "22-02-2021 05:47:40",
                          "status": "NOT_FOUND",
                          "message": "Posts not found.",
                          "errors": [
                              "Posts database is empty."
                          ]
                      }


## Get Post by Title

### Request

`GET /posts/:title`

    curl -X GET "http://localhost:8080/posts/magnam%20ut%20rerum%iure" -H "accept: */*"
    
### URL Params

   **Required:**
 
   `title=[String]`
  
### Success Response

  * **Code:** 200 <br />
    **Content:** `{
                      "id": 34,
                      "title": "magnam ut rerum iure",
                      "body": "ea velit perferendis earum nesciunt nobis"
                  }`
    
### Error Response
    
      * **Code:** 404 NOT FOUND <br />
        **Content:**  {
                          "timestamp": "22-02-2021 05:51:43",
                          "status": "NOT_FOUND",
                          "message": "Post not found. Check errors list for details.",
                          "errors": [
                              "Post with title: magnam ut rerum iure does not exist."
                          ]
                      }


## Edit Post

### Request

`PUT /posts/`

    curl -X PUT "http://localhost:8080/posts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"id\": 1, \"title\": \"Post title\", \"body\": \"Post body\"}"
    
### Data Params

   **Model:**
   
    {
    id	integer($int64)
    title	string
            minLength: 5
            maxLength: 500
    body	string
            minLength: 5
            maxLength: 500    
    }

   **Required:**
   `{
      "id": integer,
      "body": "string",
      "title": "string"
    }`
  
### Success Response

  * **Code:** 200 <br />
    **Content:** 
                 `{
                    "id": 1,
                    "title": "Post title",
                    "body": "Post body"
                  }`
    
### Error Response
    
      * **Code:** 404 NOT FOUND <br />
        **Content:**  {
                        "timestamp": "22-02-2021 06:08:54",
                        "status": "NOT_FOUND",
                        "message": "Post not found. Check errors list for details.",
                        "errors": [
                          "Post with ID: 111 does not exist."
                        ]
                      }
                      
                      
## Delete Post by ID

### Request

`DELETE /posts/:id`

    curl -X DELETE "http://localhost:8080/posts/1" -H "accept: */*"
    
### URL Params

   **Required:**
 
   `id=[integer]`
  
### Success Response

  * **Code:** 200 <br />
    **Content:** `Post successfully deleted
`
### Error Response
    
      * **Code:** 404 NOT FOUND <br />
        **Content:**  {
                        "timestamp": "22-02-2021 06:12:19",
                        "status": "NOT_FOUND",
                        "message": "Post not found. Check errors list for details.",
                        "errors": [
                          "Post with ID: 111 does not exist."
                        ]
                      }
                      
                      
## Update Posts

### Request

`POST /posts/REST`

    curl -X POST "http://localhost:8080/posts/REST" -H "accept: */*" -d ""

### Success Response

  * **Code:** 200 <br />
    **Content:** `Posts successfully updated`
    
### Error Response
    
      * **Code:** 404 NOT FOUND <br />
        **Content:**  {
                        "timestamp": "22-02-2021 06:21:36",
                        "status": "NOT_FOUND",
                        "message": "Posts not updated. API connection failure.",
                        "errors": [
                          "IP address of a host could not be determined."
                        ]
                      }