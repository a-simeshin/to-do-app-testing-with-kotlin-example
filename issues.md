### Improvements and bugs related to CRUD todo application

- Add `/health` route
- Add `/metrics` route
- Add `/ping` route
- Add `GET` `even more detailed message` for `exceptional situations` with `query parameters offset and limit`
  - As is: 400 Bad Request: "Invalid query string"
  - To be: 400 Bad Request: "Invalid query parameter: offset=-1, should be equal or more than 0"
- Add `GET` method `query parameter offset restriction to unsigned 64-bit`
  - As is: no restrictions, can be more than Long.MAX(9,223,372,036,854,775,807)
  - TO be: cannot be more than Long.MAX(9,223,372,036,854,775,807)
- Add `GET` method `query parameter limit restriction to unsigned 64-bit`
  - As is: no restrictions, can be more than Long.MAX(9,223,372,036,854,775,807)
  - TO be: cannot be more than Long.MAX(9,223,372,036,854,775,807)
- Add `POST` detailed `message` with `request body payload about exceptional situations` with `deserialization`
  - As is: 400 Bad Request: "Request body deserialize error: EOF while parsing a value at line 1 column 0""
  - To be: 400 Bad Request: "Request body deserialize error: EOF while parsing a value at line 1 column 0. Request body: {...}"
- Add `POST` restriction to field `text` in `TODO entity`
  - As is: cannot be null String
  - To be: cannot be empty or null String
- Add `DELETE` and `PUT` restriction to `id` as `query parameter`
  - AS is: no restriction, can be -1, response: 404 NOT_FOUND
  - To be: cannot be not a 64u, cannot be null, detailed response about invalid value of query parameter
- Add `PUT` `more detailed information` about `what happens` in this exceptional situation
  - As is: 404 Not Found
  - To be: 404 Not Found: Cannot find TODO entity with id {}
- 

### Issues for tests

- Health check call HTTP method minimum twice in case of fail-fast and condition evaluation
- Restarting a Spring context does not reload the AUT image in Docker, cause 500 driver exception

