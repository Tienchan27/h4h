package com.example.tms.api.dto.common;

import java.util.List;

public record SliceResponse<T>(
        List<T> items,
        boolean hasNext,
        int page,
        int size,
        String sort
) {
}

