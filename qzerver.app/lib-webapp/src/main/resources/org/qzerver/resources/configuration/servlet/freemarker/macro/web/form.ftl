[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="com.gainmatrix.lib.web.attribute.render.RenderContext" --]

[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/web/system.ftl" as system]
[#import "/org/qzerver/resources/configuration/servlet/freemarker/macro/core/helpers.ftl" as helpers]

[#-- Main form block
 * path - bean path
 * method - post method ("get" or "post")
 * attrs - optional html-attribute list
--]
[#macro form path method="post" attrs={} style="" classes=[]]
[#local formBindStatus = requestContext.getBindStatus(path, false)/]
<div class="form-block[#if formBindStatus.error] form-block-error[/#if]">
<div class="form-errors">
[#list formBindStatus.errorMessages as formErrorMessage]
    <div class="form-error form-error-${formErrorMessage_index?c}">${formErrorMessage?html}</div>
[/#list]
</div>
<form [@controlAttributes attrs classes style/] method="${method}">
[#nested]
</form>
</div>
[/#macro]

[#-- Control section block
 * titleCode - optional title message code
--]
[#macro section titleCode=""]
<fieldset>
[#nested]
</fieldset>
[/#macro]

[#-- Get control identifier by path
 * path - value path
--]
[#function getInputId path]
[#local fieldBindStatus = requestContext.getBindStatus(path, false)]
[#return "field_input_${fieldBindStatus.expression}"]
[/#function]

[#-- Base macro for control
 * path - value path
 * titleCode - label message code
 * hintCode - hint message code
--]
[#macro field path titleCode hintCode=""]
[#local fieldBindStatus = requestContext.getBindStatus(path, false)]
[#local fieldBindText = helpers.getText(fieldBindStatus.value!"")]
<div id="field_block_${fieldBindStatus.expression}"
    class="form-field-block[#if fieldBindStatus.error] form-field-block-error[/#if]">
    <div class="form-field-label">
        <label for="field_input_${fieldBindStatus.expression}">[@system.msg titleCode/]</label>
    </div>
    <div class="form-field-control">
        [#nested fieldBindStatus fieldBindText]
    </div>
    <div class="form-field-errors">
        [#list fieldBindStatus.errorMessages as errorMessage]
            <div class="form-field-error form-field-error-${errorMessage_index?c}">${errorMessage?html}</div>
        [/#list]
    </div>
    [#if hintCode?has_content]
    <div class="form-field-hint">
        [@system.msg hintCode/]
    </div>
    [/#if]
</div>
[/#macro]

[#-- Control settings
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
--]
[#macro controlAttributes attrs classes style]
[#if attrs?has_content][#list attrs?keys as key]${key?html}="${attrs[key]?html}"[#if key_has_next] [/#if][/#list][/#if]
[#if classes?has_content]class="[#list classes as class]${class?html}[#if class_has_next] [/#if][/#list]"[/#if]
[#if style?has_content]style="${style?html}"[/#if]
[/#macro]

[#-- Text input
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
--]
[#macro inputText path titleCode attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
<input [@controlAttributes attrs classes style/]
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}"
    value="${fieldBindText?html}"
    type="text"/>
[/@field]
[/#macro]

[#-- Number input
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
--]
[#macro inputNumber path titleCode attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
<input [@controlAttributes attrs classes style/]
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}"
    value="${fieldBindText?html}"
    type="text"/>
[/@field]
[/#macro]

[#-- Password input
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
 * showPassword - whether to render password text
--]
[#macro inputPassword path titleCode attrs={} hintCode="" style="" classes=[] showPassword=false]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
<input [@controlAttributes attrs classes style/]
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}"
    [#if showPassword]value="${fieldBindText?html}"[#else]value=""[/#if]
    type="password"/>
[/@field]
[/#macro]

[#-- Hidden input
 * path - value selector
--]
[#macro inputHidden path]
[#local fieldBindStatus = requestContext.getBindStatus(path, false)]
[#local fieldBindText = helpers.getText(fieldBindStatus.value!"")]
<input
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}"
    value="${fieldBindText?html}"
    type="hidden"/>
[/#macro]

[#-- Text block input
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
--]
[#macro inputTextBox path titleCode attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
<textarea [@controlAttributes attrs classes style/]
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}">${fieldBindText?html}</textarea>
[/@field]
[/#macro]

[#-- Checkbox
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
--]
[#macro inputCheckbox path titleCode attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
<input type="hidden" name="_${fieldBindStatus.expression}" value="on"/>
<input [@controlAttributes attrs classes style/]
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}"
    [#if fieldBindText=="true"]checked="checked"[/#if]
    type="checkbox"/>
[/@field]
[/#macro]

[#-- Combobox (base control - option should be explicitly specified)
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
--]
[#macro inputSingleSelect path titleCode attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
<select [@controlAttributes attrs classes style/]
    id="field_input_${fieldBindStatus.expression}"
    name="${fieldBindStatus.expression}">
    [#nested]
</select>
[/@field]
[/#macro]


[#-- Combobox (collection of values)
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
 * options - list of values
--]
[#macro inputSingleSelectSeq path titleCode options attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
[@inputSingleSelect path titleCode attrs hintCode style classes]
    [#nested]
    [#list options as value]
    [#local label=helpers.getText(value)]
    <option value="${value?html}"[#if helpers.isEqualText(fieldBindText, value)] selected="selected"[/#if]>${label?html}</option>
    [/#list]
[/@inputSingleSelect]
[/@field]
[/#macro]

[#-- Combobox (dictionary of values)
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
 * options - dictionary of values
--]
[#macro inputSingleSelecеDict path titleCode options attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
[@inputSingleSelect path titleCode attrs hintCode style classes]
    [#nested]
    [#list options?keys as value]
    [#local label=helpers.getText(options[value])]
    <option value="${value?html}"[#if helpers.isEqualText(fieldBindText, value)] selected="selected"[/#if]>${label?html}</option>
    [/#list]
[/@inputSingleSelect]
[/@field]
[/#macro]

[#-- Combobox (list of beans)
 * path - value selector
 * titleCode - title message code
 * hintCode - hint message code
 * attrs - optional html-attribute list
 * style - style modifiers
 * classes - class list
 * options - list of beans
 * valueName - name of value property
 * labelName - name of label property
--]
[#macro inputSingleSelectBeans path titleCode options valueName labelName attrs={} hintCode="" style="" classes=[]]
[@field path titleCode hintCode; fieldBindStatus, fieldBindText]
[@inputSingleSelect path titleCode attrs hintCode style classes]
    [#nested]
    [#list options as bean]
    [#local value=helpers.getText(bean[valueName])]
    [#local label=helpers.getText(bean[labelName])]
    <option value="${value?html}"[#if helpers.isEqualText(fieldBindText, value)] selected="selected"[/#if]>${label?html}</option>
    [/#list]
[/@inputSingleSelect]
[/@field]
[/#macro]

[#-- Combobox option
 * value - value text
 * label - lavel text
 * checked - should the option be marked as selected
--]
[#macro option value label="" checked=false]
<option value="${helpers.getText(value)?html}"[#if checked] selected="selected"[/#if]>${helpers.getText(label)?html}</option>
[/#macro]

[#-- Button section --]
[#macro buttons]
<div class="form-buttons">
[#nested]
</div>
[/#macro]

[#-- Submit button
 * titleCode - title code
--]
[#macro buttonSubmit titleCode]
<input type="submit" value="[@system.msg titleCode/]"/>
[/#macro]

[#-- Cancel button
 * titleCode - title code
--]
[#macro buttonReset titleCode]
<input type="reset" value="[@system.msg titleCode/]"/>
[/#macro]

[#-- Check: wether the specified error code exists
 * path - value selector
 * code - error code
--]
[#function isError path code]
[#assign bindStatus=requestContext.getBindStatus(path, false)]
[#list bindStatus.errorCodes as errorCode]
    [#if errorCode==code]
        [#return true]
    [/#if]
[/#list]
[#return false]
[/#function]
