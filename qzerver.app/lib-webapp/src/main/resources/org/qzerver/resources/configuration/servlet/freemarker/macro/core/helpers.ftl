[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#-- Get object value as string
 * value - Mandatory (non-null) value of any type
--]
[#function getText value]
    [#if value?is_boolean]
        [#return value?string("true", "false")]
    [#elseif value?is_date]
        [#return value?datetime?string]
    [#elseif value?is_enumerable]
        [#return value.name()]
    [#elseif value?is_number]
        [#return value?string]
    [#elseif value?is_string]
        [#return value?string]
    [#else]
        [#return ""]
    [/#if]
[/#function]

[#-- Check: are two values equal as strings
 * value1 - value of any type
 * value2 - value of any type
--]
[#function isEqualText value1 value2]
[#local value1String=getText(value1)]
[#local value2String=getText(value2)]
[#return value1String==value2String]
[/#function]

[#-- Output dictionary as a string
 * map - attribute dictionary
--]
[#macro outputMap map separator=" "][#list map?keys as key]${getText(key)?html}="${getText(map[key])?html}"[#if key_has_next]${separator}[/#if][/#list][/#macro]

[#-- Output value list as a string
 * list - value list
--]
[#macro outputList list separator=" "][#if list?has_content][#list list as value]${getText(value)?html}[#if value_has_next]${separator}[/#if][/#list][/#if][/#macro]

[#-- Get date as millisecond stamp (value of java.util.Date#getTime())
 * value - java.util.Date instance
--]
[#function getTimeMs value]
    [#return value?long?c]
[/#function]
