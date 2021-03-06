<#import "layout-main.ftl" as layout>

<#assign parentHead = styles in layout>
<#assign parentScripts = scripts in layout>

<#macro styles>
</#macro>

<#macro scripts>
</#macro>

<@layout.parentLayout>

<div class="b-workspace__content">
    <div class="b-workspace-content__row">
        <a href="${contextPath}/templates/upload" class="b-btn b-btn_green b-btn_search-field-level js-colorbox">
            <i class="fa fa-plus"></i> Upload Template
        </a>
        <div style="margin-left: 200px">

            <form method="get" actoin="${contextPath}/">
                <#if repos??>
                <label>Show by repo: </label><select name="repo" id="repo-filter">
                    <#list repos as repo >
                        <option value="${repo}" <#if sel_repo==repo >selected</#if> >${repo}</option>
                    </#list>
                </select>
                </#if>
            </form>
        </div>
        <table id="templates_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>Owner</th>
                <th>Context</th>
                <th>Arch</th>
                <th>Parent</th>
                <th>Version</th>
                <th>Size</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if templates?? && templates?has_content >
            <#list templates as t >
            <tr>
                <td><a href="${contextPath}/templates/${t.id}/info" class="js-colorbox">${t.name}</a></td>
                <td>${(owners[t.id]??)?then(owners[t.id],"")}</td>
                <td>${t.id?split(".")[0]}</td>
                <td><#if t.architecture??>${t.architecture}</#if></td>
                <td><#if t.parent??>${t.parent}</#if></td>
                <td>${t.version}</td>
                <td><#if t.size??>${t.size}</#if></td>
                <td><a href="${contextPath}/templates/${t.id}/download" target="_blank">download</a>
                    |  <a href="${contextPath}/relations/by-object?id=${t.id}&obj_type=3" class="js-colorbox">permissions</a>
                    <#if !( isPublic?? && isPublic ) >
                    |  <a href="#" onclick="removeTemplate('${t.id}')">remove</a>
                    |  <a href="#js-add-trust-rel" onclick="$('#template_id').val('${t.id}')" class="js-colorbox-inline">share</a>
                    </#if>
                </td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeTemplForm" method="post" action></form>
    </div>
</div>

<script>
    function removeTemplate(templId)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeTemplForm').attr('action', '${contextPath}/templates/' + templId + '/delete');
            $('#removeTemplForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_templates_tab').addClass("b-tabs-menu__item_active");

        //$('#add_tpl_btn').colorbox({href:"#js-add-tpl", inline: true});

        $('#templates_tbl').DataTable();

        $('#repo-filter').on('change', function(e){
            $(this).parent().submit();
        });

        $('table.dataTable').on( 'page.dt', function () {
            setTimeout( function () { recreateColorboxes(); }, 1000 );
        } );
        $('table.dataTable').on( 'length.dt', function ( e, settings, len ) {
            setTimeout( function () { recreateColorboxes(); }, 1000 );
        } );
    } );

</script>

<#include "_popup-share-template.ftl"/>

</@layout.parentLayout>