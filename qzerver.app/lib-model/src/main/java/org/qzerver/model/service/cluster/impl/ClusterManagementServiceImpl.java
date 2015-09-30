package org.qzerver.model.service.cluster.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.AbstractServiceException;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.paging.Extraction;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.qzerver.model.dao.cluster.ClusterGroupDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.cluster.exception.ClusterGroupUsed;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ClusterManagementServiceImpl implements ClusterManagementService {

    @NotNull
    private ClusterGroupDao clusterGroupDao;

    @NotNull
    private ScheduleJobDao scheduleJobDao;

    @NotNull
    private BusinessEntityDao businessEntityDao;

    @Override
    @Transactional(readOnly = true)
    public List<ClusterGroup> findAllGroups(Extraction extraction) {
        return clusterGroupDao.findAll(extraction);
    }

    @Override
    public ClusterGroup createGroup(String name) {
        Preconditions.checkNotNull(name);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName(name);
        clusterGroup.setRollingIndex(0);

        businessEntityDao.save(clusterGroup);

        return clusterGroup;
    }

    @Override
    public ClusterGroup modifyGroup(long clusterGroupId, String name) {
        Preconditions.checkNotNull(name);

        ClusterGroup clusterGroup = businessEntityDao.lockById(ClusterGroup.class, clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        clusterGroup.setName(name);

        return clusterGroup;
    }

    @Override
    public void deleteGroup(long clusterGroupId) throws AbstractServiceException {
        List<ScheduleJob> jobs = scheduleJobDao.findAllByClusterGroup(clusterGroupId);
        if (CollectionUtils.isNotEmpty(jobs)) {
            throw new ClusterGroupUsed();
        }

        businessEntityDao.deleteById(ClusterGroup.class, clusterGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public ClusterGroup findGroup(long clusterGroupId) {
        ClusterGroup clusterGroup = businessEntityDao.findById(ClusterGroup.class, clusterGroupId);
        if (clusterGroup != null) {
            Hibernate.initialize(clusterGroup.getNodes());
        }
        return clusterGroup;
    }

    @Override
    public int rollGroupIndex(long clusterGroupId) {
        ClusterGroup clusterGroup = businessEntityDao.lockById(ClusterGroup.class, clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        int currentIndex = clusterGroup.getRollingIndex();

        for (int i = currentIndex + 1, size = clusterGroup.getNodes().size(); i < size; i++) {
            ClusterNode clusterNode = clusterGroup.getNodes().get(i);
            if (clusterNode.isEnabled()) {
                clusterGroup.setRollingIndex(i);
                return i;
            }
        }

        for (int i = 0; i < currentIndex; i++) {
            ClusterNode clusterNode = clusterGroup.getNodes().get(i);
            if (clusterNode.isEnabled()) {
                clusterGroup.setRollingIndex(i);
                return i;
            }
        }

        return currentIndex;
    }

    @Override
    public ClusterNode createNode(long clusterGroupId, String address, String comment, boolean activity) {
        ClusterGroup clusterGroup = businessEntityDao.lockById(ClusterGroup.class, clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setEnabled(activity);
        clusterNode.setAddress(address);
        clusterNode.setDescription(comment);

        clusterGroup.getNodes().add(clusterNode);
        clusterNode.setGroup(clusterGroup);

        clusterGroup.reindexNodes();

        return clusterNode;
    }

    @Override
    public void deleteNode(long clusterNodeId) {
        ClusterNode clusterNode = businessEntityDao.findById(ClusterNode.class, clusterNodeId);
        if (clusterNode == null) {
            throw new MissingEntityException(ClusterNode.class, clusterNodeId);
        }

        ClusterGroup clusterGroup = clusterNode.getGroup();

        businessEntityDao.lock(clusterGroup);

        int index = clusterGroup.getNodes().indexOf(clusterNode);
        clusterGroup.getNodes().remove(index);

        clusterGroup.reindexNodes();

        if (clusterGroup.getRollingIndex() > index) {
            clusterGroup.setRollingIndex(clusterGroup.getRollingIndex() - 1);
        }
    }

    @Override
    public ClusterNode modifyNode(long clusterNodeId, String domain, String comment, boolean activity) {
        ClusterNode clusterNode = businessEntityDao.lockById(ClusterNode.class, clusterNodeId);
        if (clusterNode == null) {
            throw new MissingEntityException(ClusterNode.class, clusterNodeId);
        }

        clusterNode.setAddress(domain);
        clusterNode.setDescription(comment);
        clusterNode.setEnabled(activity);

        return clusterNode;
    }

    @Required
    public void setClusterGroupDao(ClusterGroupDao clusterGroupDao) {
        this.clusterGroupDao = clusterGroupDao;
    }

    @Required
    public void setScheduleJobDao(ScheduleJobDao scheduleJobDao) {
        this.scheduleJobDao = scheduleJobDao;
    }

    @Required
    public void setBusinessEntityDao(BusinessEntityDao businessEntityDao) {
        this.businessEntityDao = businessEntityDao;
    }
}
