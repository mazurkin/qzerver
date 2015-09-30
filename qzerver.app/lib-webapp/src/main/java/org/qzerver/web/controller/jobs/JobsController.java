package org.qzerver.web.controller.jobs;

import org.qzerver.web.attribute.render.ExtendedRenderContextAccessor;
import org.qzerver.web.map.MainMenuItem;
import org.qzerver.web.map.SiteMap;
import org.qzerver.web.map.SiteViews;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class JobsController {

    private ExtendedRenderContextAccessor extendedRenderContextAccessor;

    @RequestMapping(value = SiteMap.JOBS, method = RequestMethod.GET)
    public String handle(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception
    {
        extendedRenderContextAccessor.setMainMenuItem(request, MainMenuItem.JOBS);

        return SiteViews.JOBS;
    }

    @Required
    public void setExtendedRenderContextAccessor(ExtendedRenderContextAccessor extendedRenderContextAccessor) {
        this.extendedRenderContextAccessor = extendedRenderContextAccessor;
    }
}
