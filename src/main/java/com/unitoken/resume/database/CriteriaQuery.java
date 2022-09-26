package com.unitoken.resume.database;

/**
 * Base criteria query.
 *
 * @author liaoxuefeng
 *
 * @param <T> Generic type.
 */
abstract class CriteriaQuery<T> {

    protected final Criteria<T> criteria;

    CriteriaQuery(Criteria<T> criteria) {
        this.criteria = criteria;
    }

    String sql() {
        return criteria.sql();
    }
}
