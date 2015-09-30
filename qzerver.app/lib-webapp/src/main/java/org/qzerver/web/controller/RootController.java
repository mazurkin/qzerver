package org.qzerver.web.controller;

import org.qzerver.web.map.SiteMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class RootController {

    @RequestMapping(value = SiteMap.ROOT, method = RequestMethod.GET)
    public View handle(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception
    {
        return new RedirectView(SiteMap.JOBS, true);
    }

}
