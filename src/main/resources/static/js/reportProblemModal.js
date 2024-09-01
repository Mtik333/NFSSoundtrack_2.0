$(document).ready(function () {

    //once we cancel reporting problem, let's clean up the discord user and value of correction
    $(document).on("click", "button.btn-close", function () {
        $("#discord-username").val("");
        $("#right-value").val("");
    });

    $(document).on("click", "#submit-correction", function () {
        var correction = new Object();
        correction.affectedSongsubgroup = Number($("#affected-songsubgroup").val());
        correction.problemType = $("#problem-type").val();
        correction.rightValue = $("#right-value").val();
        correction.sourceUrl = $("#source-url").val();
        correction.discordUsername = $("#discord-username").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(correction),
            url: "/correction",
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                console.log("argh");
                $("#discord-username").val("");
                $("#right-value").val("");
                $("#success-alert").fadeTo(500, 500).slideUp(1000, function () {
                    $("#success-alert").slideUp(1000, function () {
                        $("#reportProblemModal").modal('hide');
                    });
                });
            },
            error: function (ooo) {
                $("#failure-alert").fadeTo(500, 500).slideUp(1000, function () {
                    $("#failure-alert").slideUp(1000, function () {
                        $("#reportProblemModal").modal('hide');
                    });
                });
            },
        });
    });

});