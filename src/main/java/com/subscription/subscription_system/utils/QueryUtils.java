package com.subscription.subscription_system.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryUtils {

    private QueryUtils() {
    }

    public static Query buildUserQuery(String search, String filterBy, String sortBy, String sortDir) {
        Query query = new Query();
        List<Criteria> andCriteria = new ArrayList<>();

        // 🔍 1️⃣ Add search
        if (search != null && !search.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(search, "i"),
                    Criteria.where("lastName").regex(search, "i"),
                    Criteria.where("email").regex(search, "i"),
                    Criteria.where("phoneNumber").regex(search, "i")
            );
            andCriteria.add(searchCriteria);
        }

        // 🎯 2️⃣ Add filter (example: role:admin,status:active)
        if (filterBy != null && !filterBy.trim().isEmpty()) {
            Arrays.stream(filterBy.split(","))
                    .map(String::trim)
                    .filter(f -> f.contains(":"))
                    .forEach(f -> {
                        String[] parts = f.split(":");
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (!key.isEmpty() && !value.isEmpty()) {
                            andCriteria.add(Criteria.where(key).regex("^" + value + "$", "i"));
                        }
                    });
        }

        // 🚫 3️⃣ Exclude users with status = "delete" (ignore case)
        andCriteria.add(Criteria.where("status").not().regex("^delete$", "i"));


        // ✅ 3️⃣ Combine all valid criteria
        if (!andCriteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        }

        // 🔄 4️⃣ Sorting
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        }
        query.with(Sort.by(direction, sortBy != null ? sortBy : "firstName"));

        return query;
    }

    public static Query buildPlanQuery(String search, String filterBy, String sortBy, String sortDir) {
        Query query = new Query();
        List<Criteria> andCriteria = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("planName").regex(search, "i"),
                    Criteria.where("planType").regex(search, "i"),
                    Criteria.where("cost").regex(search, "i")
            );
            andCriteria.add(searchCriteria);
        }

        // 🎯 2️⃣ Add filter (example: role:admin,status:active)
        if (filterBy != null && !filterBy.trim().isEmpty()) {
            Arrays.stream(filterBy.split(","))
                    .map(String::trim)
                    .filter(f -> f.contains(":"))
                    .forEach(f -> {
                        String[] parts = f.split(":");
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (!key.isEmpty() && !value.isEmpty()) {
                            andCriteria.add(Criteria.where(key).regex("^" + value + "$", "i"));
                        }
                    });

        }

        // 🚫 3️⃣ Exclude users with status = "delete" (ignore case)
        andCriteria.add(Criteria.where("status").not().regex("^delete$", "i"));

        // ✅ 3️⃣ Combine all valid criteria
        if (!andCriteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        }

        // 🔄 4️⃣ Sorting
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        }
        query.with(Sort.by(direction, sortBy != null ? sortBy : "firstName"));

        return query;
    }
}
