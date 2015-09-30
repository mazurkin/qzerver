[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/layout.ftl" as layout]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/core/helpers.ftl" as helpers]
[@layout.root_main titleCode="FreeMarker sample page"]
[#escape x as x?html]

<div class="row">

    <div class="span12">

    <p>{${helpers.getText(renderContext.now)}}</p>

    [#assign propName="url"]
    <p>${renderContext[propName]}</p>

    <p>[#if helpers.isEqualText(renderContext.timezone.ID, "UTC")]YES![/#if]</p>

    <p>[@system.msg "msg.test"/]</p>

    <p>[@system.msgParams code="msg.test2" params=["tail", "dog", 222]/]</p>

    <p>[@system.msgParams "msg.test"/]</p>

    <p>${requestContext.getContextUrl("/index")}</p>

    <p>${requestContext.getMessage("fefefe")}</p>

    <p>${requestContext.getDefaultHtmlEscape()?string}</p>

    <p>[@system.url "/index"/]</p>

    <p>[@system.urlParams "/member/{member}/picture/{picture}?mode={mode}"
        { "member": "ssss", "picture": "тест", "mode": "ура" } /]</p>

    <p>
        [#transform html_escape]
        a < b
        Romeo & Juliet
        [/#transform]
    </p>

    <p>${renderContext.now?datetime?string}</p>

    <p>timezone: ${renderContext.timezone.ID}</p>

    <p>locale: ${renderContext.locale.toString()}</p>

    <p>language: ${renderContext.locale.language}</p>

    <p>country: ${renderContext.locale.country}</p>

    <script type="text/javascript">
    //<![CDATA[
    function checkTwo(a, b) {
        if (a > b) {
            var div = document.createElement("div");
            var text = document.createTextNode("Hi there and greetings!");
            div.appendChild(text);
            document.body.appendChild(div);
        }
    }
    checkTwo(3, 5);
    //]]>
    </script>

        <pre>
            frwf
            war
            fwr
            fqw
            rf
            wqr
            fqwrf
            qwr
            f
            qwr
            fqw
            fq
            rf
            qwr
            f
            q
            rf
            q
            wrf
            qwrf
            qwr
            f
            qrf
            q
            rf
            qwr
            fq
            rf
            qr
            fq
            wf
            qwr
        </pre>

    </div>
</div>

[/#escape]
[/@layout.root_main]