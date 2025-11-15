package com.subscription.subscription_system.utils;

import com.subscription.subscription_system.enumuration.EnumStatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
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


    public static Query buildFeedbackQuery(String search, String filterBy, String sortBy, String sortDir) {

        Query query = new Query();
        Criteria criteria = new Criteria();

        List<Criteria> criteriaList = new ArrayList<>();

        // 1️⃣ Search by comments or ratings
        if (search != null && !search.trim().isEmpty()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("comments").regex(search, "i"),
                    Criteria.where("ratings").regex(search, "i")
            ));
        }

        // 2️⃣ Apply filters
        if (filterBy != null && !filterBy.trim().isEmpty()) {

            String[] filters = filterBy.split(",");

            for (String filter : filters) {

                // split into exactly 2 parts → key & full value
                String[] parts = filter.split(":", 2);
                if (parts.length != 2) continue;

                String key = parts[0];
                String value = parts[1];

                switch (key) {
                    case "ratings":
                        criteriaList.add(Criteria.where("ratings")
                                .is(Integer.parseInt(value)));
                        break;

                    case "status":
                        try {
                            EnumStatusType statusType = EnumStatusType.valueOf(value.toUpperCase());
                            criteriaList.add(Criteria.where("status").is(statusType));
                        } catch (Exception e) {
                            log.warn("Invalid status filter {}", value);
                        }
                        break;

                    case "startDate":
                        LocalDateTime start = LocalDate.parse(value.substring(0, 10))
                                .atStartOfDay();
                        criteriaList.add(Criteria.where("createdAt").gte(start));
                        break;

                    case "endDate":
                        LocalDateTime end = LocalDate.parse(value.substring(0, 10))
                                .atTime(LocalTime.MAX);
                        criteriaList.add(Criteria.where("createdAt").lte(end));
                        break;
                }
            }

        }

        // 3️⃣ Exclude Deleted
        criteriaList.add(Criteria.where("status").ne(EnumStatusType.DELETE));

        // Combine all criteria
        if (!criteriaList.isEmpty()) {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        query.addCriteria(criteria);

        // 4️⃣ Sorting
        Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        if (sortBy != null && !sortBy.isBlank()) {
            query.with(Sort.by(direction, sortBy));
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return query;
    }

    public static Query buildSupportTicketQuery(
            String userId,       // null means admin → no user restriction
            String search,
            String filterBy,
            String sortBy,
            String sortDir
    ) {
        Query query = new Query();
        List<Criteria> andCriteria = new ArrayList<>();

        // 1️⃣ If userId is NOT null → normal user → filter by owner
        if (userId != null) {
            andCriteria.add(Criteria.where("user_id").is(userId));
        }

        // 2️⃣ Search filter
        if (search != null && !search.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("issue_desc").regex(search, "i"),
                    Criteria.where("subject").regex(search, "i"),
                    Criteria.where("ticket_status").regex(search, "i"),
                    Criteria.where("status").regex(search, "i")
            );
            andCriteria.add(searchCriteria);
        }

        // 3️⃣ filterBy logic (ticketStatus, status)
        if (filterBy != null && !filterBy.trim().isEmpty()) {

            String[] filters = filterBy.split(",");

            for (String f : filters) {
                if (!f.contains(":")) continue;

                String[] parts = f.split(":");
                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.isEmpty() || value.isEmpty()) continue;

                switch (key.toLowerCase()) {
                    case "ticketstatus":
                        andCriteria.add(Criteria.where("ticket_status")
                                .regex("^" + value + "$", "i"));
                        break;

                    case "status":
                        andCriteria.add(Criteria.where("status")
                                .regex("^" + value + "$", "i"));
                        break;
                }
            }
        }

        // 4️⃣ Exclude soft-deleted tickets
        andCriteria.add(Criteria.where("status").ne(EnumStatusType.DELETE));

        // 5️⃣ Apply all criteria
        if (!andCriteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        }

        // 6️⃣ Sorting
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        query.with(Sort.by(direction, sortBy != null ? sortBy : "created_at"));

        return query;
    }

    /**
     * Build Mongo query for Subscription Entity
     */
    public static Query buildSubscriptionQuery(
            String userId,      // null → admin can see all, userId != null → filter only user's subscriptions
            String search,
            String filterBy,    // status:ACTIVE,currentSubStatus:ACTIVE,paymentStatus:PAID
            String sortBy,
            String sortDir
    ) {

        Query query = new Query();
        List<Criteria> andCriteria = new ArrayList<>();

        // 1️⃣ If user → filter subscriptions owned by that user
        if (userId != null) {
            andCriteria.add(Criteria.where("user_id").is(userId));
        }

        // 2️⃣ Search filter
        if (search != null && !search.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("plan.plan_name").regex(search, "i"),
                    Criteria.where("plan.plan_type").regex(search, "i"),
                    Criteria.where("payment_status").regex(search, "i"),
                    Criteria.where("current_sub_status").regex(search, "i"),
                    Criteria.where("amount").regex(search, "i")
            );
            andCriteria.add(searchCriteria);
        }

        // 3️⃣ filterBy key:value,key:value
        if (filterBy != null && !filterBy.trim().isEmpty()) {

            String[] filters = filterBy.split(",");

            for (String f : filters) {

                if (!f.contains(":")) continue;

                String[] parts = f.split(":");
                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.isEmpty() || value.isEmpty()) continue;

                switch (key.toLowerCase()) {

                    case "paymentstatus":
                        andCriteria.add(Criteria.where("payment_status")
                                .regex("^" + value + "$", "i"));
                        break;

                    case "currentsubstatus":
                        andCriteria.add(Criteria.where("current_sub_status")
                                .regex("^" + value + "$", "i"));
                        break;

                    case "status":
                        andCriteria.add(Criteria.where("status")
                                .regex("^" + value + "$", "i"));
                        break;

                    case "plantype":
                        andCriteria.add(Criteria.where("plan.plan_type")
                                .regex("^" + value + "$", "i"));
                        break;

                    case "planname":
                        andCriteria.add(Criteria.where("plan.plan_name")
                                .regex("^" + value + "$", "i"));
                        break;
                }
            }
        }

        // 4️⃣ Exclude soft-deleted subscriptions
        andCriteria.add(Criteria.where("status").ne(EnumStatusType.DELETE));

        // 5️⃣ Apply all criteria (only once)
        if (!andCriteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        }

        // 6️⃣ Sorting
        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        query.with(Sort.by(direction, sortBy != null ? sortBy : "created_at"));

        return query;
    }

}
