package org.qzerver.web.controller.clusters;

import com.gainmatrix.lib.paging.pager.Pager;
import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;

import java.io.Serializable;
import java.util.List;

public class ClusterGroupExploreModel implements Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private Pager pager;

    private List<ClusterGroup> clusterGroups;

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public List<ClusterGroup> getClusterGroups() {
        return clusterGroups;
    }

    public void setClusterGroups(List<ClusterGroup> clusterGroups) {
        this.clusterGroups = clusterGroups;
    }
}
