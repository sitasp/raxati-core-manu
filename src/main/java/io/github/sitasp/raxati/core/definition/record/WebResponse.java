package io.github.sitasp.raxati.core.definition.record;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.helidon.http.Status;

public record WebResponse<T>(T data,
        Status status,
        Map<String, List<String>> headers) {

    public WebResponse {
        if (status == null) {
            status = Status.OK_200;
        }

        if (headers == null) {
            headers = Map.of();
        } else {
            headers = copyHeaders(headers);
        }
    }

    public WebResponse(T data) {
        this(data, Status.OK_200);
    }

    public WebResponse(T data, Status status) {
        this(data, status, Map.of());
    }

    public static <T> WebResponse<T> ok(T data) {
        return new WebResponse<>(data, Status.OK_200);
    }

    public static <T> WebResponse<T> created(T data) {
        return new WebResponse<>(data, Status.CREATED_201);
    }

    public static <T> WebResponse<T> badRequest(T data) {
        return new WebResponse<>(data, Status.BAD_REQUEST_400);
    }

    public static <T> WebResponse<T> error(T data) {
        return new WebResponse<>(data, Status.INTERNAL_SERVER_ERROR_500);
    }

    public static <T> WebResponse<T> noContent() {
        return new WebResponse<>(null, Status.NO_CONTENT_204);
    }

    public static <T> WebResponse<T> status(T data, Status status) {
        return new WebResponse<>(data, status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Status status = Status.OK_200;
        private final Map<String, List<String>> headers = new LinkedHashMap<>();

        public Builder header(String name, String... values) {
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public <T> WebResponse<T> data(T data) {
            return new WebResponse<T>(data, status, headers);
        }

        public <T> WebResponse<T> ok(T data) {
            return status(Status.OK_200).data(data);
        }

        public <T> WebResponse<T> created(T data) {
            return status(Status.CREATED_201).data(data);
        }

        public <T> WebResponse<T> noContent() {
            return status(Status.NO_CONTENT_204).data(null);
        }

        public <T> WebResponse<T> error(T data) {
            return new WebResponse<>(data, Status.INTERNAL_SERVER_ERROR_500);
        }
    }

    private static Map<String, List<String>> copyHeaders(Map<String, List<String>> source) {
        Map<String, List<String>> copy = new LinkedHashMap<>();

        source.forEach((key, values) -> {
            if (key != null && values != null) {
                copy.put(key, List.copyOf(values));
            }
        });

        return Map.copyOf(copy);
    }
}
