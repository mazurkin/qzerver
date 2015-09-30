package org.qzerver.model.domain.business;

import com.gainmatrix.lib.business.entity.BusinessModelVersionUtils;

public final class BusinessModelVersionHolder {

    /**
     * Current business model version
     * @see com.gainmatrix.lib.business.entity.BusinessModelVersionUtils#parseBusinessModelVersion(java.lang.Class)
     */
    public static final int VERSION =
        BusinessModelVersionUtils.parseBusinessModelVersion(BusinessModelVersionHolder.class);

    private BusinessModelVersionHolder() {
    }

}
