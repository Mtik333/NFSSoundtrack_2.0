$(document).ready(function () {

    //once we cancel reporting problem, let's clean up the discord user and value of correction
    $(document).on("click", "button.btn-close", function () {
        $("#discord-username").val("");
        $("#right-value").val("");
        $("#correction-audio").val("");
        $("#recognitionAudioDiv").hide();
    });

    $(document).on("click", "#submit-correction", function () {
        var fd = new FormData();
        fd.append("affectedSongsubgroup", parseInt($("#affected-songsubgroup").val()) || -1);
        fd.append("problemType", $("#problem-type").val());
        fd.append("rightValue", $("#right-value").val());
        fd.append("sourceUrl", $("#source-url").val());
        fd.append("discordUsername", $("#discord-username").val());
        var audioFile = $("#correction-audio")[0].files[0];
        if (audioFile) fd.append("audio", audioFile);
        $.ajax({
            async: false,
            type: "POST",
            data: fd,
            url: "/correction",
            contentType: false,
            processData: false,
            success: function () {
                $("#discord-username").val("");
                $("#right-value").val("");
                $("#correction-audio").val("");
                $("#recognitionAudioDiv").hide();
                $("#success-alert").fadeTo(500, 500).slideUp(1000, function () {
                    $("#success-alert").slideUp(1000, function () {
                        $("#reportProblemModal").modal('hide');
                    });
                });
            },
            error: function () {
                $("#failure-alert").fadeTo(500, 500).slideUp(1000, function () {
                    $("#failure-alert").slideUp(1000, function () {
                        $("#reportProblemModal").modal('hide');
                    });
                });
            }
        });
    });


    $(document).on("change", "#problem-type", function () {
        if ($(this).val() === "WRONG_RECOGNITION") {
            $("#recognitionAudioDiv").show();
        } else {
            $("#recognitionAudioDiv").hide();
            $("#correction-audio").val("");
        }
    });

});