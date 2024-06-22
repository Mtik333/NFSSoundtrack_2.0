var foundArtist;
var discogsToUpdate = false;

$(document).ready(function () {
    $("#manage-artists").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var rowToChangeOfficialArtist = $('<div class="row p-1">');
        var colToChangeOfficialArtist = $('<div class="col">');
        divToAppend.append(rowToChangeOfficialArtist);
        rowToChangeOfficialArtist.append(colToChangeOfficialArtist);
        colToChangeOfficialArtist.append('<input type="checkbox" class="form-check-input m-3" id="changeOfficialArtist"><label class="form-check-label pl-2 pt-2" for="changeOfficialArtist">Modify official artist of songs?</label>');
        divToAppend.append(rowDiv);
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
                    $(failureAlertHtml).show().fadeTo(500, 500).slideUp(500, function () {
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
    $("#discogs-info").remove();
    var discoGsDiv = $('<div id="discogs-info">');
    generateDiscogsInfoDiv(foundArtist.value, discoGsDiv);
    divToAppend.append(discoGsDiv);
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
    artistToSave.discogsToUpdate = discogsToUpdate;
    if (discogsToUpdate) {
        artistToSave.uri = $("#discogsUriInput").val()
        artistToSave.twitter = $("#discogsTwitterInput").val();
        artistToSave.facebook = $("#discogsFacebookInput").val();
        artistToSave.instagram = $("#discogsInstagramInput").val();
        artistToSave.soundcloud = $("#discogsSoundcloudInput").val();
        artistToSave.wikipedia = $("#discogsWikipediaInput").val();
        artistToSave.myspace = $("#discogsMyspaceInput").val();
        artistToSave.profile = $("#discogsProfile").text();
        artistToSave.id = $("#discogsIdInput").val();
    }
    artistToSave.changeOfficialArtist = $("#changeOfficialArtist").prop("checked");
    $.ajax({
        async: false,
        type: "PUT",
        data: JSON.stringify(artistToSave),
        url: "/author/put",
        contentType: 'application/json; charset=utf-8',
        // dataType: 'json',
        success: function (ooo) {
            $(successAlertHtml).show().fadeTo(500, 500).slideUp(500, function () {
                $(successAlertHtml).slideUp(500).hide();
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
            });
        },
        error: function (ooo) {
            $(failureAlertHtml).show().fadeTo(500, 500).slideUp(500, function () {
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
    var buttonsDiv = $('<div class="col mt-auto">');
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
    var buttonsDiv = $('<div class="col mt-auto">');
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
                    $(failureAlertHtml).show().fadeTo(500, 500).slideUp(500, function () {
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
                    $(failureAlertHtml).show().fadeTo(500, 500).slideUp(500, function () {
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

function generateDiscogsInfoDiv(foundArtist, discoGsDiv) {
    $.ajax({
        async: false,
        type: "GET",
        url: "/author/discogsInfo/" + foundArtist,
        success: function (ooo) {
            var songInfo = JSON.parse(ooo);
            var zeroRowDiv = $('<div class="row p-1">');
            var divHeader = $('<h2>DiscoGS info</h2>');
            var fetchUsingDiscogsId = $('<button id="fetch-from-discogs" type="submit" class="btn btn-primary">Fetch from DiscoGS via ID</button>');
            var firstRowDiv = $('<div class="row p-1">');
            var firstRowColDiv1 = $('<div class="col">');
            var firstRowColDiv2 = $('<div class="col">');
            var firstRowColDiv3 = $('<div class="col">');
            var firstRowColDiv4 = $('<div class="col">');
            zeroRowDiv.append(divHeader);
            zeroRowDiv.append(fetchUsingDiscogsId);
            discoGsDiv.append(zeroRowDiv);
            var discogsIdInput = $('<input class="form-control" type="text" id="discogsIdInput" value="' + songInfo.discogsId + '">');
            var discogsUriInput = $('<input class="form-control" type="text" id="discogsUriInput" value="' + songInfo.uri + '">');
            var discogsTwitterInput = $('<input class="form-control" type="text" id="discogsTwitterInput" value="' + songInfo.twitter + '">');
            var discogsFacebookInput = $('<input class="form-control" type="text" id="discogsFacebookInput" value="' + songInfo.facebook + '">');
            firstRowColDiv1.append(discogsIdInput);
            firstRowColDiv1.append('<label for="discogsIdInput">DiscoGS ID</label>');
            firstRowColDiv2.append(discogsUriInput);
            firstRowColDiv2.append('<label for="discogsUriInput">DiscoGS link</label>');
            firstRowColDiv3.append(discogsTwitterInput);
            firstRowColDiv3.append('<label for="discogsTwitterInput">Twitter link</label>');
            firstRowColDiv4.append(discogsFacebookInput);
            firstRowColDiv4.append('<label for="discogsFacebookInput">Facebook link</label>');
            firstRowDiv.append(firstRowColDiv1);
            firstRowDiv.append(firstRowColDiv2);
            firstRowDiv.append(firstRowColDiv3);
            firstRowDiv.append(firstRowColDiv4);
            discoGsDiv.append(firstRowDiv);
            var secondRowDiv = $('<div class="row p-1">');
            var secondRowColDiv1 = $('<div class="col">');
            var secondRowColDiv2 = $('<div class="col">');
            var secondRowColDiv3 = $('<div class="col">');
            var secondRowColDiv4 = $('<div class="col">');
            var discogsInstagramInput = $('<input class="form-control" type="text" id="discogsInstagramInput" value="' + songInfo.instagram + '">');
            var discogsSoundcloudInput = $('<input class="form-control" type="text" id="discogsSoundcloudInput" value="' + songInfo.soundcloud + '">');
            var discogsWikipediaInput = $('<input class="form-control" type="text" id="discogsWikipediaInput" value="' + songInfo.wikipedia + '">');
            var discogsMyspaceInput = $('<input class="form-control" type="text" id="discogsMyspaceInput" value="' + songInfo.myspace + '">');
            secondRowColDiv1.append(discogsInstagramInput);
            secondRowColDiv1.append('<label for="discogsInstagramInput">Instagram link</label>');
            secondRowColDiv2.append(discogsSoundcloudInput);
            secondRowColDiv2.append('<label for="discogsSoundcloudInput">SoundCloud link</label>');
            secondRowColDiv3.append(discogsWikipediaInput);
            secondRowColDiv3.append('<label for="discogsWikipediaInput">Wikipedia link</label>');
            secondRowColDiv4.append(discogsMyspaceInput);
            secondRowColDiv4.append('<label for="discogsMyspaceInput">Myspace link</label>');
            secondRowDiv.append(secondRowColDiv1);
            secondRowDiv.append(secondRowColDiv2);
            secondRowDiv.append(secondRowColDiv3);
            secondRowDiv.append(secondRowColDiv4);
            discoGsDiv.append(secondRowDiv);
            var thirdRowDiv = $('<div class="row p-1">');
            var discogsProfile = $('<textarea class="form-control" id="discogsProfile"></textarea/>');
            discogsProfile.text(songInfo.profile);
            thirdRowDiv.append(discogsProfile);
            thirdRowDiv.append('<label for="discogsProfile">DiscoGS profile</label>');
            discoGsDiv.append(thirdRowDiv);
        },
        error: function (ooo) {
            console.log("e2");
            console.log(ooo);
        },
    });
}

$(document).on('click', '#fetch-from-discogs', function (e) {
    var idToCheck = Number($("#discogsIdInput").val());
    discogsToUpdate = true;
    $.ajax({
        async: false,
        type: "GET",
        url: "/author/discogsEntry/" + idToCheck,
        success: function (ooo) {
            var songInfo = JSON.parse(ooo);
            $("#discogsUriInput").val(songInfo.uri);
            $("#discogsTwitterInput").val(songInfo.twitter);
            $("#discogsFacebookInput").val(songInfo.facebook);
            $("#discogsInstagramInput").val(songInfo.instagram);
            $("#discogsSoundcloudInput").val(songInfo.soundcloud);
            $("#discogsWikipediaInput").val(songInfo.wikipedia);
            $("#discogsMyspaceInput").val(songInfo.myspace);
            $("#discogsProfile").text(songInfo.profile);
        }
    });

});