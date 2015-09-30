package org.qzerver.model.dao.job.impl;

import org.qzerver.model.dao.job.ScheduleActionDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Transactional(propagation = Propagation.MANDATORY)
public class ScheduleActionJpaDao implements ScheduleActionDao {

    private EntityManager entityManager;

    @Override
    public int deleteOrphaned() {
        Query query = entityManager.createNamedQuery("ScheduleAction.removeOrphaned");

        return query.executeUpdate();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
