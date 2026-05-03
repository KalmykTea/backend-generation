package com.example.generation.repositories;

import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.Account;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> withFilters(TransactionFilterRequest filters, Long userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by customer ownership: customer must be owner of either fromAccount or toAccount
            Join<Transaction, Account> fromAccountJoin = root.join("fromAccount", jakarta.persistence.criteria.JoinType.LEFT);
            Join<Transaction, Account> toAccountJoin = root.join("toAccount", jakarta.persistence.criteria.JoinType.LEFT);

            Predicate isFromAccountOwner = criteriaBuilder.equal(fromAccountJoin.get("user").get("id"), userId);
            Predicate isToAccountOwner = criteriaBuilder.equal(toAccountJoin.get("user").get("id"), userId);
            predicates.add(criteriaBuilder.or(isFromAccountOwner, isToAccountOwner));

            if (filters.startDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), LocalDateTime.of(filters.startDate(), LocalTime.MIN)));
            }
            if (filters.endDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), LocalDateTime.of(filters.endDate(), LocalTime.MAX)));
            }

            if (filters.amountEq() != null) {
                predicates.add(criteriaBuilder.equal(root.get("amount"), filters.amountEq()));
            } else {
                if (filters.amountLt() != null) {
                    predicates.add(criteriaBuilder.lessThan(root.get("amount"), filters.amountLt()));
                }
                if (filters.amountGt() != null) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("amount"), filters.amountGt()));
                }
            }

            if (filters.iban() != null && !filters.iban().isBlank()) {
                Predicate isFromIban = criteriaBuilder.equal(fromAccountJoin.get("iban"), filters.iban());
                Predicate isToIban = criteriaBuilder.equal(toAccountJoin.get("iban"), filters.iban());
                predicates.add(criteriaBuilder.or(isFromIban, isToIban));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
