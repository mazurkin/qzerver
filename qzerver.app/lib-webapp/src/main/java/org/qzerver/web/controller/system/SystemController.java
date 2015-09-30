package org.qzerver.web.controller.system;

import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.qzerver.web.attribute.render.ExtendedRenderContextAccessor;
import org.qzerver.web.map.MainMenuItem;
import org.qzerver.web.map.SiteMap;
import org.qzerver.web.map.SiteViews;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping
public class SystemController {

    private ExtendedRenderContextAccessor extendedRenderContextAccessor;

    private QuartzManagementService quartzManagementService;

    @RequestMapping(value = SiteMap.SYSTEM_STATE)
    public String state(
        HttpServletRequest request,
        Model viewModel
    ) throws Exception
    {
        extendedRenderContextAccessor.setMainMenuItem(request, MainMenuItem.SYSTEM);

        SystemStateModel model = new SystemStateModel();
        viewModel.addAttribute("model", model);

        boolean schedulerActive = quartzManagementService.isSchedulerActive();
        model.setScheduleActive(schedulerActive);

        return SiteViews.SYSTEM_DASHBOARD;
    }

    @RequestMapping(value = SiteMap.SYSTEM_CONTROL)
    public View control(
        @RequestParam
        boolean activity
    ) throws Exception
    {
        if (activity) {
            quartzManagementService.enableScheduler();
        } else {
            quartzManagementService.disableScheduler();
        }

        return new RedirectView(SiteMap.SYSTEM_STATE, true);
    }

    @Required
    public void setExtendedRenderContextAccessor(ExtendedRenderContextAccessor extendedRenderContextAccessor) {
        this.extendedRenderContextAccessor = extendedRenderContextAccessor;
    }

    @Required
    public void setQuartzManagementService(QuartzManagementService quartzManagementService) {
        this.quartzManagementService = quartzManagementService;
    }
}
