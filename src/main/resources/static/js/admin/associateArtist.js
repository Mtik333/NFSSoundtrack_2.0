$(document).ready(function () {
    $("#associate-artists").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var buttonDiv = $('<div class="row p-1">');
        var buttonCol = $('<div class="col">');
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        var rightCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        var authorToBeMemberSelect = $('<input class="form-control" id="authorToBeMemberSelect"/>');
        var authorToBeMemberSelectHidden = $('<input type="hidden" id="authorToBeMemberSelectHidden"/>');
        leftCellDiv.append('<label for="authorToBeMemberSelect">Author - member to assign</label>');
        leftCellDiv.append(authorToBeMemberSelect);
        leftCellDiv.append(authorToBeMemberSelectHidden);
        setupAutocompleteMergeArtist(authorToBeMemberSelect, authorToBeMemberSelectHidden, "");
        var authorToAssignMemberTo = $('<input class="form-control" id="authorToAssignMemberTo"/>');
        var authorToAssignMemberToHidden = $('<input type="hidden" id="authorToAssignMemberToHidden"/>');
        rightCellDiv.append('<label for="authorToAssignMemberTo">Author - to assign member to</label>');
        rightCellDiv.append(authorToAssignMemberTo);
        rightCellDiv.append(authorToAssignMemberToHidden);
        setupAutocompleteMergeArtist(authorToAssignMemberTo, authorToAssignMemberToHidden, "");
        buttonCol.append('<button id="associate-artist" type="submit" class="btn btn-primary">Save</button>');
        buttonDiv.append(buttonCol);
        rowDiv.append(buttonDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        divToAppend.append(rowDiv);
    });

    $(document).on('click', "#associate-artist", function (e) {
        var authorToBeMember = $("#authorToBeMemberSelectHidden").val();
        var authorToAssignMember = $("#authorToAssignMemberToHidden").val();
        var objToSubmit = {};
        objToSubmit.authorToBeMemberId = Number(authorToBeMember);
        objToSubmit.authorToAssignMemberId = Number(authorToAssignMember);
        $(successAlertHtml).hide();
        $(failureAlertHtml).hide();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(objToSubmit),
            contentType: 'application/json; charset=utf-8',
            url: "/author/associateArtist",
            success: function (ooo) {
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        var divToAppend = $('#nfs-content');
                        divToAppend.empty();
                    });
                });
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function () {
                        var divToAppend = $('#nfs-content');
                        divToAppend.empty();
                    });
                });
            },
        });
    });
});

function setupAutocompleteMergeArtist(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/author/authorNameMgmt/" + encodeURIComponent($(mySelect).val().replace("/","__")),
                success: function (ooo) {
                    var foundArtist = JSON.parse(ooo);
                    if (foundArtist) {
                        response(foundArtist);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500);
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
