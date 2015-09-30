package org.qzerver.model.domain.entities.base;

import com.gainmatrix.lib.business.entity.AbstractBusinessEntity;
import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.qzerver.model.domain.business.BusinessModelVersionHolder;

public abstract class AbstractApplicationEntity extends AbstractBusinessEntity<Long> {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    @Override
    public int getCurrentBusinessModelVersion() {
        return BusinessModelVersionHolder.VERSION;
    }

}
