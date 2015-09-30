package org.qzerver.model.dao.cluster.impl;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.dao.cluster.ClusterGroupDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterGroup_;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public class ClusterGroupJpaDao implements ClusterGroupDao {

    private EntityManager entityManager;

    @Override
    public List<ClusterGroup> findAll(Extraction extraction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ClusterGroup> criteriaQuery = criteriaBuilder.createQuery(ClusterGroup.class);
        Root<ClusterGroup> root = criteriaQuery.from(ClusterGroup.class);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.orderBy(
            criteriaBuilder.asc(root.get(ClusterGroup_.name))
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ClusterGroup> typedQuery = entityManager.createQuery(criteriaQuery);
        if (Extraction.isRequired(extraction)) {
            typedQuery.setFirstResult(extraction.getOffset());
            typedQuery.setMaxResults(extraction.getCount());
        }

        return typedQuery.getResultList();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
