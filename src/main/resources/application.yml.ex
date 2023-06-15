# This is template yaml file for different test environments.
# For just you should delete .ex from filename, then edit all required parameters.

rest-client-config-data:
  url: "http://localhost:8080"
  todosGetPath: "/todos"
  todosPostPath: "/todos/{id}"
  todosPutPath: "/todos/{id}"
  todosDeletePath: "/todos/{id}"
  ssl: true
  trustCertCollectionPath: "path/to/ca.pem"
  certificateChain: "path/to/tls.crt"
  privateKey: "path/to/tls.key"
  connectTimeout: 30000
  connectionRequestTimeout: 30000
  socketTimeout: 30000
  maxTotalConnections: 1000
  maxConnectionsPerRoute: 5