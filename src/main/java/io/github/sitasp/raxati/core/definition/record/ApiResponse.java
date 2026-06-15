package io.github.sitasp.raxati.core.definition.record;

public record ApiResponse<T>(T data, String message, boolean success) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "OK", true);
    }

    public static <T> ApiResponse<T> failed(String message) {
        return new ApiResponse<>(null, message, false);
    }

    public static <T> ApiResponse<T> failed(T data, String message) {
        return new ApiResponse<>(data, message, false);
    }
}
