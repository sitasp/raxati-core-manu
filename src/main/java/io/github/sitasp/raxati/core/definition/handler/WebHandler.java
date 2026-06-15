package io.github.sitasp.raxati.core.definition.handler;

import io.github.sitasp.raxati.core.definition.record.ApiResponse;
import io.github.sitasp.raxati.core.definition.record.WebRequest;
import io.github.sitasp.raxati.core.definition.record.WebResponse;
import io.github.sitasp.raxati.core.definition.route.WebRoute;
import io.helidon.http.Status;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public final class WebHandler {
    private WebHandler() {
    }

    public static Handler create(Handler handler) {
        return handler;
    }

    private static <B> WebRequest<B> webRequest(ServerRequest sRequest, B body) {
        return new WebRequest<B>(
                body,
                sRequest.path().pathParameters().toMap(),
                sRequest.query().toMap(),
                sRequest.headers().toMap(),
                sRequest);
    }

    public static <R> Handler create(WebRoute<Void, R> route) {
        return (request, response) -> {
            WebRequest<Void> webRequest = webRequest(request, null);
            send(response, route.handle(webRequest));
        };
    }

    public static <B, R> Handler create(WebRoute<B, R> route, Class<B> reqBodyType) {
        return (request, response) -> {
            try {
                B body = request.content().as(reqBodyType);
                WebRequest<B> webRequest = webRequest(request, body);
                send(response, route.handle(webRequest));
            } catch (IllegalArgumentException | IllegalStateException e) {
                response.status(Status.BAD_REQUEST_400);
                response.send(ApiResponse.failed("Invalid request body"));
            }
        };
    }

    private static <R> void send(ServerResponse response, WebResponse<R> webResponse) {
        response.status(webResponse.status());

        if (webResponse.data() == null) {
            response.send();
            return;
        }

        response.send(webResponse.data());
    }
}
