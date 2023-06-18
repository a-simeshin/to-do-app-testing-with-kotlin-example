# This is template yaml file for different test environments.
# For usage delete .ex from filename, then edit postfix profile-name and all required parameters below.

rest-client-config-data:
  url: "http://localhost:8080"
  todosGetPath: "/todos"
  todosPostPath: "/todos"
  todosPutPath: "/todos/{id}"
  todosDeletePath: "/todos/{id}"
  basicAuthLogin: "test credential login"
  basicAuthPassword: "test credential password"
  ssl: true
  trustCertCollectionPath: "path/to/ca.pem"
  certificateChain: "path/to/tls.crt"
  privateKey: "path/to/tls.key"
  connectTimeout: 30000
  connectionRequestTimeout: 30000
  socketTimeout: 30000
  maxTotalConnections: 1000
  maxConnectionsPerRoute: 5
  enableHttpAttachments: true

awaitility:
  defaultPollInterval: 500
  defaultTimeout: 10000

# This section for generic docker image configuration for test-containers
# By default enabled=false and this properties is not required for integration tests
to-do-app-in-docker:
  enabled: true
  imageName: "todo-app:latest"
  exposedPorts:
    8080: 4242