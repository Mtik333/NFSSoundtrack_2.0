var foundArtist;

$(document).ready(function () {
    $("#manage-artists").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        var rightCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        var authorSelect = $('<input class="form-control" id="authorInput"/>');
        var authorSelectHidden = $('<input type="hidden" id="authorInputHidden"/>');
        leftCellDiv.append('<label for="authorInput">Author name</label>');
        leftCellDiv.append(authorSelect);
        leftCellDiv.append(authorSelectHidden);
        setupAutocompleteManageArtist(authorSelect, authorSelectHidden, "");
        rightCellDiv.append('<button id="save-artist" type="submit" class="btn btn-primary">Save</button>');
        rightCellDiv.append('<button id="cancel-artist" type="submit" class="btn btn-warning">Cancel</button>');
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        divToAppend.append(rowDiv);
    });

    $(document).on('click', "#cancel-artist", function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
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
                    if (foundArtist) {
                        response(foundArtist);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).show().fadeTo(2000, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500).hide();
                    });
                },
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);
            $("#country-info").empty();
            $("#alias-info").empty();
            if (foundArtist.length == 1) {
                setupCountryAndAliasFields(foundArtist[0]);
            } else {
                for (let i = 0; i < foundArtist.length; i++) {
                    var foundArtistId = foundArtist[i].value;
                    if (ui.item.value == foundArtistId) {
                        setupCountryAndAliasFields(foundArtist[i]);
                        break;
                    }
                }
            }
        },
        minLength: 1
    });
}

function setupCountryAndAliasFields(foundArtist) {
    var allAliases = foundArtist.aliases;
    var allCountries = foundArtist.countries;
    var countriesDiv = $('<div id="country-info">');
    if (allCountries.length > 0) {
        for (let i = 0; i < allCountries.length; i++) {
            generateCountryDiv(i, allCountries[i], countriesDiv);
        }
    } else {
        generateCountryDiv(0, null, countriesDiv);
    }
    var divToAppend = $('#nfs-content');
    divToAppend.append(countriesDiv);
    var aliasesDiv = $('<div id="alias-info">');
    for (let i = 0; i < allAliases.length; i++) {
        generateAliasDiv(i, allAliases[i], aliasesDiv);
    }
    divToAppend.append(aliasesDiv);
}

$(document).on('click', 'button.delete-alias', function (e) {
    var col = $(this).parent();
    var rowCol = col.parent();
    var idOfInput = $(this).attr("id").replace("delete-alias-", "aliasInfo-");
    if ($("#" + idOfInput).val() == "" || $("#" + idOfInput).val() == undefined) {
        var divCol = rowCol.parent();
        divCol.remove();
    } else {
        $("#" + idOfInput).addClass("text-decoration-line-through");
    }
});

$(document).on('click', 'button.delete-country', function (e) {
    var col = $(this).parent();
    var rowCol = col.parent();
    var idOfInput = $(this).attr("id").replace("delete-country-", "countryInfo-");
    if ($("#" + idOfInput).val() == "" || $("#" + idOfInput).val() == undefined) {
        var divCol = rowCol.parent();
        divCol.remove();
    } else {
        $("#" + idOfInput).addClass("text-decoration-line-through");
    }
});

$(document).on('click', 'button.add-country', function (e) {
    var col = $(this).parent();
    var rowCol = col.parent();
    var divCol = rowCol.parent();
    var thisId = Number($(this).attr("id").replace("add-country-", ""));
    generateCountryDiv((thisId + 1), null, divCol);
});

$(document).on('click', 'button.add-alias', function (e) {
    var col = $(this).parent();
    var rowCol = col.parent();
    var divCol = rowCol.parent();
    var thisId = Number($(this).attr("id").replace("add-alias-", ""));
    generateAliasDiv((thisId + 1), null, divCol);
});

$(document).on('click', '#save-artist', function (e) {
    var artistToSave = {};
    if ($("#authorInput").val() == "") {
        artistToSave.authorId = "NEW-" + $("#authorInputHidden").val();
    } else {
        artistToSave.authorId = $("#authorInputHidden").val();
    }
    artistToSave.authorName = $("#authorInput").val();
    var countries = $("#country-info").find("input.countryInfo");
    for (let i = 0; i < countries.length; i++) {
        var countryInput = countries[i];
        if ($(countryInput).val() != "") {
            if ($(countryInput).next().val() != "") {
                if ($(countryInput).hasClass("text-decoration-line-through")) {
                    artistToSave[countryInput.id] = "DELETE-" + $(countryInput).next().val();
                } else {
                    artistToSave[countryInput.id] = $(countryInput).next().val();
                }
            } else {
                artistToSave[countryInput.id] = "NEW-" + $(countryInput).val();
            }
        }
    }
    var aliases = $("#alias-info").find("input.aliasInfo");
    for (let i = 0; i < aliases.length; i++) {
        var aliasInput = aliases[i];
        if ($(aliasInput).val() != "") {
            if ($(aliasInput).next().val() != "") {
                if ($(aliasInput).hasClass("text-decoration-line-through")) {
                    artistToSave[aliasInput.id] = "DELETE-" + $(aliasInput).next().val();
                } else {
                    artistToSave[aliasInput.id] = "EXISTING-" + $(aliasInput).next().val() + "-VAL-" + $(aliasInput).val();
                }
            } else {
                artistToSave[aliasInput.id] = "NEW-" + $(aliasInput).val();
            }
        }
    }
    $.ajax({
        async: false,
        type: "PUT",
        data: JSON.stringify(artistToSave),
        url: "/author/put",
        contentType: 'application/json; charset=utf-8',
        // dataType: 'json',
        success: function (ooo) {
            $(successAlertHtml).show().fadeTo(2000, 500).slideUp(500, function () {
                $(successAlertHtml).slideUp(500).hide();
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
            });
        },
        error: function (ooo) {
            $(failureAlertHtml).show().fadeTo(2000, 500).slideUp(500, function () {
                $(failureAlertHtml).slideUp(500).hide();
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
            });
        },
    });
});


function generateCountryDiv(i, country, countriesDiv) {
    var countriesRowDiv = $('<div id="countriesRow-' + i + '" class="row p-1">');
    var inputDiv = $('<div class="col">');
    var buttonsDiv = $('<div class="col">');
    inputDiv.append('<label for="countryInfo-' + i + '">Country</label>');
    var countrySelect = $('<input class="form-control countryInfo" id="countryInfo-' + i + '">');
    if (country != undefined) {
        countrySelect.val(country.countryName);
    }
    inputDiv.append(countrySelect);
    var countrySelectHidden = $('<input type="hidden" id="countryInfoHidden-' + i + '"/>');
    if (country != undefined) {
        countrySelectHidden.val(country.countryId);
    }
    inputDiv.append(countrySelectHidden);
    var addCountryButton = $('<button id="add-country-' + i + '" type="submit" class="btn btn-primary add-country">+</button>');
    var deleteFeatButton = $('<button id="delete-country-' + i + '" type="submit" class="btn btn-danger delete-country">-</button>');
    buttonsDiv.append(addCountryButton);
    buttonsDiv.append(deleteFeatButton);
    countriesRowDiv.append(inputDiv);
    countriesRowDiv.append(buttonsDiv);
    setupAutocompleteCountry(countrySelect, countrySelectHidden, null);
    countriesDiv.append(countriesRowDiv);
}

function generateAliasDiv(i, alias, aliasesDiv) {
    var aliasesRowDiv = $('<div id="aliasesRow-' + i + '" class="row p-1">');
    var inputDiv = $('<div class="col">');
    var buttonsDiv = $('<div class="col">');
    inputDiv.append('<label for="aliasInfo-' + i + '">Alias</label>');
    var aliasSelect = $('<input class="form-control aliasInfo" id="aliasInfo-' + i + '">');
    if (alias != undefined) {
        aliasSelect.val(alias.aliasName);
    }
    inputDiv.append(aliasSelect);
    var aliasSelectHidden = $('<input type="hidden" id="aliasInfoHidden-' + i + '"/>');
    if (alias != undefined) {
        aliasSelectHidden.val(alias.aliasId);
    }
    inputDiv.append(aliasSelectHidden);
    var addAliasButton = $('<button id="add-alias-' + i + '" type="submit" class="btn btn-primary add-alias">+</button>');
    var deleteAliasButton = $('<button id="delete-alias-' + i + '" type="submit" class="btn btn-danger delete-alias">-</button>');
    if (i == 0) {
        $("#delete-alias-0").prop("disabled", true);
    }
    buttonsDiv.append(addAliasButton);
    buttonsDiv.append(deleteAliasButton);
    aliasesRowDiv.append(inputDiv);
    aliasesRowDiv.append(buttonsDiv);
    setupAutocompleteAliasArtistMgmt(aliasSelect, aliasSelectHidden, null);
    aliasesDiv.append(aliasesRowDiv);
}

function setupAutocompleteCountry(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/country/countryName/" + $(mySelect).val(),
                success: function (ooo) {
                    var result = JSON.parse(ooo);
                    if (result) {
                        response(result);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).show().fadeTo(2000, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500).hide();
                    });
                },
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);
        },
        minLength: 1
    });
}

function setupAutocompleteAliasArtistMgmt(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/author/aliasName/" + $(mySelect).val(),
                success: function (ooo) {
                    var result = JSON.parse(ooo);
                    if (result) {
                        response(result);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).show().fadeTo(2000, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500).hide();
                    });
                },
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);
        },
        minLength: 1
    });
}