package org.qzerver.web.controller.clusters;

import com.gainmatrix.lib.paging.Extraction;
import com.gainmatrix.lib.paging.pager.impl.ForecastPager;
import com.gainmatrix.lib.paging.strategy.PagerSelectorCallback;
import com.gainmatrix.lib.paging.strategy.PagerSelectorStrategy;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.web.attribute.render.ExtendedRenderContextAccessor;
import org.qzerver.web.map.MainMenuItem;
import org.qzerver.web.map.SiteMap;
import org.qzerver.web.map.SiteViews;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@Controller
@RequestMapping
public class ClusterGroupExploreController {

    private static final int PAGER_FORECAST = 3;

    private static final int PAGER_PAGE_LIMIT = 100;

    private static final int PAGER_PAGE_SIZE = 10;

    private ExtendedRenderContextAccessor extendedRenderContextAccessor;

    private ClusterManagementService clusterManagementService;

    private PagerSelectorStrategy pagerSelectorStrategy;

    @RequestMapping(value = SiteMap.CLUSTER_GROUP_EXPLORE, method = RequestMethod.GET)
    public String handle(
        HttpServletRequest request,
        HttpServletResponse response,
        Model viewModel,
        @RequestParam(required = false, defaultValue = "1")
        int page
    ) throws Exception
    {
        extendedRenderContextAccessor.setMainMenuItem(request, MainMenuItem.CLUSTERS);

        ForecastPager pager = new ForecastPager();
        pager.setForecast(PAGER_FORECAST);
        pager.setPageLimit(PAGER_PAGE_LIMIT);
        pager.setPageSize(PAGER_PAGE_SIZE);
        pager.setPage(page);

        List<ClusterGroup> clusterGroups = pagerSelectorStrategy.select(pager,
            new PagerSelectorCallback<ClusterGroup>() {
                @Override
                public List<ClusterGroup> select(Extraction extraction) {
                    return clusterManagementService.findAllGroups(extraction);
                }
            }
        );

        ClusterGroupExploreModel model = new ClusterGroupExploreModel();
        model.setClusterGroups(clusterGroups);
        model.setPager(pager);

        viewModel.addAttribute("model", model);

        return SiteViews.CLUSTER_GROUP_EXPLORE;
    }

    @Required
    public void setExtendedRenderContextAccessor(ExtendedRenderContextAccessor extendedRenderContextAccessor) {
        this.extendedRenderContextAccessor = extendedRenderContextAccessor;
    }

    @Required
    public void setClusterManagementService(ClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    @Required
    public void setPagerSelectorStrategy(PagerSelectorStrategy pagerSelectorStrategy) {
        this.pagerSelectorStrategy = pagerSelectorStrategy;
    }

}
