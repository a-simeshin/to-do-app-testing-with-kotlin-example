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
  - To be: 400 Bad Request: "Request body deserialize error: EOF while parsing a value at line 1 column 0. Request
    body: {...}"
- Add `POST` restriction to field `text` in `TODO entity`
  - As is: cannot be null String
  - To be: cannot be empty or null String
- Add `DELETE` and `PUT` restriction to `id` as `query parameter`
  - AS is: no restriction, can be -1, response: 404 NOT_FOUND
  - To be: cannot be not a 64u, cannot be null, detailed response about invalid value of query parameter
- Add `PUT` `more detailed information` about `what happens` in this exceptional situation
  - As is: 404 Not Found
  - To be: 404 Not Found: Cannot find TODO entity with id {}
- There is a linear relationship between the volume of persisted entities via POST and the performance degradation of
  persistence via POST

[Report json](https://github.com/a-simeshin/to-do-app-testing-with-kotlin-example/blob/develop/performance/jmh-report.json)
For fast-view recommends to use https://jmh.morethan.io/

Results:

| Iteration (each 10s) |  Throughput (tps)  |
|----------------------|:------------------:|
| 1                    | 1760.7533389108683 |
| 2                    | 1812.6600311572374 |
| 3                    | 1701.3090391827957 |
| 4                    | 1569.826973670962  |
| 5                    | 1591.803127436374  |
| 6                    | 1583.970890363783  |
| 7                    | 1515.862869150945  |
| 8                    | 1452.3689804576695 |
| 9                    | 1405.5689949693392 |
| 10                   | 1415.2755891370562 |

Percentiles:

| Percentile | Throughput (tps) |
|------------|:----------------:|
| 0.0        |     1405.57      |
| 50.0       |     1576.90      |
| 90.0       |     1807.47      |
| 95.0       |     1812.66      |
| 99.0       |     1812.66      |
| 99.9       |     1812.66      |
| 99.99      |     1812.66      |
| 99.999     |     1812.66      |
| 99.9999    |     1812.66      |
| 100.0      |     1812.66      |

### Issues for tests

- Health check call HTTP method minimum twice in case of fail-fast and condition evaluation
- Restarting a Spring context does not reload the AUT image in Docker, cause 500 driver exception
- Proxy-application with micrometer to collect metrics, if real application does not provide metrics
- Load-runner based performance check

