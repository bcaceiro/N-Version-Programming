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
    $('input[id*=' + prefix + ']').each( function(i, e)  { obj = $(e); if ( obj.val() == '' ||  obj.hasClass('LV_invalid_field') )  all_filled_and_valid = false; 
    /*console.log(e); console.log(obj.val()); console.log(obj.hasClass('LV_invalid_field'));*/

});

    return all_filled_and_valid;
}

function listenToAllInputFields() {
    $('input').on("input change", function(e) {fireLater(validateForm); return true; } );
    $('input').on("input change", function(e) {fireLater(validateForm); return true; } );
    $('a[data-toggle="tab"]').click( function(e) {fireLater(validateForm); return true; } ) ;

    anon = function() { validateForm(); setTimeout(anon,100);}; setTimeout(anon,100);
}
