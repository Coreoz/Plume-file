package com.coreoz.plume.file.streaming;

import org.apache.commons.fileupload.RequestContext;

import jakarta.ws.rs.container.ContainerRequestContext;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Implement RequestContext for JAX-RS {@link ContainerRequestContext}
 */
public class JaxRsFileItemIterator implements RequestContext {
    private final ContainerRequestContext request;

    public JaxRsFileItemIterator(ContainerRequestContext request) {
        this.request = request;
    }

    @Override
    public String getCharacterEncoding() {
        return StandardCharsets.UTF_8.displayName();
    }

    @Override
    public String getContentType() {
        return request.getMediaType().toString();
    }

    @Override
    public int getContentLength() {
        return request.getLength();
    }

    @Override
    public InputStream getInputStream() {
        return request.getEntityStream();
    }
}
