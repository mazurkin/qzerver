[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name="freemarker_static['org.qzerver.web.map.SiteMap']" type="org.qzerver.web.map.SiteMap.static" --]

[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]

[#setting locale=renderContext.locale.toString()]
[#setting time_zone=renderContext.timezone.ID]
[#setting date_format="full"]
[#setting time_format="full"]
[#setting datetime_format="full"]
[#setting url_escaping_charset="UTF-8"]

[#assign SiteMap = freemarker_static["org.qzerver.web.map.SiteMap"]]

[#-- Document declaration
--]
[#macro document]
<!DOCTYPE html>
<html lang="${renderContext.locale.language}">
[#nested]
</html>
[/#macro]

[#-- HEAD section
 * titleCode - title message code
 * cssIncludes - list of CSS resources
 * jsIncludes - list of JS resources
--]
[#macro head titleCode="" cssIncludes=[] jsIncludes=[]]
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="[@system.resource "/favicon.ico"/]" rel="icon"/>
    <link href="[@system.resource "/favicon.ico"/]" rel="shortcut icon"/>
    <link href="[@system.resource "/css/bootstrap.css"/]" rel="stylesheet" media="screen">
    <link href="[@system.resource "/css/bootstrap-responsive.css"/]" rel="stylesheet" media="all">
    <link href="[@system.resource "/css/style.css"/]" rel="stylesheet" media="all"/>
    [#list cssIncludes as cssInclude]
    <link href="[@system.resource "${cssInclude}"/]" rel="stylesheet" media="all"/>
    [/#list]
    [#list jsIncludes as jsInclude]
    <script src="[@system.resource "${jsInclude}"/]"></script>
    [/#list]
    [#nested]
    <title>${renderContext.applicationName?html}[#if titleCode?has_content] - [@system.msg titleCode/][/#if]</title>
</head>
[/#macro]

[#-- BODY section
--]
[#macro body]
<body>
[#nested]
[#if renderContext.development]
<script src="[@system.resource "/js/jquery-1.8.3.js"/]"></script>
<script src="[@system.resource "/js/bootstrap.js"/]"></script>
[#else]
<script src="[@system.resource "/js/jquery-1.8.3.min.js"/]"></script>
<script src="[@system.resource "/js/bootstrap.min.js"/]"></script>
[/#if]
</body>
[/#macro]

[#-- Navigation menu
--]
[#macro navbar]
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="[@system.url "/" /]">QZERVER</a>
            <ul class="nav">
                <li[#if renderContext.mainMenuItem == 'JOBS'] class="active"[/#if]><a href="[@system.url SiteMap.JOBS /]">Jobs</a></li>
                <li[#if renderContext.mainMenuItem == 'CLUSTERS'] class="active"[/#if]><a href="[@system.url SiteMap.CLUSTER_GROUP_EXPLORE /]">Clusters</a></li>
                <li[#if renderContext.mainMenuItem == 'EXECUTIONS'] class="active"[/#if]><a href="[@system.url SiteMap.EXECUTIONS /]">Executions</a></li>
                <li[#if renderContext.mainMenuItem == 'SYSTEM'] class="active"[/#if]><a href="[@system.url SiteMap.SYSTEM_STATE /]">System</a></li>
            </ul>
        </div>
    </div>
</div>
[/#macro]

[#-- Root template for common pages
 * titleCode - title message code
 * cssIncludes - list of CSS resources
 * jsIncludes - list of JS resources
--]
[#macro root_main titleCode="" cssIncludes=[] jsIncludes=[]]
[@document]
[@head titleCode cssIncludes jsIncludes]
[/@head]
[@body]
[@navbar/]
<div class="container">
[#nested]
</div>
[/@body]
[/@document]
[/#macro]

[#-- Root template for error pages
 * titleCode - title message code
 * cssIncludes - list of CSS resources
 * jsIncludes - list of JS resources
--]
[#macro root_error titleCode="" cssIncludes=[] jsIncludes=[]]
[@document]
[@head titleCode cssIncludes jsIncludes]
[/@head]
[@body]
[@navbar/]
<div class="container">
[#nested]
</div>
[/@body]
[/@document]
[/#macro]

