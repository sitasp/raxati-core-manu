package io.github.sitasp.raxati.core.definition.record;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.helidon.http.HeaderNames;
import io.helidon.http.HttpMediaTypes;
import io.helidon.http.Status;

class WebResponseTest {

    @Test
    void createsCommonResponses() {
        assertEquals(Status.OK_200, WebResponse.ok("ok").status());
        assertEquals(Status.CREATED_201, WebResponse.created("created").status());
        assertEquals(Status.BAD_REQUEST_400, WebResponse.badRequest("bad").status());
        assertEquals(Status.NOT_FOUND_404, WebResponse.notFound("missing").status());
        assertEquals(Status.INTERNAL_SERVER_ERROR_500, WebResponse.error("error").status());
    }

    @Test
    void createsNoContentResponseWithoutBody() {
        WebResponse<Void> response = WebResponse.noContent();

        assertEquals(Status.NO_CONTENT_204, response.status());
        assertNull(response.data());
    }

    @Test
    void buildsResponseWithHeadersStatusAndData() {
        WebResponse<String> response = WebResponse.builder()
                .header("X-Request-Id", "request-1")
                .header(HeaderNames.VARY, "Accept-Encoding")
                .location(URI.create("/tasks/1"))
                .contentType(HttpMediaTypes.JSON)
                .accepted("accepted");

        assertEquals(Status.ACCEPTED_202, response.status());
        assertEquals("accepted", response.data());
        assertEquals(List.of("request-1"), response.headers().get("X-Request-Id"));
        assertEquals(List.of("Accept-Encoding"), response.headers().get("Vary"));
        assertEquals(List.of("/tasks/1"), response.headers().get("Location"));
        assertEquals(List.of("application/json"), response.headers().get("Content-Type"));
    }

    @Test
    void mergesRepeatedHeaders() {
        WebResponse<String> response = WebResponse.builder()
                .header("Set-Cookie", "a=1")
                .header("Set-Cookie", "b=2")
                .ok("ok");

        assertEquals(List.of("a=1", "b=2"), response.headers().get("Set-Cookie"));
    }

    @Test
    void copiesHeadersDefensively() {
        List<String> values = new java.util.ArrayList<>(List.of("one"));
        Map<String, List<String>> headers = new java.util.LinkedHashMap<>();
        headers.put("X-Test", values);

        WebResponse<String> response = new WebResponse<>("data", Status.OK_200, headers);

        values.add("two");
        headers.put("X-Other", List.of("other"));

        assertEquals(Map.of("X-Test", List.of("one")), response.headers());
        assertThrows(UnsupportedOperationException.class, () -> response.headers().put("X-New", List.of("new")));
        assertThrows(UnsupportedOperationException.class, () -> response.headers().get("X-Test").add("two"));
    }

    @Test
    void rejectsInvalidBuilderInput() {
        WebResponse.Builder builder = WebResponse.builder();

        assertThrows(NullPointerException.class, () -> builder.header((String) null, "value"));
        assertThrows(NullPointerException.class, () -> builder.header("X-Test", (String[]) null));
        assertThrows(NullPointerException.class, () -> builder.header("X-Test", "value", null));
        assertThrows(NullPointerException.class, () -> builder.status(null));
        assertThrows(NullPointerException.class, () -> builder.location((String) null));
        assertThrows(NullPointerException.class, () -> builder.location((URI) null));
    }
}
