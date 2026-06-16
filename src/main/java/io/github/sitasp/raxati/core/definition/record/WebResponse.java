package io.github.sitasp.raxati.core.definition.record;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.http.HttpMediaType;
import io.helidon.http.Status;

public record WebResponse<T>(
        T data,
        Status status,
        Map<String, List<String>> headers) {

    public WebResponse {
        status = Objects.requireNonNullElse(status, Status.OK_200);
        headers = copyHeaders(headers);
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

    public static <T> WebResponse<T> notFound(T data) {
        return new WebResponse<>(data, Status.NOT_FOUND_404);
    }

    public static <T> WebResponse<T> error(T data) {
        return new WebResponse<>(data, Status.INTERNAL_SERVER_ERROR_500);
    }

    public static WebResponse<Void> noContent() {
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
            addHeader(headers, name, values);
            return this;
        }

        public Builder header(HeaderName name, String... values) {
            Objects.requireNonNull(name, "header name must not be null");
            return header(name.defaultCase(), values);
        }

        public Builder headers(Map<String, List<String>> headers) {
            copyHeaders(headers).forEach((name, values) ->
                    addHeader(this.headers, name, values.toArray(String[]::new)));
            return this;
        }

        public Builder location(String location) {
            return header(HeaderNames.LOCATION, location);
        }

        public Builder location(URI location) {
            Objects.requireNonNull(location, "location must not be null");
            return location(location.toString());
        }

        public Builder contentType(String contentType) {
            return header(HeaderNames.CONTENT_TYPE, contentType);
        }

        public Builder contentType(HttpMediaType contentType) {
            Objects.requireNonNull(contentType, "content type must not be null");
            return contentType(contentType.text());
        }

        public Builder status(Status status) {
            this.status = Objects.requireNonNull(status, "status must not be null");
            return this;
        }

        public Builder ok() {
            return status(Status.OK_200);
        }

        public Builder created() {
            return status(Status.CREATED_201);
        }

        public Builder accepted() {
            return status(Status.ACCEPTED_202);
        }

        public Builder badRequest() {
            return status(Status.BAD_REQUEST_400);
        }

        public Builder notFound() {
            return status(Status.NOT_FOUND_404);
        }

        public Builder error() {
            return status(Status.INTERNAL_SERVER_ERROR_500);
        }

        public <T> WebResponse<T> data(T data) {
            return new WebResponse<>(data, status, headers);
        }

        public <T> WebResponse<T> ok(T data) {
            return ok().data(data);
        }

        public <T> WebResponse<T> created(T data) {
            return created().data(data);
        }

        public <T> WebResponse<T> accepted(T data) {
            return accepted().data(data);
        }

        public <T> WebResponse<T> badRequest(T data) {
            return badRequest().data(data);
        }

        public <T> WebResponse<T> notFound(T data) {
            return notFound().data(data);
        }

        public <T> WebResponse<T> error(T data) {
            return error().data(data);
        }

        public WebResponse<Void> noContent() {
            return status(Status.NO_CONTENT_204).data(null);
        }
    }

    private static Map<String, List<String>> copyHeaders(Map<String, List<String>> source) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }

        Map<String, List<String>> copy = new LinkedHashMap<>();

        source.forEach((name, values) -> {
            Objects.requireNonNull(name, "header name must not be null");
            Objects.requireNonNull(values, "header values must not be null");
            values.forEach(value -> Objects.requireNonNull(value, "header value must not be null"));
            copy.put(name, List.copyOf(values));
        });

        return Map.copyOf(copy);
    }

    private static void addHeader(Map<String, List<String>> headers, String name, String... values) {
        Objects.requireNonNull(headers, "headers must not be null");
        Objects.requireNonNull(name, "header name must not be null");
        Objects.requireNonNull(values, "header values must not be null");

        List<String> copiedValues = Arrays.stream(values)
                .map(value -> Objects.requireNonNull(value, "header value must not be null"))
                .toList();

        headers.merge(name, copiedValues, (existing, added) -> {
            List<String> merged = new ArrayList<>(existing);
            merged.addAll(added);
            return merged;
        });
    }
}
