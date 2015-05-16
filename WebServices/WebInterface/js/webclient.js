function showProgressBar() {
    $('#progress-bar').show();
}

function hideProgressBar() {
    $('#progress-bar').hide();
}

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

function genericRangeValidation(id, min,max) {
    var f1 = new LiveValidation(id);
    f1.add(Validate.Presence,  { failureMessage: "Please fill this field" });
    f1.add(Validate.Numericality, { onlyInteger: true } , { failureMessage: "Must be a number!" });
    f1.add( Validate.Numericality, { minimum: min, maximum: max}, {failureMessage: "Must be a number between " + min + " and " + max}); 
}

k_sample_count = 0
function addKSamples(createOther,focus) {
    btn_field='<div class="col-lg-6 col-sm-8" id="btn_k_samples"><div class="avatar avatar-green avatar-sm"><button class="avatar avatar-red avatar-sm icon icon-add"  onclick="addKSamples(true,true);"></button></div></div>'
    field='<div class="form-group form-group-label" ><div class="row"><div class="col-lg-6 col-sm-8"><label class="floating-label" for="mealtime-k-samples-PLACEHOLDER">Insert a value in a scale of 0 - 10</label><input class="form-control" id="mealtime-k-samples-PLACEHOLDER" type="text"></div>';
    field = field.split('PLACEHOLDER').join(++k_sample_count);
    $('#btn_k_samples').remove();
    $('#the_fields').append($(field+btn_field))
    $('#the_fields')
    genericRangeValidation("mealtime-k-samples-"+k_sample_count, 0, 10);

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
    $('#the_fields_values').append($(field+btn_field))
    genericRangeValidation("mealtime-k-samples-values-"+k_sample_values_count, 25, 100);

    if (focus)
        $('#mealtime-k-samples-values-'+k_sample_values_count).focus();
    if ( createOther )
        addKSamples(false,false);
}

function listenToAllInputFields() {
    $('input').on("input change", function(e) {fireLater(validateForm); return true; } );
    $('a[data-toggle="tab"]').click( function(e) {fireLater(validateForm); return true; } ) ;

    anon = function() { validateForm(); setTimeout(anon,100);}; setTimeout(anon,100);
}

function showResults(result, reason) {
    if ( result == -1 )
        $('#results-field').html('No consensus (' + reason + ')');
    else
        $('#results-field').html(result);
    $('#results').show();
}

function doShowTechnicalInfo() {
    techinfo = $('#technical-info');
    techinfobtns = $('[id*=-technical-info-btn]');
    if ( techinfobtns.html().indexOf('Show') > -1 ) {
        techinfobtns.html('Hide Technical Information');
        techinfo.show();
    } else {
        techinfobtns.html('Show Technical Information');
        techinfo.hide();
    }

}

function setTechnicalInfo(result) {
    //FIXME
    //$('#technical-info').show();
    $('#no-webservices').html( ( result.urls.length ? result.urls.length : 'N/A')   +'<br/> <span style="font-size: 55%">' + result.urls.join('<br/>') + '</span>' );

    if ( result.majority != -1 ) {
        $('#results-webservices').html('<span style="font-size: 100%">' + result.results.join('<br/>') + '</span>');
        $('#majority-result').html(result.majority + ' (' + result.reason + ')');
    } else {
        $('#majority-result').html();

    }
    /*
    #no-webservices
    #results-webservices
    #majority-result
     */
}

function hideResults() {
    $('#results').hide();
    $('#technical-info').hide();
}

function doSubmit() {
    showProgressBar();
    activeTab = getSelectedTabId();
    if (activeTab == 'standard') {
        parameters = {carbohydrateAmount: $('#standard-float-text1').val(),
                      carbohydrateToInsulinRatio: $('#standard-float-text2').val(),
                    preMealBloodSugar: $('#standard-float-text3').val(),
                    targetBloodSugar: $('#standard-float-text4').val(),
                    personalSensitivity: $('#standard-float-text5').val()};
        url = "/REST/MealtimeInsulinDose";

    } else if ( activeTab == 'last' ) {
        parameters = {weight: $('#last-float-text6').val()};
        url = "/REST/BackgroundInsulinDose";
    } else {
        /*
         private int carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar;
         private int physicalActivityLevel;
         private int[] physicalActivitySamples;
         private int[] bloodSugarDropSamples;
         */
        carbohydrateAmount = $('#mealtime-float-text7').val();
        carbohydrateToInsulinRatio = $('#mealtime-float-text8').val();
        preMealBloodSugar = $('#mealtime-float-text9').val();
        targetBloodSugar = $('#mealtime-float-text10').val();
        physicalActivityLevel = $('#mealtime-float-text11').val();
        array1 = [] ; $('#the_fields').children().each(function(i, e)  { array1.push($(e).children().first().children().first().children().next().val()); } );
        array2 = [] ; $('#the_fields_values').children().each(function(i, e)  { array2.push($(e).children().first().children().first().children().next().val()); } );
        parameters = { carbohydrateAmount: carbohydrateAmount,
            carbohydrateToInsulinRatio: carbohydrateToInsulinRatio,
            preMealBloodSugar: preMealBloodSugar,
            targetBloodSugar: targetBloodSugar,
            physicalActivityLevel: physicalActivityLevel,
            physicalActivitySamples: array1.join(','),
            bloodSugarDropSamples: array2.join(',')
        };
        url = "/REST/MealTimeInsulinDosePersonal";
    }

    console.log(parameters);
    $.ajax({
        method: "GET",
        url: url,
        data: parameters,
        timeout: 4000,
        success: function(data, status, jqXHR) {
            data = $.parseJSON(data);
            showResults(data.majority, data.reason);
            setTechnicalInfo(data);
            console.log(data);
            hideProgressBar();
        },
        error: function(jqXHR, status) {
            data = {urls:[], majority:-1, reason: status};
            showResults(data.majority, data.reason);
            setTechnicalInfo(data);
            hideProgressBar();
        }
    });
}
