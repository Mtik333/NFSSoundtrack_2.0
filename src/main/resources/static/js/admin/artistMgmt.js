var successAlertHtml = '<div class="alert alert-success" id="success-alert" style="display: none;"><strong>Success!</strong></div>';
var foundArtist;

$(document).ready(function () {
    $("#manage-artists").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        var rowDiv = $('<div class="row p-1">');
        var countriesRowDiv = $('<div id="countriesRow" class="row p-1">');
        var aliasesRow = $('<div id="aliasesRow" class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        var rightCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        var authorSelect = $('<input class="form-control" id="authorInput"/>');
        var authorSelectHidden = $('<input type="hidden" id="authorInputHidden"/>');
        leftCellDiv.append('<label for="authorSelect">Author name</label>');
        leftCellDiv.append(authorSelect);
        leftCellDiv.append(authorSelectHidden);
        setupAutocompleteManageArtist(authorSelect, authorSelectHidden, "");
        rightCellDiv.append('<button id="save-artist" type="submit" class="btn btn-primary">Save</button>');
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        divToAppend.append(rowDiv);
        divToAppend.append(aliasesRow);
        divToAppend.append(countriesRowDiv);
    });
});

function setupAutocompleteManageArtist(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/author/authorNameMgmt/" + $(mySelect).val(),
                success: function (ooo) {
                    foundArtist = JSON.parse(ooo);
                    response(foundArtist);
                },
                error: function (ooo) {
                    console.log("e2");
                },
                done: function (ooo) {
                    console.log("e3");
                }
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);

        },
        minLength: 0
    });
}

function setupCountryAndAliasFields(foundArtist) {
    var countriesRowDiv = $('<div id="countriesRow" class="row p-1">');
    var aliasesRow = $('<div id="aliasesRow" class="row p-1">');
    
}