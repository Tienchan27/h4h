package com.example.tms.api.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableGuard {
    private PageableGuard() {
    }

    public static Pageable guard(
            Pageable input,
            int maxSize,
            Sort defaultSort,
            Set<String> allowedSortProperties
    ) {
        int page = Math.max(0, input == null ? 0 : input.getPageNumber());
        int sizeRequested = input == null ? maxSize : input.getPageSize();
        int size = Math.min(Math.max(1, sizeRequested), maxSize);

        Sort sort = defaultSort == null ? Sort.unsorted() : defaultSort;
        if (input != null && input.getSort() != null && input.getSort().isSorted()) {
            Sort.Order first = input.getSort().stream().findFirst().orElse(null);
            if (first != null && first.getProperty() != null) {
                String prop = first.getProperty();
                if (allowedSortProperties != null && allowedSortProperties.contains(prop)) {
                    sort = Sort.by(first.getDirection(), prop);
                }
            }
        }

        return PageRequest.of(page, size, sort);
    }

    public static String sortToString(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "";
        }
        Sort.Order first = sort.stream().findFirst().orElse(null);
        if (first == null) {
            return "";
        }
        return first.getProperty() + "," + first.getDirection().name().toLowerCase();
    }
}

