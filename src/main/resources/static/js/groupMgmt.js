var gameId;
var fullScopeOfEdit;
var editedGroup;
var groupIndex;
var successAlertHtml = '<div class="alert alert-success" id="success-alert" style="display: none;"><strong>Success!</strong></div>';
$(document).ready(function () {
    $('#success-alert').hide();
    function getGroupsFromGame() {
        $.ajax({
            async: false,
            type: "GET",
            url: "/maingroup/read/" + gameId,
            success: function (ooo) {
                fullScopeOfEdit = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                var newGroupSpan = $('<h2><button data-gameId=' + gameId + ' id="new-group-' + gameId + '" class="new-group badge badge-success">New group</button></h2>');
                if (fullScopeOfEdit.length > 0) {
                    var tableApp = $('<table class="table">');
                    divToAppend.append(newGroupSpan);
                    divToAppend.append(tableApp);
                    for (let i = 0; i < fullScopeOfEdit.length; i++) {
                        var groupName = fullScopeOfEdit[i].groupName;
                        var groupId = fullScopeOfEdit[i].id;
                        tableApp.append('<tr>');
                        tableApp.append('<td class="w-75"><h3><span class="badge">' + groupName + '</span></h3></td>');
                        tableApp.append('<td class="text-right"><button type="button" id="edit-group" data-groupId="' + fullScopeOfEdit[i].id + '" class="btn btn-warning">Edit</button></td>');
                        tableApp.append('<td class="text-right"><button type="button" id="delete-group" data-groupId="' + fullScopeOfEdit[i].id + '" class="btn btn-danger">Delete</button></td>');
                        tableApp.append('</tr>');
                    }
                    divToAppend.append('</table>');
                }
                divToAppend.append(successAlertHtml);
                console.log("e1");
            },
            error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });
    }
    $('a.manage-groups').click(function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        getGroupsFromGame();
    });
    $(document).on('click', 'button.new-group', function (e) {
        console.log(e);
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        var formAppend = $('<div id="new-group">');
        divToAppend.append(formAppend);
        formAppend.append('<div class="form-group">');
        formAppend.append('<label for="groupName">Group name</label>');
        formAppend.append('<input type="text" class="form-control" id="groupName" placeholder="Give group a name">');
        formAppend.append('<div id="div-subGroups" class="form-group">');
        formAppend.append('<label for="subGroups">Subgroups</label>');
        var divWithCols = $('<div class="row"></div>');
        divWithCols.append('<div class="col-sm w-80"><input type="text" class="form-control subGroups" id="subGroups-0" placeholder="Subgroup name"></div>');
        divWithCols.append('<div class="col-sm"><button id="add-subgroup" type="submit" class="btn btn-success">+</button><button id="delete-subgroup" type="submit" class="btn btn-danger">X</button></div>');
        formAppend.append(divWithCols);
        formAppend.append('<button id="submit-group" type="submit" class="btn btn-primary">Submit</button>');
        formAppend.append('<button id="cancel-group" type="submit" class="btn btn-primary">Cancel</button>');
        divToAppend.append(successAlertHtml);
    });

    $(document).on('click', '#cancel-group', function (e) {
        getGroupsFromGame();
    });

    $(document).on('click', '#add-subgroup', function (e) {
        var divWithCols = $('<div class="row"></div>');
        var targetId = e.target.id;
        targetId = parseInt(targetId.replace("subGroups-", ""));
        targetId++;
        divWithCols.append('<div class="col-sm w-80"><input type="text" class="form-control subGroups" id="subGroups-' + targetId + '" placeholder="Subgroup name"></div>');
        divWithCols.append('<div class="col-sm"><button id="add-subgroup" type="submit" class="btn btn-success">+</button><button id="delete-subgroup" type="submit" class="btn btn-danger">X</button></div>');
        $(divWithCols).insertAfter(e.target.parentElement.parentElement);

    });

    $(document).on('click', '#edit-group', function (e) {
        var groupToEdit;
        for (let i = 0; i < fullScopeOfEdit.length; i++) {
            var groupId = fullScopeOfEdit[i].id
            if (groupId == $(this).attr('data-groupId')) {
                groupToEdit = fullScopeOfEdit[i];
                break;
            }
        }
        console.log(e);
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        var formAppend = $('<div id="content-edit-group" data-groupId="'+groupToEdit.id+'">');
        divToAppend.append(formAppend);
        formAppend.append('<div class="form-group">');
        formAppend.append('<label for="groupName">Group name</label>');
        formAppend.append('<input type="text" class="form-control" id="groupName" value="' + groupToEdit.groupName + '">');
        formAppend.append('<div id="div-subGroups" class="form-group">');
        formAppend.append('<label for="subGroups">Subgroups</label>');
        for (let i = 0; i < groupToEdit.subgroups.length; i++) {
            var divWithCols = $('<div class="row"></div>');
            divWithCols.append('<div class="col-sm w-80"><input type="text" class="form-control subGroups" id="subGroups-' + i + '" data-subGroupId="' + groupToEdit.subgroups[i].id + '" value="' + groupToEdit.subgroups[i].subgroupName + '"></div>');
            divWithCols.append('<div class="col-sm"><button id="add-subgroup" type="submit" class="btn btn-success">+</button><button type="button" id="delete-subgroup" data-subGroupId="' + groupToEdit.subgroups[i].id + '" class="btn btn-danger">X</button></div>');
            formAppend.append(divWithCols);
            //text-decoration-line-through
        }
        formAppend.append('<div class="form-group">');
        formAppend.append('<button id="update-subgroups" data-groupId="'+groupToEdit.id+'" type="submit" class="btn btn-primary">Submit</button>');
        formAppend.append('<button id="cancel-group" type="submit" class="btn btn-primary">Cancel</button>');
        divToAppend.append(successAlertHtml);
    });

    $(document).on('click', '#delete-subgroup', function (e) {
        var subgroupInput = $($(this).parent().parent()).find('input')[0];
        if ($(subgroupInput).attr('data-subgroupId')!=undefined){
            $(subgroupInput).addClass('text-decoration-line-through');
        } else {
            subgroupInput.parentElement.parentElement.remove();
        }
    });

    $(document).on('click', '#submit-group', function (e) {
        var subgroups = $("#new-group").find('input.subGroups').map(function (idx, elem) {
            return $(elem).val();
        }).get();
        var formData = {
            groupName: $('#groupName').val(),
            subgroupsNames: subgroups
        };
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(formData),
            url: "/maingroup/save/" + gameId,
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                console.log("eee");
                getGroupsFromGame();
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500);
                });
            },
            error: function (ooo) {
                console.log("e2");

            },
            done: function (ooo) {
                console.log("e3");

            }
        });
        //event.preventDefault();
        //return false;
    });

    $(document).on('click', '#update-subgroups', function (e) {
        var groupId = $(this).attr('data-groupId');
        var subgroups = $('#content-edit-group').find('input.subGroups').map(function (idx, elem) {
            var returnVal = $(elem).val();
            if ($(elem).attr('data-subgroupId')!=undefined){
                if ($(elem).hasClass('text-decoration-line-through')){
                    return $(elem).attr('data-subgroupId')+"-DELETE";
                } else {
                    return $(elem).attr('data-subgroupId')+"-UPDATE-"+$(elem).val();
                }
            }
            else return $(elem).val();
        }).get();
        var formData = {
            groupName: $('#groupName').val(),
            subgroupsNames: subgroups
        };
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(formData),
            url: "/maingroup/put/" + groupId,
            contentType: 'application/json; charset=utf-8',
            success: function (ooo) {
                console.log("eee");
                getGroupsFromGame();
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500);
                });
            },
            error: function (ooo) {
                console.log("e2");

            },
            done: function (ooo) {
                console.log("e3");

            }
        });
        //event.preventDefault();
        //return false;
    });

    $(document).on('click', '#delete-group', function (e) {
        var groupId = $(this).attr('data-groupId');
        $.ajax({
            async: false,
            type: "DELETE",
            url: "/maingroup/delete/" + groupId,
            success: function (ooo) {
                console.log("eee");
                getGroupsFromGame();
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500);
                });

            },
            error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");

            }
        });

    });

});
