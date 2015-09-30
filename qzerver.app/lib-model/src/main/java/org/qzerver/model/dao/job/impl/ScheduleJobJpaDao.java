package org.qzerver.model.dao.job.impl;

import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup_;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.domain.entities.job.ScheduleJob_;
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
public class ScheduleJobJpaDao implements ScheduleJobDao {

    private EntityManager entityManager;

    @Override
    public List<ScheduleJob> findAllByClusterGroup(long clusterGroupId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ScheduleJob> criteriaQuery = criteriaBuilder.createQuery(ScheduleJob.class);
        Root<ScheduleJob> root = criteriaQuery.from(ScheduleJob.class);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.where(
            criteriaBuilder.equal(root.get(ScheduleJob_.cluster).get(ClusterGroup_.id), clusterGroupId)
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ScheduleJob> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
