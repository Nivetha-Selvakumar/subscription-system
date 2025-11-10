package com.subscription.subscription_system.dto;


import java.util.List;

public class PaginatedResponse<T> {
    private List<T> userDetails;
    private long totalCount;

    public PaginatedResponse(List<T> userDetails, long totalCount) {
        this.userDetails = userDetails;
        this.totalCount = totalCount;
    }

    public List<T> getUserDetails() {
        return userDetails;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
