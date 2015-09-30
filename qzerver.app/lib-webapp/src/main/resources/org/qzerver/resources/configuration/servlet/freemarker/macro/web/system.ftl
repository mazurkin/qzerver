[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="com.gainmatrix.lib.web.attribute.render.RenderContext" --]

[#-- Output message by a specified code
 * code - message code
--]
[#macro msg code]${requestContext.getMessage(code, [], "[${code}]", false)?html}[/#macro]

[#-- Output message with message template code and parameter array
 * code - message code
 * params - list of parameters
--]
[#macro msgParams code params=[]]${requestContext.getMessage(code, params, "[${code}]", false)?html}[/#macro]

[#-- Compose a proper reference
 * relativeUrl - relative reference
--]
[#macro url relativeUrl]${requestContext.getContextUrl(relativeUrl)?html}[/#macro]

[#-- Compose a proper reference with template and parameters
 * relativeUrl - relative reference template
 * params - dictionary of parameters
--]
[#macro urlParams relativeUrl params]${requestContext.getContextUrl(relativeUrl, params)?html}[/#macro]

[#-- Compose internal versioned resource reference (js, image, css)
 * relativeUrl - relative reference
--]
[#macro resource relativeUrl]${requestContext.getContextUrl("${relativeUrl}?r=${renderContext.applicationVersion}")?html}[/#macro]]

