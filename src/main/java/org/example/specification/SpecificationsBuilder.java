package org.example.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SpecificationsBuilder<T> {

    private final List<SearchCriteria> params = new ArrayList<>();

    public SpecificationsBuilder<T> with(String key, String operation, Object value, boolean orPredicate) {
        params.add(new SearchCriteria(key, operation, value, orPredicate));
        return this;
    }

    public Specification<T> build() {
        if (params.isEmpty()) return null;

        Specification<T> spec = new GenericSpecification<>(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            Specification<T> next = new GenericSpecification<>(params.get(i));
            spec = params.get(i).isOrPredicate() ? Specification.where(spec).or(next)
                    : Specification.where(spec).and(next);
        }
        return spec;
    }
}