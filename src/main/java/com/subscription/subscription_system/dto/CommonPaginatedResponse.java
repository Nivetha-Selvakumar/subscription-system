package com.subscription.subscription_system.dto;

import java.util.List;

public class CommonPaginatedResponse<T> {
    private List<T> data;
    private long totalCount;

    public CommonPaginatedResponse(List<T> data, long totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return data;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
