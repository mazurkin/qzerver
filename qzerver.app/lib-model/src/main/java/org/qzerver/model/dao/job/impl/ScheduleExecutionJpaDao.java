package org.qzerver.model.dao.job.impl;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecution_;
import org.qzerver.model.domain.entities.job.ScheduleJob_;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import java.util.Date;
import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public class ScheduleExecutionJpaDao implements ScheduleExecutionDao {

    private EntityManager entityManager;

    @Override
    public List<ScheduleExecution> findAll(Extraction extraction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ScheduleExecution> criteriaQuery = criteriaBuilder.createQuery(ScheduleExecution.class);
        Root<ScheduleExecution> root = criteriaQuery.from(ScheduleExecution.class);
        root.fetch(ScheduleExecution_.job, JoinType.LEFT);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.orderBy(
            criteriaBuilder.desc(root.get(ScheduleExecution_.fired))
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ScheduleExecution> typedQuery = entityManager.createQuery(criteriaQuery);
        if (Extraction.isRequired(extraction)) {
            typedQuery.setFirstResult(extraction.getOffset());
            typedQuery.setMaxResults(extraction.getCount());
        }

        return typedQuery.getResultList();
    }

    @Override
    public List<ScheduleExecution> findFinished(Extraction extraction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ScheduleExecution> criteriaQuery = criteriaBuilder.createQuery(ScheduleExecution.class);
        Root<ScheduleExecution> root = criteriaQuery.from(ScheduleExecution.class);
        root.fetch(ScheduleExecution_.job, JoinType.LEFT);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.where(
            criteriaBuilder.isNotNull(root.get(ScheduleExecution_.finished))
        );

        criteriaQuery.orderBy(
            criteriaBuilder.desc(root.get(ScheduleExecution_.fired))
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ScheduleExecution> typedQuery = entityManager.createQuery(criteriaQuery);
        if (Extraction.isRequired(extraction)) {
            typedQuery.setFirstResult(extraction.getOffset());
            typedQuery.setMaxResults(extraction.getCount());
        }

        return typedQuery.getResultList();
    }

    @Override
    public List<ScheduleExecution> findEngaged(Extraction extraction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ScheduleExecution> criteriaQuery = criteriaBuilder.createQuery(ScheduleExecution.class);
        Root<ScheduleExecution> root = criteriaQuery.from(ScheduleExecution.class);
        root.fetch(ScheduleExecution_.job, JoinType.LEFT);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.where(
            criteriaBuilder.isNull(root.get(ScheduleExecution_.finished))
        );

        criteriaQuery.orderBy(
            criteriaBuilder.desc(root.get(ScheduleExecution_.fired))
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ScheduleExecution> typedQuery = entityManager.createQuery(criteriaQuery);
        if (Extraction.isRequired(extraction)) {
            typedQuery.setFirstResult(extraction.getOffset());
            typedQuery.setMaxResults(extraction.getCount());
        }

        return typedQuery.getResultList();
    }

    @Override
    public List<ScheduleExecution> findByJob(long scheduleJobId, Extraction extraction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ScheduleExecution> criteriaQuery = criteriaBuilder.createQuery(ScheduleExecution.class);
        Root<ScheduleExecution> root = criteriaQuery.from(ScheduleExecution.class);

        // CHECKSTYLE-OFF: NestedMethodCall
        criteriaQuery.where(
            criteriaBuilder.equal(root.get(ScheduleExecution_.job).get(ScheduleJob_.id), scheduleJobId)
        );

        criteriaQuery.orderBy(
            criteriaBuilder.desc(root.get(ScheduleExecution_.fired))
        );
        // CHECKSTYLE-ON: NestedMethodCall

        TypedQuery<ScheduleExecution> typedQuery = entityManager.createQuery(criteriaQuery);
        if (Extraction.isRequired(extraction)) {
            typedQuery.setFirstResult(extraction.getOffset());
            typedQuery.setMaxResults(extraction.getCount());
        }

        return typedQuery.getResultList();
    }

    @Override
    public int deleteExpired(Date expiration) {
        Query query = entityManager.createNamedQuery("ScheduleExecution.deleteExpired");
        query.setParameter("expiration", expiration);

        return query.executeUpdate();
    }

    @Override
    public int detachJob(long scheduleJobId) {
        Query query = entityManager.createNamedQuery("ScheduleExecution.detachJob");
        query.setParameter("scheduleJobId", scheduleJobId);

        return query.executeUpdate();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
