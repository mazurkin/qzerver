[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name="freemarker_static['org.qzerver.web.map.SiteMap']" type="org.qzerver.web.map.SiteMap.static" --]
[#-- @ftlvariable name="model" type="org.qzerver.web.controller.system.SystemStateModel" --]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/layout.ftl" as layout]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/core/helpers.ftl" as helpers]
[@layout.root_main titleCode="System dashboard"]
[#escape x as x?html]

[#assign SiteMap = freemarker_static["org.qzerver.web.map.SiteMap"]]

<div class="row">
    <div class="span12">

        <h1>System</h1>

        <hr/>

        <table class="table table-condensed table-bordered">
            <tbody>
                <tr>
                    <td>Host:</td>
                    <td>${renderContext.host}</td>
                </tr>
                <tr>
                    <td>Version:</td>
                    <td>${renderContext.applicationVersion}</td>
                </tr>
                <tr>
                    <td>Name:</td>
                    <td>${renderContext.applicationName}</td>
                </tr>
                <tr>
                    <td>Url:</td>
                    <td>${renderContext.url}</td>
                </tr>
                <tr>
                    <td>State: </td>
                    <td>${model.scheduleActive?string("active", "disabled")}</td>
                </tr>
            </tbody>
        </table>

        <p>
            [#if model.scheduleActive]
                <a href="[@system.url "${SiteMap.SYSTEM_CONTROL}?activity=false"/]" class="btn btn-large btn-danger">Turn the node off</a>
            [#else]
                <a href="[@system.url "${SiteMap.SYSTEM_CONTROL}?activity=true"/]" class="btn btn-large btn-success">Turn the node on</a>
            [/#if]

        </p>

    </div>
</div>

[/#escape]
[/@layout.root_main]