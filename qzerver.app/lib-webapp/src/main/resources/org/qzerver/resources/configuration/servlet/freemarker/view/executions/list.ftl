[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/layout.ftl" as layout]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/core/helpers.ftl" as helpers]
[@layout.root_main titleCode="Executions"]
[#escape x as x?html]

<div class="row">
    <div class="span12">

        <h1>Executions</h1>

        <hr/>
    </div>
</div>

[/#escape]
[/@layout.root_main]