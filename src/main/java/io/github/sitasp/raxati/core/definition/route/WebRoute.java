package io.github.sitasp.raxati.core.definition.route;

import io.github.sitasp.raxati.core.definition.record.WebRequest;
import io.github.sitasp.raxati.core.definition.record.WebResponse;

@FunctionalInterface
public interface WebRoute<B, R> {
    WebResponse<R> handle(WebRequest<B> request);
}
