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
        <a href="/raw-files/upload" class="b-btn b-btn_green b-btn_search-field-level js-colorbox">
            <i class="fa fa-plus"></i> Upload file
        </a>
        <table id="raw_files_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>Size</th>
                <th>Fingerprint</th>
            </tr>
            </thead>
            <tbody>
            <#if files?? && files?has_content >
            <#list files as f >
            <tr>
                <td><a href="/raw-files/info" class="js-colorbox">${f.name}</a></td>
                <td>${f.size}</td>
                <td>${f.fingerprint}</td>
                <td><a href="/raw-file/${f.id}/download" target="_blank">download</a>  |  <a href="#" onclick="removeTemplate('${f.id}')">remove</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeRawFileForm" method="post" action></form>
    </div>
</div>

<script>
    function removeTemplate(fileId)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeRawFileForm').attr('action', '/raw-files/' + fileId + '/delete');
            $('#removeRawFileForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_raw_tab').addClass("b-tabs-menu__item_active");

        //$('#add_tpl_btn').colorbox({href:"#js-add-tpl", inline: true});

        $('#raw_files_tbl').DataTable();
    } );


</script>

<#include "flashscope.ftl"/>

</@layout.parentLayout>