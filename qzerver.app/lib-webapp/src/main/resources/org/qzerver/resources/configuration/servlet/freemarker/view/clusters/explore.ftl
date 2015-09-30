[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name="model" type="org.qzerver.web.controller.clusters.ClusterGroupExploreModel" --]
[#-- @ftlvariable name="freemarker_static['org.qzerver.web.map.SiteMap']" type="org.qzerver.web.map.SiteMap.static" --]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/layout.ftl" as layout]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/core/helpers.ftl" as helpers]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/block/pager.ftl" as block_pager]
[@layout.root_main titleCode="Clusters"]
[#escape x as x?html]

[#assign SiteMap = freemarker_static["org.qzerver.web.map.SiteMap"]]

<div class="row">
    <div class="span12">

        <h1>Clusters</h1>

        <hr/>

        <div class="btn-toolbar">
            <div class="btn-group">
                <a href="#" class="btn btn-success"><span class="icon-plus"></span> Create new cluster group..</a>
            </div>
        </div>

        [#if model.clusterGroups?has_content]
            <table class="table table-condensed table-striped table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Nodes</th>
                        <th>Rolling index</th>
                        <th>&nbsp;</th>
                    </tr>
                </thead>
                <tbody>
                    [#list model.clusterGroups as clusterGroup]
                        <tr>
                            <td>${clusterGroup.id}</td>
                            <td>${clusterGroup.name} wrg wteg wrg wrg wrgw rag wrg w argqwrg qwarg qwgr qwrgqwrg qwrgw rwgw</td>
                            <td>${clusterGroup.nodeCount}</td>
                            <td>${clusterGroup.rollingIndex}</td>
                            <td>
                                <a href="#" class="btn btn-mini"><span class="icon-edit"></span> Edit</a>
                                <a href="#" class="btn btn-mini btn-danger"><span class="icon-remove"></span> Delete</a>
                            </td>
                        </tr>
                    [/#list]
                </tbody>
            </table>

            [@block_pager.navigator pager=model.pager url="${SiteMap.CLUSTER_GROUP_EXPLORE}?page=" /]
        [/#if]

    </div>
</div>

[/#escape]
[/@layout.root_main]