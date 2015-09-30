[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name="exception" type="java.lang.Exception" --]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/layout.ftl" as layout]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/block/error.ftl" as block_error]
[@layout.root_error titleCode="ERROR #5XX"]
[#escape x as x?html]

[@block_error.report
    name="#5XX"
    reason="Server error"
    description="Please check the error log"
    /]

[#if renderContext.development]
    [#if exception??]
        [@block_error.exception
            throwable=exception
            /]
    [/#if]
[/#if]

[/#escape]
[/@layout.root_error]