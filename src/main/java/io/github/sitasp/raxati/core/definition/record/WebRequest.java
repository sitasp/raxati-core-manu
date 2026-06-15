package io.github.sitasp.raxati.core.definition.record;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.helidon.webserver.http.ServerRequest;

public record WebRequest<T>(
        T body,
        Map<String, List<String>> pathVariables,
        Map<String, List<String>> queryParams,
        Map<String, List<String>> headers,
        ServerRequest raw) {

    public Optional<String> pathVariable(String name) {
        return first(pathVariables, name);
    }

    public List<String> pathVariables(String name) {
        return pathVariables.getOrDefault(name, List.of());
    }

    public Optional<String> queryParam(String name) {
        return first(queryParams, name);
    }

    public List<String> queryParams(String name) {
        return queryParams.getOrDefault(name, List.of());
    }

    public Optional<String> header(String name) {
        return first(headers, name);
    }

    public List<String> headers(String name) {
        return headers.getOrDefault(name, List.of());
    }

    private static Optional<String> first(Map<String, List<String>> values, String name) {
        List<String> list = values.get(name);

        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(0));
    }
}
