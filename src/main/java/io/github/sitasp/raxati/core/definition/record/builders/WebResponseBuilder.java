package io.github.sitasp.raxati.core.definition.record.builders;

import io.github.sitasp.raxati.core.definition.record.WebResponse;
import io.helidon.http.Status;

public final class WebResponseBuilder<T> {
    private T data;
    private Status status = Status.OK_200;

    private WebResponseBuilder() {
    }

    public static <T> WebResponseBuilder<T> builder() {
        return new WebResponseBuilder<>();
    }

    public WebResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public WebResponseBuilder<T> status(Status status) {
        this.status = status;
        return this;
    }

    public WebResponse<T> build() {
        return new WebResponse<>(data, status);
    }
}
