# to-do-app-testing-with-kotlin-example

# Content

- [Issues and bugs](issues.md)

# Description

This project exemplifies some of the good test automation practices that I have come across:

- Tests are `independent of` the test stand or `environment`
  - Test access to the Application settings can be controlled through yml files and Spring profiles with:
    - `@ActiveProfiles("dev-stand")` for `application-dev-stand.yml` in resource folder
    - `-Dspring.profiles.active=dev-stand` for `application-dev-stand.yml` in resource folder
  - Test can be run anywhere and work with docker via test-containers or with usual deployed application on the cloud
    - `Developer's environment` via `test-containers` - `shift-left` practice
    - `CI` on `Pull Request build` - `shift-left` practice
    - `CD` on `Post Deploy build trigger` - classic `Integration tests` practice
- Tests used `Spring` ecosystem
  - Great community support for everything that Spring have
  - Junit5 runner and hamcrest assertions built-in integration
  - Possibilities to use any Spring-boot autoconfiguration libs
- Tests have a `layered API`
  - With the ability to `wait healthcheck` via Awaitility
  - With `fail-last` ability to wait for data via Awaitility
  - With the ability to `determine what the problem is`:
    - `Assertions failed`, then initiate `fail-last`
    - `Corrupted tests environment or tests API`, then initiate `fail-fast`
    - `Corrupted application environment`, then initiate `fail-fast`
- Tests have 2 packages
  - `todo.app.testing.api.rest.integration.smoke` with group = `smoke`
  - `todo.app.testing.api.rest.integration.regress` with regress = `regress`
- Tests make it possible to test certain functionality using `additional groups`
  - `delete-todo`
  - `get-todo`
  - `post-todo`
  - `put-todo`
  - `negative`
- Prepared for work with `mTLS` via support materials in formats
  - `PEM`
  - `JKS`
- Prepared for work with `different user credentials` for API interactions
  - via `Spring profiles`
- Allure reports
  - Strait allure reports with steps
  - Interceptor for `HTTP requests and responses`
  - Hamcrest aspect for Hamcrest `assertions`

# PRE run, dependencies, runner, formatter

Tests required to run with Surefire or Failsafe with correct aspectj agent configuration

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <configuration>
        <testFailureIgnore>false</testFailureIgnore>
        <argLine>
            -Dfile.encoding=UTF-8
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar"
        </argLine>
        <systemPropertyVariables>
            <allure.results.directory>
                ${project.build.directory}/allure-results
            </allure.results.directory>
        </systemPropertyVariables>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>${aspectj.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

To run tests in parallel mode, you can add settings to the Surefire's or Failsafe's config

```xml

<systemPropertyVariables>
    <allure.results.directory>
        ${project.build.directory}/allure-results
    </allure.results.directory>
    <junit.jupiter.extensions.autodetection.enabled>
        true
    </junit.jupiter.extensions.autodetection.enabled>
    <junit.jupiter.execution.parallel.enabled>
        true
    </junit.jupiter.execution.parallel.enabled>
    <junit.jupiter.execution.parallel.config.strategy>
        dynamic
    </junit.jupiter.execution.parallel.config.strategy>
</systemPropertyVariables>
```

Tests require dependencies

```xml
<dependencies>
    <!-- All for HTTP -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
    </dependency>
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-spring-web</artifactId>
    </dependency>
    <!-- SSL -->
    <dependency>
        <groupId>io.github.hakky54</groupId>
        <artifactId>sslcontext-kickstart-for-pem</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.hakky54</groupId>
        <artifactId>sslcontext-kickstart</artifactId>
    </dependency>
    <!-- Handy Polling for Rest -->
    <dependency>
        <groupId>org.awaitility</groupId>
        <artifactId>awaitility</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.module</groupId>
        <artifactId>jackson-module-kotlin</artifactId>
    </dependency>
    <!-- etc -->
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-reflect</artifactId>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib-jdk8</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
    <!-- Runner -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <!--suppress KotlinTestJUnit -->
        <artifactId>kotlin-test</artifactId>
    </dependency>
    <!-- Allure -->
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-junit5</artifactId>
        <version>${allure.version}</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>${aspectj.version}</version>
    </dependency>
    <!-- TestContainers -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-testcontainers</artifactId>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
    </dependency>
</dependencies>
```

Tests have a limited code style and will not run without proper adherence to the rules.
Before run be sure that you have nice formatted code by ``mvn spotless:apply``

# RUN test

Now is the time to run `smoke` tests that check the availability of the application

``mvn test -Dgroups=smoke allure:serve``

If `smoke` tests pass, you usually need to run `regress`

``mvn test -Dgroups=regress allure:serve``

Sometimes it makes sense to run only for certain changes, for example, in the last application changes there were changes to get methods

``mvn test -Dgroups=get-todo allure:serve``

Imagine that the developers have finally reached the point of error handling and the new version has changes to decipher errors

``mvn test -Dgroups=negative allure:serve``

Imagine now that we have 2 CD stands: Dev, Integration, it's possible to run different configurations like

``mvn test -Dgroups=smoke -Dspring.profiles.active=dev allure:serve`` for application-dev.yml

``mvn test -Dgroups=smoke -Dspring.profiles.active=integration allure:serve`` for application-integration.yml

What is `application-integration.yml` file and how to prepare it correctly?

# Configs, settings and profiles

Usually, in preparing a project for writing actual tests, very large proportion of time is spent on managing
properties, building objects, etc. boilerplate. Spring autoconfiguration takes care of all the work. You just need to
adjust the yml file template to suit your needs.

### Template yml file

Template file location is
``src\main\resources\application.yml.ex``

with

```yml
# This is template yaml file for different test environments.
# For usage delete .ex from filename, then edit postfix profile-name and all required parameters below.

rest-client-config-data:
  url: "http://localhost:8080"
  todosGetPath: "/todos"
  todosPostPath: "/todos/{id}"
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
# By default enabled=false and these properties is not required for integration tests
to-do-app-in-docker:
  enabled: true
  imageName: "todo-app:latest"
  exposedPorts:
    8080: 4242
```

### Application related HTTP API client configuration

Let's dig into! First we need to build a REST API client for test purposes via:

```yml
rest-client-config-data:
  url: "http://localhost:8080"
  todosGetPath: "/todos"
  todosPostPath: "/todos"
  todosPutPath: "/todos/{id}"
  todosDeletePath: "/todos/{id}"
```

### Auth

If authorization is required for some API methods

```yml
rest-client-config-data:
  basicAuthLogin: "test credential login"
  basicAuthPassword: "test credential password"
```

It can also be passed through command line variables when running on CI/CD
``mvn test -Drest-client-config-data.basicAuthLogin=login -Drest-client-config-data.basicAuthPassword=password``

### Security in PEM and JKS

In addition to the http REST API, the client must support https for production-like test environments
All the parameters below are optional, if not provided, only HTTP integration will be available.

For PEM format

```yml
rest-client-config-data:
  ssl: true
  trustCertCollectionPath: "path/to/ca.pem"
  certificateChain: "path/to/tls.crt"
  privateKey: "path/to/tls.key"
```

For JKS format

```yml
rest-client-config-data:
  ssl: true
  keyStorePath: "path/to/keyStore.jks"
  keyStorePassword: "123456"
  trustStorePath: "path/to/trustStore.jks"
  trustStorePassword: "123456"
```

It can also be passed through command line variables when running on CI/CD for example
``mvn test -Drest-client-config-data.keyStorePath="path/to/keyStore.jks"``

### HTTP client Timeouts

In some cases, you need to tweak the settings for the duration of connection timeouts and the number of connections.
All values should be in milliseconds format.

Example:

```yml
rest-client-config-data:
  connectTimeout: 30000
  connectionRequestTimeout: 30000
  socketTimeout: 30000
  maxTotalConnections: 1000
  maxConnectionsPerRoute: 5
```

### REST API client Timeout for fail-last

In addition to connection timeouts, it is very important to be able to wait for data in tests, even with synchronous
interaction. Exceptions can often occur in which it is not possible to receive data the first time. In addition to
exceptional situations, it is not uncommon to encounter situations where a synchronous request to save or process data
causes an asynchronous flow.

The `Awaitility`library will help stabilize tests with data acquisition using the POLL mechanism with a maximum
expectation and poll interval settings. All values should be in milliseconds format.

```yml
awaitility:
  defaultPollInterval: 500
  defaultTimeout: 10000
```

### Running the app under test in a container

In order to shift testing as much as possible towards early testing, most likely, the developer will want to test
changes to the application even BEFORE deploying to the testing environment, in which case it is necessary to enable
local deployment of tests and the application in the form of a docker image using the test-containers tool.

```yml
# This section for generic docker image configuration for test-containers
# By default, enabled=false
to-do-app-in-docker:
  enabled: true
  imageName: "todo-app:latest"
  exposedPorts:
    8080: 4242
```

It is also possible to run an integration application on a remote host with a running docker engine.
- https://java.testcontainers.org/features/configuration/
- https://github.com/docker-java/docker-java/blob/main/docs/getting_started.md#properties-docker-javaproperties
