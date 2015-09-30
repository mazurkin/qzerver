[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name=".vars['javax.servlet.error.exception']" type="java.lang.Throwable" --]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/layout.ftl" as layout]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/block/error.ftl" as block_error]
[@layout.root_error titleCode="Exception"]
[#escape x as x?html]

[@block_error.report
    name="Exception"
    reason="Server error"
    description="Please check the error log"
    /]

[#if renderContext.development]
    [#if .vars["javax.servlet.error.exception"]??]
        [@block_error.exception
            throwable=.vars["javax.servlet.error.exception"]
            /]
    [/#if]
[/#if]

[/#escape]
[/@layout.root_error]