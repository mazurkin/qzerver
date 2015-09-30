package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.web.attribute.render.RenderContext;
import org.qzerver.web.map.MainMenuItem;

/**
 * Context for rendering web page
 */
public class ExtendedRenderContext extends RenderContext {

    private String host;

    private String url;

    private boolean development;

    private String applicationName;

    private MainMenuItem mainMenuItem;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDevelopment() {
        return development;
    }

    public void setDevelopment(boolean development) {
        this.development = development;
    }

    public MainMenuItem getMainMenuItem() {
        return mainMenuItem;
    }

    public void setMainMenuItem(MainMenuItem mainMenuItem) {
        this.mainMenuItem = mainMenuItem;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
