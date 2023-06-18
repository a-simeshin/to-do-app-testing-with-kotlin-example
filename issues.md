### Improvements and bugs related to CRUD todo application

- Add `/health` route
- Add `/metrics` route
- Add `/ping` route
- Add detailed message for exceptional situations with query parameters offset and limit
  - As is: 400 Bad Request: "Invalid query string"
  - To be: 400 Bad Request: "Invalid query parameter: offset=-1, should be equal or more than 0"
- Add GET method query parameter offset restriction to unsigned 64-bit
  - As is: no restrictions, can be more than Long.MAX(9,223,372,036,854,775,807)
  - TO be: cannot be more than Long.MAX(9,223,372,036,854,775,807)
- Add GET method query parameter limit restriction to unsigned 64-bit
  - As is: no restrictions, can be more than Long.MAX(9,223,372,036,854,775,807)
  - TO be: cannot be more than Long.MAX(9,223,372,036,854,775,807)
- Add detailed message with request body payload about exceptional situations with deserialization of POST message body into entity
  - As is: 400 Bad Request: "Request body deserialize error: EOF while parsing a value at line 1 column 0""
  - To be: 400 Bad Request: "Request body deserialize error: EOF while parsing a value at line 1 column 0. Request body: {...}"
- Add restriction to TODO Entity text
  - As us: cannot be null String
  - To be: cannot be empty or null String
- 

### Issues for tests

- Health check call HTTP method minimum twice in case of fail-fast and condition evaluation

