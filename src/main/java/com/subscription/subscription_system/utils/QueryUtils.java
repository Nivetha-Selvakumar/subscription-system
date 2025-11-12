package com.subscription.subscription_system.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

        // 🔍 1️⃣ Search filter
        if (search != null && !search.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("plan_name").regex(search, "i"),
                    Criteria.where("plan_type").regex(search, "i"),
                    Criteria.where("description").regex(search, "i"),
                    Criteria.where("status").regex(search, "i")
            );
            andCriteria.add(searchCriteria);
        }

        // 🎯 2️⃣ Filters (plan_type, status, costMin, costMax)
        if (filterBy != null && !filterBy.trim().isEmpty()) {
            Double minCost = null;
            Double maxCost = null;

            String[] filters = filterBy.split(",");
            for (String f : filters) {
                if (!f.contains(":")) continue;

                String[] parts = f.split(":");
                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.isEmpty() || value.isEmpty()) continue;

                switch (key.toLowerCase()) {
                    case "costmin":
                        try {
                            minCost = Double.parseDouble(value);
                        } catch (NumberFormatException ignored) {
                        }
                        break;

                    case "costmax":
                        try {
                            maxCost = Double.parseDouble(value);
                        } catch (NumberFormatException ignored) {
                        }
                        break;

                    default:
                        // ✅ match backend MongoDB field naming convention
                        String mongoField = key
                                .replace("planname", "plan_name")
                                .replace("plantype", "plan_type")
                                .replace("status", "status");

                        andCriteria.add(Criteria.where(mongoField).regex("^" + value + "$", "i"));
                        break;
                }
            }

            // ✅ Apply cost range filter — correct field name is "cost"
            if (minCost != null && maxCost != null) {
                andCriteria.add(Criteria.where("cost").gte(minCost).lte(maxCost));
            } else if (minCost != null) {
                andCriteria.add(Criteria.where("cost").gte(minCost));
            } else if (maxCost != null) {
                andCriteria.add(Criteria.where("cost").lte(maxCost));
            }
        }

        // 🚫 3️⃣ Exclude deleted plans
        andCriteria.add(Criteria.where("status").not().regex("^delete$", "i"));

        // ✅ 4️⃣ Combine all valid criteria
        if (!andCriteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        }

        // 🔄 5️⃣ Sorting
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        query.with(Sort.by(direction, sortBy != null ? sortBy : "plan_name"));

        return query;
    }
}
