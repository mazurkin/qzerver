[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]

[#macro navigator pager url]
<div class="pagination pagination-centered">
    <ul>
        <li[#if pager.page=1] class="disabled"[/#if]><a href="[@system.url "${url}1"/]">&laquo;</a></li>
        [#assign halfWindow=4]
        [#if (pager.page - 1) > halfWindow]
            [#assign pageLeft=pager.page - halfWindow]
        [#else]
            [#assign pageLeft=1]
        [/#if]
        [#if (pager.pageCount - pager.page) > halfWindow]
            [#assign pageRight=pager.page + halfWindow]
        [#else]
            [#assign pageRight=pager.pageCount]
        [/#if]
        [#if pageLeft > 1]<li class="disabled"><span>...</span></li>[/#if]
        [#list pageLeft..pageRight as page]
            [#if page != pager.page]
                <li><a href="[@system.url "${url}${page?c}"/]">${page?c}</a></li>
            [#else]
                <li class="active"><span>${page?c}</span></li>
            [/#if]
        [/#list]
        [#if pageRight < pager.pageCount]<li class="disabled"><span>...</span></li>[/#if]
        <li[#if pager.page=pager.pageCount] class="disabled"[/#if]><a href="[@system.url "${url}${pager.pageCount?c}"/]">&raquo;</a></li>
    </ul>
</div>
[/#macro]