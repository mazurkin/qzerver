package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.web.attribute.render.RenderContext;
import com.gainmatrix.lib.web.attribute.render.RenderContextAttributePublisher;
import com.google.common.base.Preconditions;
import org.qzerver.web.map.MainMenuItem;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

public class ExtendedRenderContextAttributePublisher extends RenderContextAttributePublisher
    implements ExtendedRenderContextAccessor
{

    @NotNull
    private String host;

    @NotNull
    private String url;

    private boolean development;

    private String applicationName;

    @Override
    protected RenderContext createRenderContext() {
        return new ExtendedRenderContext();
    }

    @Override
    protected void publishRenderContext(RenderContext renderContext) {
        super.publishRenderContext(renderContext);

        ExtendedRenderContext extendedRenderContext = (ExtendedRenderContext) renderContext;
        extendedRenderContext.setUrl(url);
        extendedRenderContext.setHost(host);
        extendedRenderContext.setDevelopment(development);
        extendedRenderContext.setMainMenuItem(MainMenuItem.NONE);
        extendedRenderContext.setApplicationName(applicationName);
    }

    @Override
    public void setMainMenuItem(HttpServletRequest request, MainMenuItem mainMenuItem) {
        Preconditions.checkNotNull(mainMenuItem, "Menu item is null");

        ExtendedRenderContext context = (ExtendedRenderContext) resolve(request);
        context.setMainMenuItem(mainMenuItem);
    }

    @Required
    public void setUrl(String url) {
        this.url = url;
    }

    @Required
    public void setHost(String host) {
        this.host = host;
    }

    public void setDevelopment(boolean development) {
        this.development = development;
    }

    @Required
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
