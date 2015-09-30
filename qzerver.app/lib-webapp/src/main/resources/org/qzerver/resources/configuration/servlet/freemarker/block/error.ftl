[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]

[#-- Error block --]
[#macro report name reason description]
<div class="row">
    <div class="span8 offset2">
        <div class="page-header">
            <h1>${name} <small>${reason}</small></h1>
            <p class="text-error">${description}</p>
        </div>
        [#nested]
        <p>
            <a class="btn btn-primary" href="[@system.url "/" /]">Return to the main page</a>
        </p>
    </div>
</div>
[/#macro]

[#-- Error exception --]
[#macro exception throwable]
<div class="row">
    <div class="span8 offset2">
        <div style="height: 2em">&nbsp;</div>
        <p><strong>${throwable.class.name}</strong></p>
        [#if throwable.message??]
            <p>${throwable.message}</p>
        [/#if]
        <pre style="font-size: 80%">[@exceptionDump throwable=throwable /]</pre>
    </div>
</div>
[/#macro]

[#-- Output exception dump
 * exception - instance of java.lang.Throwable
--]
[#macro exceptionDump throwable]
[#list throwable.stackTrace as stackTraceElement]
${stackTraceElement.className?html}#${stackTraceElement.methodName?html} [#if stackTraceElement.fileName??]
    (${stackTraceElement.fileName?html}:${stackTraceElement.lineNumber?c})[/#if]
[/#list]
[/#macro]