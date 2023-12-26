$(document).ready(function () {

    if (window.location.href.indexOf("/home") > -1) {
        $("#nfs-top-home").parent().addClass("nfs-top-item-active");
    }

    $('.play_icon').mouseover(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny", "znakwodny2"));
    }).mouseout(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
    });

    $(document).find("div.accordion-collapse").each(function () {
        $(this).on('shown.bs.collapse', function () {
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

        });
    });
    // $(document).on('click', 'button.gamegroup-button', function (e) { 
    //     $('div.offcanvas-body ').animate({
    //         scrollTop: $(this).offset().top
    //     }, 1000);
    // });

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
                //DODAC GROUP ID DO TYCH SEPARATOROW
            }
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).addClass('visually-hidden');
                });
            } else {
                $(this).addClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
            }
        }


        /*
                $('.subgroup.active').each(function () {
                    $(this).removeClass('active');
                    //we again remove active mark from any active subgroup
                });
                $(this).addClass('active');
                $(this).removeClass('visually-hidden');
                if (e.target.textContent != "All"){
                    console.log('zonk');
                    $(document).find('tr:has(td)').each(function () {
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
                        //and we append the subgroup separator so one can know which song belongs to what subgroup
                        $('<tr><td colspan="5">' + aText + '</td></tr>').insertBefore(
                            $(document).find('tr[data-el_id="' + aId + '"]').first()
                        );
                    });
                }
        */
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
                if (ev.originalEvent.target.className != "modal-body") {
                    $(theModal + ' iframe').attr('src', videoSRC);
                }
            });
        });
    }

    //when loading list of songs, we want to trigger main group by default
    console.log("freeman");
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).first().click();
        //we have to hit first subgroup in main group to have stuff displayed
    }
    autoPlayYouTubeModal();
});
