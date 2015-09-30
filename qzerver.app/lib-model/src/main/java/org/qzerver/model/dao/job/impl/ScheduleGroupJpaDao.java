package org.qzerver.model.dao.job.impl;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.dao.job.ScheduleGroupDao;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleGroup_;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public class ScheduleGroupJpaDao implements ScheduleGroupDao {

    private EntityManager entityManager;

    @Override
    public List<ScheduleGroup> findAll(Extraction extraction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ScheduleGroup> criteriaQuery = criteriaBuilder.createQuery(ScheduleGroup.class);
        Root<ScheduleGroup> root = criteriaQuery.from(ScheduleGroup.class);
        root.fetch(ScheduleGroup_.jobs, JoinType.LEFT);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.orderBy(
            criteriaBuilder.asc(root.get(ScheduleGroup_.name))
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ScheduleGroup> typedQuery = entityManager.createQuery(criteriaQuery);
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
