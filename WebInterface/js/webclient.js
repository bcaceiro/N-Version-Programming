function getSelectedTabId() {
    //return $('[aria-expanded=true]').attr('id');
    return $('li.active').children().attr('id')
}

function getActiveButton() {
    return $('#' + getSelectedTabId() + '-submit')
}

function enableSubmitButton() {
    getActiveButton().removeAttr('disabled')
}

function disableSubmitButton() {    
    getActiveButton().attr('disabled', 'disabled')
}

function fireLater(f) {
    setTimeout(f,1);
}


function validateForm() {
    //console.log('fired');
    if ( allInputsFilled(getSelectedTabId())) {
        enableSubmitButton(); 
        //console.log('enable');
        return true;
    } else {
       disableSubmitButton();
       //console.log('disable');
       return false;
    }
}

function doSubmit() {
    if (validateForm()) {
        $('form').submit();
    }
}

function allInputsFilled(prefix) {
    all_filled_and_valid = true;
    $('input[id*=' + prefix + ']').each(
        function(i, e)  {
            obj = $(e);
            if ( obj.val() == '' ||  obj.hasClass('LV_invalid_field') ) 
                all_filled_and_valid = false; 
            /*console.log(e); console.log(obj.val()); console.log(obj.hasClass('LV_invalid_field'));*/
        });

    return all_filled_and_valid;
}


k_sample_count = 0
function addKSamples(createOther,focus) {
    btn_field='<div class="col-lg-6 col-sm-8" id="btn_k_samples"><div class="avatar avatar-green avatar-sm"><button class="avatar avatar-red avatar-sm icon icon-add"  onclick="addKSamples(true,true);"></button></div></div>'
    field='<div class="form-group form-group-label" ><div class="row"><div class="col-lg-6 col-sm-8"><label class="floating-label" for="mealtime-k-samples-PLACEHOLDER">Insert a value in a scale of 1 - 10</label><input class="form-control" id="mealtime-k-samples-PLACEHOLDER" type="text"></div>';
    field = field.split('PLACEHOLDER').join(++k_sample_count);
    $('#btn_k_samples').remove();
    curr = $('#the_fields').html()
    $('#the_fields').html(curr + field + btn_field + '</div>');
    f2 = new LiveValidation("mealtime-k-samples-"+k_sample_count);
    f2.add(Validate.Presence,  { failureMessage: "Please fill this field" });
    f2.add(Validate.Numericality, { onlyInteger: true } , { failureMessage: "Must be a number!" });
    f2.add( Validate.Numericality, { minimum: 0, maximum: 10}, {failureMessage: "Must be a number between 0 and 10"}); 

    if (focus)
        $('#mealtime-k-samples-'+k_sample_count).focus();
    if ( createOther )
        addKDropSamples(false,false);

}

k_sample_values_count = 0
function addKDropSamples(createOther,focus) {
    btn_field='<div class="col-lg-6 col-sm-8" id="btn_k_samples_values"><div class="avatar avatar-green avatar-sm"><button class="avatar avatar-red avatar-sm icon icon-add"  onclick="addKDropSamples(true,true);"></button></div></div>'
    field='<div class="form-group form-group-label" ><div class="row"><div class="col-lg-6 col-sm-8"><label class="floating-label" for="mealtime-k-samples-values-PLACEHOLDER">Insert a value in a scale between 25 to 100 mg/dl</label><input class="form-control" id="mealtime-k-samples-values-PLACEHOLDER" type="text"></div>';
    field = field.split('PLACEHOLDER').join(++k_sample_values_count);
    $('#btn_k_samples_values').remove();
    curr = $('#the_fields_values').html()
    $('#the_fields_values').html(curr + field + btn_field + '</div>');
    var f1 = new LiveValidation('mealtime-k-samples-values-'+k_sample_values_count);
    f1.add(Validate.Presence,  { failureMessage: "Please fill this field" });
    f1.add(Validate.Numericality, { onlyInteger: true } , { failureMessage: "Must be a number!" });
    f1.add( Validate.Numericality, { minimum: 0, maximum: 10}, {failureMessage: "Must be a number between 0 and 10"}); 

    if (focus)
        $('#mealtime-k-samples-values-'+k_sample_values_count).focus();
    if ( createOther )
        addKSamples(false,false);
}

function listenToAllInputFields() {
    $('input').on("input change", function(e) {fireLater(validateForm); return true; } );
    $('input').on("input change", function(e) {fireLater(validateForm); return true; } );
    $('a[data-toggle="tab"]').click( function(e) {fireLater(validateForm); return true; } ) ;

    anon = function() { validateForm(); setTimeout(anon,100);}; setTimeout(anon,100);
}
