$(document).ready(function () {
    $(document).on('click', 'a.artistgroup', function (e) {
        console.log("jesus");
        e.preventDefault();
        var howManyActiveAlready = $('.artistgroup.active').length;
        var isActive = $(this).hasClass("active");
        var isAllGroupActive = $('a[data-authorcontribution="all"]').hasClass("active");
        var contributionType = $(this).attr('data-authorcontribution');
        activeSubgroups = $('.aliassubgroup.active');
        var isActiveAllSubgroup = $("#allaliases").hasClass("active");
        if (contributionType == 'all') {
            if (!isActive) {
                $('.artistgroup.active').each(function () {
                    $(this).removeClass('active');
                    //we get rid of any previously active groups
                });
                $(this).addClass('active');
                $("#artistStuff").find("tr.all").each(function () {
                    var trToCheck = $(this);
                    var trAliasId = $(this).attr("data-aliasid");
                    if (isActiveAllSubgroup) {
                        trToCheck.removeClass('visually-hidden');
                    } else {
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
            if (!isActive) {
                //we activate
                $(this).addClass('active');
                if (isAllGroupActive) {
                    $('tr.all').each(function () {
                        $(this).addClass('visually-hidden');
                        //we get rid of any previously active groups
                    });
                    $('a[data-authorcontribution="all"]').each(function () {
                        $(this).removeClass('active');
                        //we get rid of any previously active groups
                    });
                }
                $("#artistStuff").find("tr." + contributionType).each(function () {
                    var trToCheck = $(this);
                    var trAliasId = $(this).attr("data-aliasid");
                    if (isActiveAllSubgroup) {
                        trToCheck.removeClass('visually-hidden');
                    } else {
                        activeSubgroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-aliassubgroupid");
                            if (trAliasId == aliasSubgroupId) {
                                trToCheck.removeClass('visually-hidden');
                            }
                        });
                    }
                    // $(this).removeClass('visually-hidden');
                });
            } else {
                $(this).removeClass('active');
                $("#artistStuff").find("tr." + contributionType).each(function () {
                    var trToCheck = $(this);
                    var trAliasId = $(this).attr("data-aliasid");
                    if (isActiveAllSubgroup) {
                        trToCheck.addClass('visually-hidden');
                    } else {
                        activeSubgroups.each(function () {
                            var aliasSubgroupId = $(this).attr("data-aliassubgroupid");
                            if (trAliasId == aliasSubgroupId) {
                                trToCheck.addClass('visually-hidden');
                            }
                        });
                    }
                    // $(this).addClass('visually-hidden');
                });
                howManyActiveAlready = $('.artistgroup.active').length;
                if (howManyActiveAlready == 0) {
                    $('a[data-authorcontribution="all"]').each(function () {
                        $(this).addClass('active');
                        //we get rid of any previously active groups
                    });
                    $("#artistStuff").find("tr.all").each(function () {
                        var trToCheck = $(this);
                        var trAliasId = $(this).attr("data-aliasid");
                        if (isActiveAllSubgroup) {
                            trToCheck.removeClass('visually-hidden');
                        } else {
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
                    //we get rid of any previously active groups
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
                //we activate
                $(this).addClass('active');
                if (isAllSubgroupActive) {
                    $('tr.all').each(function () {
                        $(this).addClass('visually-hidden');
                        //we get rid of any previously active groups
                    });
                    $('button[data-authoralias="all"]').each(function () {
                        $(this).removeClass('active');
                        //we get rid of any previously active groups
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
                        //we get rid of any previously active groups
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
});