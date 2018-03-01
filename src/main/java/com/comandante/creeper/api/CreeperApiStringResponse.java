package com.comandante.creeper.api;

import org.glassfish.jersey.internal.util.Base64;

public class CreeperApiStringResponse {

    public final String contents;

    public CreeperApiStringResponse(String contents) {
        this.contents = Base64.encodeAsString(contents);
    }

    public String getContents() {
        return contents;
    }
}
