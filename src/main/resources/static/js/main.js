$(document).ready(function () {
    $("#filter_games_menu").val("");
    /*$(document).find("span.display-text").each(function(e){
        $(this).text().trim();
    })*/
    var currentGame = window.location.href.replace(document.location.origin, "");
    $(document).find("a[href='" + currentGame + "']").each(function (e) {
        if (!$(this).hasClass("nav-link")) {
            $(this).addClass("active");
            $(this).parent().parent().parent().parent().find("button").click();
        } else {
            $(this).parent().addClass("nfs-top-item-active");
        }
    })
    $(document).find("td.countries").each(function () {
        $(this).find('.country-img:nth-child(n+2)').remove();
    });
    var activeSubgroups;
    var baseVideoSrc;
    if (window.location.href.indexOf("/home") > -1) {
        $("#nfs-top-home").parent().addClass("nfs-top-item-active");
    }

    $('.play_icon').mouseover(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny", "znakwodny2"));
        baseVideoSrc = $(this).attr("data-tagVideo");
    }).mouseout(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
    });

    $(document).find("div.accordion-collapse").each(function () {
        $(this).on('shown.bs.collapse', function () {
            if ($("#filter_games_menu").val() > 3) {
                console.log($(this).offset());
                var currentScroll = $('div.offcanvas-body').scrollTop();
                var origTop = $('div.offcanvas-body').offset().top;
                var thisTop = $(this).offset().top;
                if (currentScroll > 0) {
                    var scrollTop = currentScroll + thisTop - origTop - $(this).parent().children().first().height();
                } else {
                    var scrollTop = thisTop - origTop - $(this).parent().children().first().height();
                }
                $('div.offcanvas-body').animate({
                    scrollTop: scrollTop
                }, 500);
            }
        });
    });
    // $(document).on('click', 'button.gamegroup-button', function (e) { 
    //     $('div.offcanvas-body ').animate({
    //         scrollTop: $(this).offset().top
    //     }, 1000);
    // });

    $("#filter_games_menu").on("keyup", function () {
        var searchValue = $(this).val().toLowerCase();
        if (searchValue.length > 3) {
            $(document).find("button.gamegroup-button").filter(function () {
                var divId = $(this).attr("data-bs-target");
                if ($(this).text().toLowerCase() > -1 && !$(divId).hasClass("show")) {
                    $(this).click();
                } else {
                    var allA = $(divId).find("a");
                    var somethingFound = false;
                    for (let i = 0; i < allA.length; i++) {
                        if ($(allA[i]).text().toLowerCase().indexOf(searchValue) > -1) {
                            somethingFound = true;
                            $(allA[i]).show();
                        } else {
                            $(allA[i]).hide();
                        }
                    }
                    if (somethingFound) {
                        if (!$(divId).is(":visible")){
                            $(divId).parent().show();
                        }
                        if (!$(divId).hasClass("show")){
                            $(this).click();
                            $(this).removeClass("collapsed");
                        }
                    } else {
                        $(divId).parent().hide();
                        $(divId).removeClass("show");
                    }
                }
            });
        } else {
            $(document).find("button.gamegroup-button").filter(function () {
                $(this).collapse('hide');
                var divId = $(this).attr("data-bs-target");
                $(divId).find("a").filter(function () {
                    $(this).show();
                });
                var divId = $(this).attr("data-bs-target");
                $(divId).parent().show();
                $(divId).collapse('hide');
            })
        }
    });

    $("#filter_songs").on("keyup", function () {
        var searchValue = $(this).val().toLowerCase();
        if (searchValue.length > 3) {
            $("#game_stuff").find("tr:has(td):not(.subgroup-separator)").filter(function () {
                if ($(this).text().toLowerCase().indexOf(searchValue) > -1) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        } else {
            $("#game_stuff").find("tr:has(td)").filter(function () {
                $(this).show();
            });
        }
    });

    $(document).on('click', 'a.gamegroup', function (e) {
        console.log("jesus");
        e.preventDefault();
        $('.gamegroup.active').each(function () {
            $(this).removeClass('active');
            //we get rid of any previously active groups
        });
        $(this).addClass('active');
        var aId = $(this).attr('data-el_id');
        $(document).find('ul.subgroup[data-el_id="' + aId + '"]').each(function () {
            $(this).removeClass('visually-hidden');
            //we remove visually-hidden class from subgroups that belong to this group
        });
        $(document).find('ul.subgroup:not([data-el_id="' + aId + '"])').each(function () {
            $(this).addClass('visually-hidden');
            //and we hide subgroups that belong to other groups
        });
        $(this).removeClass('visually-hidden');
        var div = $($(this).attr('href'))[0];
        $(div).addClass('active');
        $(div).removeClass('visually-hidden');
        $(div).find("button").first().click();
        activeSubgroups = 1;
    });
    $(document).on('click', 'button.subgroup', function (e) {
        //handling clicking on subgroup
        e.preventDefault();
        var groupId = $(this).attr('data-group_id');
        if ($(this).text() == "All") {
            $('.subgroup.active').each(function () {
                $(this).removeClass('active');
                //we again remove active mark from any active subgroup
            });
            // $(document).find('tr:has(td)').each(function () {
            //     $(this).removeClass('visually-hidden');
            //     //first we hide all rows
            // });
            if ($(this).attr("data-gameGroupTxt") == "All") {
                $(document).find('tr').each(function () {
                    $(this).removeClass('visually-hidden');
                    //first we hide all rows
                });
            } else {
                if (groupId != undefined) {
                    $(document).find('tr:has(td):not([data-group_id="' + groupId + '"])').each(function () {
                        $(this).addClass('visually-hidden');
                        //first we hide all rows
                    });
                    $(document).find('tr:has(td)[data-group_id="' + groupId + '"]').each(function () {
                        $(this).removeClass('visually-hidden');
                        //first we hide all rows
                    });
                }
            }
            activeSubgroups = 1;
        } else {
            var aId = $(this).attr('id');
            var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
            if (allSubgroup.hasClass('active')) {
                allSubgroup.removeClass('active');
                //$(document).find('tr[data-group_id="' + aId + '"]');
                /*
                $(document).find('tr:has(td)').each(function () {
                    $(this).addClass('visually-hidden');
                    //first we hide all rows
                });
                */
                $(document).find('tr:has(td):not([data-el_id="' + aId + '"])').each(function () {
                    $(this).addClass('visually-hidden');
                    //first we hide all rows
                });
                activeSubgroups--;
                //DODAC GROUP ID DO TYCH SEPARATOROW
            }
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).addClass('visually-hidden');
                });
                activeSubgroups--;
            } else {
                $(this).addClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                activeSubgroups++
            }
            if (activeSubgroups == 0) {
                var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
                $(allSubgroup).click();
                activeSubgroups == 1;
                return;
            }
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") == "All") {
            $(this).addClass('active');
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") != "All") {
            //we are in not-root subgroup as i call it
            console.log('zonk');
            $(document).find('tr:has(td)').not('.subgroup-separator').each(function () {
                $(this).addClass('visually-hidden');
                //first we hide all rows
            });
            $(e.target.parentElement.parentElement).find("button").each(function () {
                //then for each subgroup we remove this hidden class
                var aId = $(this).attr('id');
                $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                var aText = $(this).text();

            });
            $(this).addClass('active');
        } else {
            //we're in main group and main subgroup so we just make basically everything visible
            /*
            var aId = $(this).attr('id');
            $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                $(this).removeClass('visually-hidden');
            });
            $(document).find('tr:has(td):not([data-el_id=' + aId + '])').each(function () {
                $(this).addClass('visually-hidden');
            });
            */
        }
    });

    $('#videoModal').on('shown.bs.modal', function (e) {
        console.log("japierdole");
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#video").attr('src', baseVideoSrc + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0");
    })

    $('#videoModal').on('hide.bs.modal', function (e) {
        // a poor man's stop video
        console.log("japierdole2");
        $("#video").attr('src', baseVideoSrc);
    })

    /*
    function autoPlayYouTubeModal() {
        var triggerOpen = $('body').find('[data-tagVideo]');
        triggerOpen.click(function () {
            var theModal = $(this).data('bs-target'),
                videoSRC = $(this).attr('data-tagVideo'),
                videoSRCauto = 'https://www.youtube.com/embed/' + videoSRC + '?autoplay=1';
            $('body').css('overflow', 'inherit');
            $(theModal + ' iframe').attr('src', videoSRCauto);
            $(theModal + ' button.btn-close').click(function () {
                $(theModal + ' iframe').attr('src', videoSRC);
            });
            $('#videoModal').click(function (ev) {
                var clickTarget = ev.originalEvent.target.className;
                if (clickTarget == "modal-header"){
                    return;
                } if (clickTarget != "modal-body") {
                    $(theModal + ' iframe').attr('src', videoSRC);
                } 
            });
        });
    }
    */

    //when loading list of songs, we want to trigger main group by default
    console.log("freeman");
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).first().click();
        //we have to hit first subgroup in main group to have stuff displayed
    }
});
