package io.github.sitasp.raxati.core.definition.record;

import io.helidon.http.Status;

public record WebResponse<T>(T data, Status status) {
    public WebResponse(T data) {
        this(data, Status.OK_200);
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

    public static <T> WebResponse<T> status(T data, Status status) {
        return new WebResponse<>(data, status);
    }
}
