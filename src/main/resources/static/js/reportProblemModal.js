$(document).ready(function () {

    $(document).on("click", "#submit-correction", function () {
        var correction = new Object();
        correction.affectedSongsubgroup = Number($("#affected-songsubgroup").val());
        correction.problemType = $("#problem-type").val();
        correction.rightValue = $("#right-value").val();
        correction.discordId = $("#discord-id").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(correction),
            url: "/correction",
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                alert("success");
                console.log("argh");
            },
            error: function (ooo) {
                alert("fail");
                console.log("eehehehe");
            },
        });
    });

});