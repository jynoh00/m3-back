package com.example.community.common;

import com.example.community.exception.InvalidRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageRequestFactory {
    private static final int PAGE_SIZE = 10;

    private PageRequestFactory() {
    }

    public static Pageable of(int page) {
        if (page < 1) throw new InvalidRequestException(ExceptionMessage.INVALID_PAGE.getMessage());

        return PageRequest.of(page - 1, PAGE_SIZE);
    }
}
