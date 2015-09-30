package org.qzerver.web.attribute.render;

import org.qzerver.web.map.MainMenuItem;

import javax.servlet.http.HttpServletRequest;

/**
 * Accessor allows to change some elements in render context on the go
 */
public interface ExtendedRenderContextAccessor {

    /**
     * Set main menu item
     * @param request Request
     * @param mainMenuItem Main menu item
     */
    void setMainMenuItem(HttpServletRequest request, MainMenuItem mainMenuItem);

}
