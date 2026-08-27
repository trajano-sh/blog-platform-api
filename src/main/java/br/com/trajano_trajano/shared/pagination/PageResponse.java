package br.com.trajano_trajano.shared.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<?> content, int page, int size, long totalElements, int totalPages, boolean last) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
