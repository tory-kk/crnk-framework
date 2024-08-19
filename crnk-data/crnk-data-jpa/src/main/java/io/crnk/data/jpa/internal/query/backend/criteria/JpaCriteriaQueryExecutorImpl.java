package io.crnk.data.jpa.internal.query.backend.criteria;

import io.crnk.data.jpa.internal.query.AbstractQueryExecutorImpl;
import io.crnk.data.jpa.internal.query.QueryUtil;
import io.crnk.data.jpa.internal.query.backend.querydsl.ObjectArrayTupleImpl;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQueryExecutor;
import io.crnk.meta.model.MetaDataObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JpaCriteriaQueryExecutorImpl<T> extends AbstractQueryExecutorImpl<T> implements JpaCriteriaQueryExecutor<T> {

    private CriteriaQuery<T> query;

    public JpaCriteriaQueryExecutorImpl(EntityManager em, MetaDataObject meta, CriteriaQuery<T> criteriaQuery,
                                        int numAutoSelections, Map<String, Integer> selectionBindings) {
        super(em, meta, numAutoSelections, selectionBindings);

        this.query = criteriaQuery;
    }

    public CriteriaQuery<T> getQuery() {
        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TypedQuery<T> getTypedQuery() {
        return (TypedQuery<T>) setupQuery(em.createQuery(query));
    }

    @Override
    protected boolean isCompoundSelection() {
        return query.getSelection().isCompoundSelection();
    }

    @Override
    protected boolean isDistinct() {
        return query.isDistinct();
    }

    @Override
    protected boolean hasManyRootsFetchesOrJoins() {
        return QueryUtil.hasManyRootsFetchesOrJoins(query);
    }

    @Override
    public long getTotalRowCount() {
		final Set<Root<?>> roots = query.getRoots();
		if (roots.size() != 1) {
			throw new IllegalStateException("cannot compute totalRowCount in case of multiple query roots");
		}
		if (!query.getGroupList().isEmpty()) {
			throw new IllegalStateException("cannot compute totalRowCount for grouped queries");
		}

		final SqmSelectStatement<Long> countQuery = ((SqmSelectStatement<T>) query).createCountQuery();
		countQuery.distinct(isDistinct());

		final TypedQuery<Long> countTypedQuery = em.createQuery(countQuery);
		return countTypedQuery.getSingleResult();
	}

    @Override
    public List<Tuple> getResultTuples() {
        List<?> results = executeQuery();
        List<Tuple> tuples = new ArrayList<>();
        for (Object result : results) {
            if (result instanceof Object[]) {
                tuples.add(new CriteriaTupleImpl((Object[]) result, selectionBindings));
            } else {
                tuples.add(new ObjectArrayTupleImpl(result, selectionBindings));
            }
        }
        return tuples;
    }
}
