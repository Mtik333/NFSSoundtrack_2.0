$(document).ready(function () {
    /**
     * when clicking 'group' in artist page, like 'composer', 'remix' etc.
     */
    $(document).on('click', 'a.artistgroup', function (e) {
        //don't want to ago anywhere
        e.preventDefault();
        //need to check how many groups are already active
        var howManyActiveAlready = $('.artistgroup.active').length;
        //is the one we clicked is already active
        var isActive = $(this).hasClass("active");
        //is the 'all' group active now
        var isAllGroupActive = $('a[data-authorcontribution="all"]').hasClass("active");
        //here we just fetch value of this group
        var contributionType = $(this).attr('data-authorcontribution');
        //we also have to track info about how many subgroups (aliases) are active
        activeSubgroups = $('.aliassubgroup.active');
        //is the 'all' subgroup active now
        var isActiveAllSubgroup = $("#allaliases").hasClass("active");
        if (contributionType == 'all') {
            if (!isActive) {
                //we clicked on 'all' group and it is not active
                $('.artistgroup.active').each(function () {
                    $(this).removeClass('active');
                    //we get rid of any previously active groups
                });
                //we make 'all' group active
                $(this).addClass('active');
                $("#artistStuff").find("tr.all").each(function () {
                    var trToCheck = $(this);
                    var trAliasId = $(this).attr("data-aliasid");
                    if (isActiveAllSubgroup) {
                        //now if 'all' subgroup is active, we go through all rows in table and make them visible
                        trToCheck.removeClass('visually-hidden');
                    } else {
                        //otherwise we make visible only rows relevant to filter based on active subgroups (aliases)
                        activeSubgroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-aliassubgroupid");
                            if (trAliasId == aliasSubgroupId) {
                                trToCheck.removeClass('visually-hidden');
                            }
                        });
                    }
                });
            }
        } else {
            //this is not 'all' group clicked
            if (!isActive) {
                //we have to activate this group
                $(this).addClass('active');
                //if 'all' group was active before click
                if (isAllGroupActive) {
                    $('tr.all').each(function () {
                        $(this).addClass('visually-hidden');
                        //we simply hide everything
                    });
                    //then we also make 'all' group 'unactive'
                    $('a[data-authorcontribution="all"]').each(function () {
                        $(this).removeClass('active');
                        //we get rid of any previously active groups
                    });
                }
                //now we iterate through the rows with songs of 'clicked type' contribution
                //so if we selected 'subcomposer', then we go through 'subcomposer' rows
                $("#artistStuff").find("tr." + contributionType).each(function () {
                    var trToCheck = $(this);
                    //we have to keep eye on current subgroup filter
                    var trAliasId = $(this).attr("data-aliasid");
                    //if 'all' subgroup is used then we just show all rows
                    if (isActiveAllSubgroup) {
                        trToCheck.removeClass('visually-hidden');
                    } else {
                        //otherwise we go through each active subgroup (as we can have multiple when 'all' is not active)
                        activeSubgroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-aliassubgroupid");
                            //and we just show all songs with currently active aliases
                            if (trAliasId == aliasSubgroupId) {
                                trToCheck.removeClass('visually-hidden');
                            }
                        });
                    }
                });
            } else {
                //this group was active so we now make it unactive
                $(this).removeClass('active');
                //iterate over rows with this type of contribution
                $("#artistStuff").find("tr." + contributionType).each(function () {
                    var trToCheck = $(this);
                    var trAliasId = $(this).attr("data-aliasid");
                    //if 'all' subgroup is active, we hide all rows of this contribution type since we're disabling this group
                    if (isActiveAllSubgroup) {
                        trToCheck.addClass('visually-hidden');
                    } else {
                        //otherwise we iterate over active aliases
                        activeSubgroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-aliassubgroupid");
                            //and we hide stuff that was of clicked contribution type
                            if (trAliasId == aliasSubgroupId) {
                                trToCheck.addClass('visually-hidden');
                            }
                        });
                    }
                });
                //let's check how many active groups we have
                howManyActiveAlready = $('.artistgroup.active').length;
                //if due to this click we have 0 active groups, we automatically enable 'all' group
                if (howManyActiveAlready == 0) {
                    $('a[data-authorcontribution="all"]').each(function () {
                        $(this).addClass('active');
                    });
                    //and then we go through all rows in the table
                    $("#artistStuff").find("tr.all").each(function () {
                        var trToCheck = $(this);
                        var trAliasId = $(this).attr("data-aliasid");
                        //if 'all' alias subgroup is active then we just show the row
                        if (isActiveAllSubgroup) {
                            trToCheck.removeClass('visually-hidden');
                        } else {
                            //otherwise we go through active subgroups, check if row has the same alias and if so, show it in the table
                            activeSubgroups.each(function () {
                                var aliasSubgroupId = $(this).attr("data-aliassubgroupid");
                                if (trAliasId == aliasSubgroupId) {
                                    trToCheck.removeClass('visually-hidden');
                                }
                            });
                        }
                    });
                }
            }

        }
    });

    /**
     * this one follows logic of previous method, to be honest
     */
    $(document).on('click', 'button.aliassubgroup', function (e) {
        console.log("jesus");
        e.preventDefault();
        var howManyActiveAlready = $('.aliassubgroup.active').length;
        var isActive = $(this).hasClass("active");
        var isAllSubgroupActive = $('button[data-aliassubgroupid="all"]').hasClass("active");
        var aliasValue = $(this).attr('data-aliassubgroupid');
        var activeGroups = $('.artistgroup.active');
        var isActiveAllGroup = $("#allartistgroup").hasClass("active");
        if (aliasValue == 'all') {
            if (!isActive) {
                $('.aliassubgroup.active').each(function () {
                    $(this).removeClass('active');
                });
                $(this).addClass('active');
                $("#artistStuff").find("tr.all").each(function () {
                    var trToCheck = $(this);
                    var trRoleVal = $(this).attr("data-role");
                    if (isActiveAllGroup) {
                        trToCheck.removeClass('visually-hidden');
                    } else {
                        activeGroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-authorcontribution");
                            if (trRoleVal == aliasSubgroupId) {
                                trToCheck.removeClass('visually-hidden');
                            }
                        });
                    }
                });
            }
        } else {
            if (!isActive) {
                $(this).addClass('active');
                if (isAllSubgroupActive) {
                    $('tr.all').each(function () {
                        $(this).addClass('visually-hidden');
                    });
                    $('button[data-authoralias="all"]').each(function () {
                        $(this).removeClass('active');
                    });
                }
                $("#artistStuff").find("tr[data-aliasid='" + aliasValue + "']").each(function () {
                    var trToCheck = $(this);
                    var trRoleVal = $(this).attr("data-role");
                    if (isActiveAllGroup) {
                        trToCheck.removeClass('visually-hidden');
                    } else {
                        activeGroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-authorcontribution");
                            if (trRoleVal == aliasSubgroupId) {
                                trToCheck.removeClass('visually-hidden');
                            }
                        });
                    }
                });
            } else {
                $(this).removeClass('active');
                $("#artistStuff").find("tr[data-aliasid='" + aliasValue + "']").each(function () {
                    var trToCheck = $(this);
                    var trRoleVal = $(this).attr("data-role");
                    if (isActiveAllGroup) {
                        trToCheck.addClass('visually-hidden');
                    } else {
                        activeGroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-authorcontribution");
                            if (trRoleVal == aliasSubgroupId) {
                                trToCheck.addClass('visually-hidden');
                            }
                        });
                    }
                });
                howManyActiveAlready = $('.aliassubgroup.active').length;
                if (howManyActiveAlready == 0) {
                    $('button[data-authoralias="all"]').each(function () {
                        $(this).addClass('active');
                    });
                    $("#artistStuff").find("tr.all").each(function () {
                        var trToCheck = $(this);
                        var trRoleVal = $(this).attr("data-role");
                        if (isActiveAllGroup) {
                            trToCheck.removeClass('visually-hidden');
                        } else {
                            activeGroups.each(function () {
                                var aliasSubgroupId = $(this).attr("data-authorcontribution");
                                if (trRoleVal == aliasSubgroupId) {
                                    trToCheck.removeClass('visually-hidden');
                                }
                            });
                        }
                    });
                }
            }
        }
    });

        $(document).on("click", "#report-artist-bug", function () {
            //handling search case
                $(this).tooltip('dispose');
                $("#source-url").val(window.location.href);
                $("#reportProblemModal").modal('show');
                $("#problem-type").val("WRONG_ARTIST_INFO");
                $("#affected-songsubgroup").val(-1);
        });
});