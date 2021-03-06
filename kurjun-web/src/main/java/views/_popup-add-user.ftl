<div style="display: none">
<div id="js-add-user">
    <div class="b-popup__header">
        <span>Add User Public Key</span>
        <span onclick="$.colorbox.close();">
            <img src="${contextPath}/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <form method="post" action="${contextPath}/users/create">
            <div class="b-workspace__content">
                <div class="b-form__wrapper g-margin-bottom-half">
                    <label class="b-form-label">User Public Key</label>
                    <textarea name="key" class="b-form-input b-form-input_full bp-set-pub-key" style="width: 370px; height: 160px"></textarea>
                </div>
            </div>
            <div class="b-popup__footer">
                <button class="b-btn b-btn_green g-right" type="submit">
                    Add
                </button>
                <div class="clear"></div>
            </div>
        </form>
    </div>
</div>
</div>