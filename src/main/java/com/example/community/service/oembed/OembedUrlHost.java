package com.example.community.service.oembed;

import java.net.URI;

final class OembedUrlHost {
    private OembedUrlHost() {
    }

    static String extract(String url) {
        try {
            String host = URI.create(url).getHost();
            return host == null ? null : host.toLowerCase();
        } catch (IllegalArgumentException e) { // 커스텀 예외로 처리할지 고민
            return null;
        }
    }
}
