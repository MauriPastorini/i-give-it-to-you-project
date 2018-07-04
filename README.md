# Yo te lo regalo API

Esta documentación muestra la comunicación básica con la API Yo te lo regalo.

# Group Category
## All Categories [/category]

### List All Categories [GET]

+ Response 200 (application/json)

        [
            {
                "_id": "5aa582e94cc7e94920a6de20",
                "name": "Tecnología"
            }
        ]

# Group User
## User [/user]
### List All Users [GET /user?role={role}]

+ Parameters
    + role: admin (string)

+ Request

    + Attributes (TokenA)

    + Headers

            Authorization: Bearer TOKEN

+ Response 200 (application/json)

        [
            {
                "_id": "5a9b8677a75e8147b0c8e7d8",
                "birthday": null,
                "country": "Uruguay",
                "email": "mauri295@gmail.com",
                "gender": "Male",
                "lastName": "Pastorini",
                "name": "Mauri",
                "photo": "https://firebasestorage.googleapis.com/v0/b/yo-te-lo-regalo.appspot.com/o/5a9b8677a75e8147b0c8e7d8%2F20180214_103411.jpg?alt=media&token=f04384b9-540c-41d9-b0f7-13ad1f03ff6a",
                "__v": 0,
                "facebookId": "10206015146810379",
                "totalStars": 50,
                "role": "user",
                "signupDate": "2018-03-04T05:23:19.438Z"
            },
            {
                "_id": "5aa599c3f9ab722bc08137ce",
                "name": "Mauri",
                "lastName": "Pastorini",
                "password": "$2a$10$9hBw03bPDwAmQDn0ijFyOekCrL0P8KBE8HB7o746ajYNdAxIx1tK.",
                "email": "dfssdsadd@gmail.com",
                "country": "Uruguay",
                "__v": 0,
                "totalStars": 12,
                "role": "user",
                "signupDate": "2018-03-11T21:00:23.122Z"
            }
        ]

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Unthorized role for request"
            }

### Update user [PUT /user/{id}]

+ Parameters
    + id: ABC (string)

+ Request(application/json)

    + Attributes (TokenU)

    + Headers

            Authorization: Bearer TOKEN

+ Response 200 (application/json)

        {
            "_id": "5aa686a7ab32221e585f04e4",
            "name": "Mauri",
            "lastName": "Pastorini",
            "password": "$2a$10$d8ZqkM/0hS7Cp9.q/eeR7uEskRFKdDzNQ8LsdG78Vhfyrk3JH8Vru",
            "email": "mauri295295@gmail.com",
            "country": "Uruguay",
            "__v": 0,
            "gender": "male",
            "totalStars": 0,
            "role": "admin",
            "signupDate": "2018-03-12T13:50:49.131Z"
        }

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### Delete user [DELETE /user/{id}]

+ Parameters
    + id: ABC (string)

+ Request(application/json)

    + Attributes (TokenA)

    + Headers

            Authorization: (TOKEN)

+ Response 200 (application/json)

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### Set User as admin [POST /user/{id}/admin]

+ Parameters
    + id: ABC (string)

+ Request(application/json)

    + Attributes (TokenA)

    + Headers

            Authorization: (TOKEN)

+ Response 200 (application/json)

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### Sign in [POST /user/signin]

+ Request(application/json)

+ Response 200 (application/json)

            {
                "message": "Ok",
                "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1YTliODY3N2E3NWU4MTQ3YjBjOGU3ZDgiLCJpYXQiOjE1MjA3OTQxNDAsImV4cCI6MTUyMjAwMzc0MH0.gF44g57as4eKEncGt1SN-qVVfHr05BT4QPJuWzPAfG8"
            }

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

+ Response 403 (application/json)

            {
             errors: [
                {
                  code: 7,
                  message: "Some information for authentication is missing"
                }
              ]
            }
+ Response 403 (application/json)

            {
             errors: [
                {
                  code: 8,
                  message: "Invalid facebook token"
                }
              ]
            }
+ Response 403 (application/json)

            {
             errors: [
                {
                  code: 9,
                  message: "User doesnt exists"
                }
              ]
            }

### Sign up [POST /user/signup]

+ Request(application/json)

+ Response 200 (application/json)

### Validate token [GET /user/token]

+ Request(application/json)

+ Response 200 (application/json)


# Group Product
## Product [/product?lat={lat}&lng={lng}&moderated={moderated}&category={categoryId}&solicitatedByMe={solicitatedByMe}&recently={recently}&maxTendency={maxTendency}&maxApplicants={maxApplicants}&minApplicants={minApplicants}&bestOwnerUser={bestOwnerUser}&injectUser={injectUser}]
### List All Products [GET]

+ Parameters
    + lat: 100 (int)
    + lng: 10 (int)
    + moderated: true (boolean)
    Only if is admin
    + categoryId: ABC (string)
    + solicitatedByMe: true (boolean)
    + recently: true (boolean)
    + maxTendency: true (boolean)
    + maxApplicants: true (boolean)
    + minApplicants: true (boolean)
    + bestOwnerUser: true (boolean)
    actualmente este parametro no esta implementado
    + injectUser: true (boolean)

+ Request

    + Attributes (Token)

    + Headers

            Authorization: Bearer TOKEN Parametro necesario si se quiere hacer la solicitud de productos del usuario o los no moderados

+ Response 200 (application/json)

            [
                {
                    "_id": "5aa57e66bc35f72b70195927",
                    "name": "Television",
                    "description": "Muy buena de los anios 70",
                    "category": {
                        "_id": "5aa582e94cc7e94920a6de20",
                        "name": "Tecnología",
                        "products": [],
                        "__v": 0
                    },
                    "condition": "New",
                    "location": [
                        -32.8756188,
                        -56.1421267,
                        15
                    ],
                    "ownerUser": "5a9b8677a75e8147b0c8e7d8",
                    "__v": 0,
                    "countTendency": 52,
                    "created_at": "2018-03-11T20:43:52.000Z",
                    "applicantsUsers_count": 1,
                    "applicantsUsers": [],
                    "image_path": [
                        "https://firebasestorage.googleapis.com/v0/b/si-venis-es-tuyo-1494542561433.appspot.com/o/products%2F11111401_917219405000803_1884964792_a.jpg?alt=media&token=516eb32b-64f4-4e7b-9d26-ae158f4df476"
                    ],
                    "moderated": true
                },
                {
                    "_id": "5aa57e67bc35f72b70195928",
                    "name": "Television",
                    "description": "Muy buena de los anios 70",
                    "category": {
                        "_id": "5aa582e94cc7e94920a6de20",
                        "name": "Tecnología",
                        "products": [],
                        "__v": 0
                    },
                    "condition": "New",
                    "location": [
                        -31.8756188,
                        -57.1421267,
                        15
                    ],
                    "ownerUser": "5aa599c3f9ab722bc08137ce",
                    "__v": 0,
                    "countTendency": 1,
                    "created_at": "2018-03-11T20:42:55.000Z",
                    "applicantsUsers_count": 2,
                    "applicantsUsers": [],
                    "image_path": [
                        "https://firebasestorage.googleapis.com/v0/b/si-venis-es-tuyo-1494542561433.appspot.com/o/products%2F11111401_917219405000803_1884964792_a.jpg?alt=media&token=516eb32b-64f4-4e7b-9d26-ae158f4df476"
                    ],
                    "moderated": true
                }
            ]

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### Publicate new product [POST]

+ Request(application/json)

            {
                "name": "Television" (string),
                "description":"Muy buena de los anios 70" (string),
                "category":"59a47a9207f3615a54f26c48" (ObjectId),
                "image_path": "https://firebasestorage.googleapis.com/v0/b/si-venis-es-tuyo-1494542561433.appspot.com/o/products%2F11111401_917219405000803_1884964792_a.jpg?alt=media&token=516eb32b-64f4-4e7b-9d26-ae158f4df476",
                "location": [100,100],
                "ownerUser": "5a037763fb155821e48e1fd4" (ObjectId)
            }

+ Response 200 (application/json)

+ Response 422 (application/json)

            {
                "errors": [
                        {
                            "code": 10,
                            "message": "ownerUser references a non existing ID"
                        },
                        {
                            "code": 11,
                            "message": "category references a non existing ID"
                        }
                    ]   
            }

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### List One Product [GET /product/{id}]

+ Parameters
    + id: ABC(required, string)

+ Response 200 (application/json)

            {
                "status": "osk"
            }


### Delete product  [DELETE /product/{id}]

+ Parameters
  + id: ABC(required,string)

+ Request(application/json)

+ Response 200 (application/json)

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### Accept moderated product [POST /product/{id}/accept]

+ Parameters
    + id: ABC(required, string}

+ Request
    + Attributes (TokenA)

+ Response 200 (application/json)

### Reject moderated product [POST /product/{id}/reject]

+ Parameters
    + id: ABC(required, string}

+ Request
    + Attributes (TokenA)

+ Response 200 (application/json)

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Invalid Token"
            }

### Add image path to product [POST /product/{id}/image]

+ Parameters
    + id: ABC(required, string)

+ Request (application/json)

            [
                {
                    "image_path": "https://www.anipedia.net/imagenes/caracteristicas-generales-de-los-elefantes.jpg"
                },
                {
                    "image_path": "https://www.anipedia.net/imagenes/caracteristicas-generales-de-los-elefantes.jpg"
                }
            ]

+ Response 200 (application/json)

+ Response 403 (application/json)

            {
                "success": false,
                "message": "Unthorized user, this action can be performed only by the owner user of the product"
            }

+ Response 404 (application/json)

            {
                "success": false,
                "message": "Product doesnt exists"
            }

### Add new solicitation [POST /product/{productId}/solicitude]

+ Request
    + Attributes (TokenU)

+ Parameters
    + productId: ABC(required, string)

+ Response 200 (application/json)

### Delete solicitation [DELETE /product/{id}/solicitude]

+ Request
    + Attributes (TokenU)
    The user that delete must have solicitated the product before

+ Parameters
    + id: ABC(required, string)

+ Response 200 (application/json)

+ Response 422 (application/json)

        {
            code: 13,
            message: "User was not in products solicitation"
        }

### Add user to deliver the product [POST /product/{id}/user-to-deliver]

+ Parameters
    + id: ABC(required,string)

+ Response 200 (application/json)

### Confirm product delivered [POST /product/{id}/delivered]

+ Parameters
    + id: ABC(required, string)

+ Response 200 (application/json)

# Group Review

## Review [/review]

### Add new review [POST]

+ Response 200 (application/json)






# Data Structures
## TokenA (object)
  + TOKEN: ABC (required, string)
        The user must be admin
## TokenU (object)
  + TOKEN: ABC (required, string)
        The user to update must be the same that updates

## Token (object)
  + TOKEN: ABC (required, string)
        The user must be logged in
