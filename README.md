# raxati-core-manu

Small Helidon SE helper library for writing route handlers in a compact, Spring-like style without annotations, scanning, or a container.

The library keeps Helidon SE routing explicit while removing repetitive request and response handling from route methods.

## Install

```xml
<dependency>
    <groupId>io.github.sitasp</groupId>
    <artifactId>raxati-core-manu</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Basic Route

```java
package me.satishpatra.se.quickstart.controller;

import io.github.sitasp.raxati.core.definition.handler.WebHandler;
import io.github.sitasp.raxati.core.definition.record.WebRequest;
import io.github.sitasp.raxati.core.definition.record.WebResponse;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class GreetController implements HttpService {

    @Override
    public void routing(HttpRules rules) {
        rules
                .get("/", WebHandler.create(this::getDefaultMessage))
                .get("/{name}", WebHandler.create(this::getMessage));
    }

    private WebResponse<String> getDefaultMessage(WebRequest<Void> request) {
        return WebResponse.ok("Ciao World!");
    }

    private WebResponse<String> getMessage(WebRequest<Void> request) {
        String name = request.pathVariable("name").orElse("World");
        return WebResponse.ok("Ciao " + name + "!");
    }
}
```

## JSON Body

```java
import java.util.UUID;

import io.github.sitasp.raxati.core.definition.handler.WebHandler;
import io.github.sitasp.raxati.core.definition.record.ApiResponse;
import io.github.sitasp.raxati.core.definition.record.WebRequest;
import io.github.sitasp.raxati.core.definition.record.WebResponse;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public class TaskController implements HttpService {

    @Override
    public void routing(HttpRules rules) {
        rules.post("/task", WebHandler.create(this::createTask, CreateTaskRequest.class));
    }

    private WebResponse<ApiResponse<TaskResponse>> createTask(WebRequest<CreateTaskRequest> request) {
        CreateTaskRequest body = request.body();

        if (body.value() == null || body.value().isBlank()) {
            return WebResponse.badRequest(ApiResponse.failed("value is required"));
        }

        TaskResponse task = new TaskResponse(UUID.randomUUID().toString(), body.value());

        return WebResponse.builder()
                .location("/task/" + task.id())
                .created(ApiResponse.success(task));
    }

    record CreateTaskRequest(String value) {
    }

    record TaskResponse(String id, String value) {
    }
}
```

Malformed request bodies are converted to:

```json
{
  "data": null,
  "message": "Invalid request body",
  "success": false
}
```

## Response Helpers

Use static helpers for the common path:

```java
return WebResponse.ok(data);
return WebResponse.created(data);
return WebResponse.badRequest(error);
return WebResponse.notFound(error);
return WebResponse.noContent();
```

Use the builder when the response needs metadata:

```java
return WebResponse.builder()
        .header("X-Request-Id", requestId)
        .contentType("application/json")
        .status(Status.ACCEPTED_202)
        .data(data);
```

## Raw Helidon Handlers

Native Helidon handlers can still be used directly:

```java
rules.get("/raw", WebHandler.create((request, response) -> response.send("OK")));
```

This keeps route registration visually consistent while still allowing full Helidon access when needed.

## Design

- No annotations.
- No classpath scanning.
- No dependency injection container.
- Helidon `HttpService` and `HttpRules` stay explicit.
- Route methods receive a typed `WebRequest<T>` and return `WebResponse<T>`.
