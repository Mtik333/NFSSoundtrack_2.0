$(document).ready(function () {
    $("#rebuild-band-graph").on("click", function () {
        var $btn = $(this);
        var $status = $("#rebuild-band-graph-status");

        $btn.prop("disabled", true).text("Rebuilding...");
        $status.text("").removeClass("text-success text-danger");

        $.ajax({
            url: "/api/bands/admin/graph/rebuild",
            type: "POST",
            success: function (data) {
                var result = typeof data === "string" ? JSON.parse(data) : data;
                $status.addClass("text-success")
                       .text("Done — " + result.bandCount + " bands loaded");
            },
            error: function (xhr) {
                $status.addClass("text-danger")
                       .text("Failed: " + xhr.status + " " + xhr.statusText);
            },
            complete: function () {
                $btn.prop("disabled", false).text("Rebuild band connection graph");
            }
        });
    });
});
