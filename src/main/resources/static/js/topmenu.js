var currentBg = 0;
$(document).ready(function () {
    changeStuffForDynamicTheme();
    var gameMatch = window.location.pathname.match(/\/game\/([^\/]+)/);
    if (gameMatch) {
        $('#limitToGame').val(gameMatch[1]);
        $('#gameFilterDiv').show();
    }

    $('#hide-icons').multiselect({
        includeSelectAllOption: true,
        templates: {
            button: '<button type="button" class="form-select multiselect dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false"><span class="multiselect-selected-text"></span></button>',
        },
    });
    if (localStorage.getItem("lang")!=undefined){
        var lang = localStorage.getItem("lang");
        var foundImg = $(document).find("img[data-countryval='"+lang+"']").first()[0].src;
        $("#dropdownCountryImg")[0].src=foundImg;
    } 

    /**
     * method to save preferences
     */

    $(document).on("click", "#save-preferences", function () {
        /*putting all stuff to the localstorage and reloading page as someone might have decided to change language*/
        let localStorageInfo = new Object();
        localStorageInfo.expandableWidth = $("#expandable-width").val();
        localStorageInfo.scrollingStuff = $("#scrolling-stuff").prop("checked");
        localStorageInfo.staticLeftMenu = $("#static-leftmenu").prop("checked");
        localStorageInfo.iconsSize = $("#icons-size").val();
        localStorageInfo.contentWidth = $("#content-width").val();
        localStorageInfo.videoRenderingStuff = $("#video-rendering-stuff").prop("checked");
        localStorageInfo.hideIcons = JSON.stringify($("#hide-icons").val());
        localStorageInfo.hideFlags = $("#hide-flags").prop("checked");
        localStorageInfo.allGames = $("#all-games").prop("checked");
        localStorageInfo.composersFlagsOnly = $("#composers-flags-only").prop("checked");
        localStorageInfo.lang = $("#lang-select").val();
        localStorage.setItem("expandable-width", localStorageInfo.expandableWidth);
        localStorage.setItem("scrolling-stuff", localStorageInfo.scrollingStuff);
        localStorage.setItem("static-leftmenu", localStorageInfo.staticLeftMenu);
        localStorage.setItem("icons-size", localStorageInfo.iconsSize);
        localStorage.setItem("content-width", localStorageInfo.contentWidth);
        localStorage.setItem("video-rendering-stuff", localStorageInfo.videoRenderingStuff);
        localStorage.setItem("hide-icons", localStorageInfo.hideIcons);
        localStorage.setItem("hide-flags", localStorageInfo.hideFlags);
        localStorage.setItem("all-games", localStorageInfo.allGames);
        localStorage.setItem("composers-flags-only", localStorageInfo.composersFlagsOnly);
        localStorage.setItem("lang", $("#lang-select").val());
        $.ajax({
            url: "/preferences",
            type: "POST",
            data: JSON.stringify(localStorageInfo),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(lyrics) {
                console.log("saved preferences to session");
            },
            error: function() {
                console.log("something wrong in session");
            }
        });
        var langAlreadyThere = window.location.toString().indexOf("?lang") > -1;
        if (langAlreadyThere) {
            window.location.search = "?lang=" + $("#lang-select").val();
        } else {
            window.location = window.location + "?lang=" + $("#lang-select").val();
        }
    });

    $(document).on("click", "#copy-overlay-url", function () {
        var url = $("#streaming-overlay-url").val();
        if (url) {
            navigator.clipboard.writeText(url).catch(function() {
                $("#streaming-overlay-url").select();
                document.execCommand("copy");
            });
        }
    });

    $(document).on("click", "#openPreferences", function () {
        var streamingToken = localStorage.getItem("streaming-token");
        if (!streamingToken) {
            streamingToken = crypto.randomUUID();
            localStorage.setItem("streaming-token", streamingToken);
        }
        $("#streaming-overlay-url").val(window.location.origin + "/streaming/overlay/" + streamingToken);

        var scrolling = localStorage.getItem("scrolling-stuff");
        if (scrolling != undefined) {
            $("#scrolling-stuff").prop("checked", JSON.parse(scrolling));
        }
        var renderingVideo = localStorage.getItem("video-rendering-stuff");
        if (renderingVideo != undefined) {
            $("#video-rendering-stuff").prop("checked", JSON.parse(renderingVideo));
        }
        var hideFlags = localStorage.getItem("hide-flags");
        if (hideFlags != undefined) {
            $("#hide-flags").prop("checked", JSON.parse(hideFlags));
        }
        var staticMenu = localStorage.getItem("static-leftmenu");
        if (staticMenu != undefined) {
            $("#static-leftmenu").prop("checked", JSON.parse(staticMenu));
        }
        var expandWidth = localStorage.getItem("expandable-width");
        if (expandWidth != undefined) {
            $("#expandable-width").val(expandWidth);
        }
        var iconsSize = localStorage.getItem("icons-size");
        if (iconsSize != undefined) {
            $("#icons-size").val(iconsSize);
        }
        var contentWidth = localStorage.getItem("content-width");
        if (contentWidth != undefined) {
            $("#content-width").val(contentWidth);
        }
        var lang = localStorage.getItem("lang");
        if (lang != undefined) {
            $("#lang-select").val(lang);
        }
        var hideIcons = JSON.parse(localStorage.getItem("hide-icons"));
        if (hideIcons != undefined) {
            $("#hide-icons").multiselect('select', hideIcons);
        }
        var allGames = localStorage.getItem("all-games");
        if (allGames != undefined) {
            $("#all-games").prop("checked", JSON.parse(allGames));
        }
        var composersFlagsOnly = localStorage.getItem("composers-flags-only");
        if (composersFlagsOnly != undefined) {
            $("#composers-flags-only").prop("checked", JSON.parse(composersFlagsOnly));
        }
    });

    /**
     * function to switch night mode (when you click outside checkbox) and save it in local storage
     */
    $("#nightModeSwitch").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", !$(this).prev().prop("checked"));
        $(this).prev().click();
    });

    $("#sunlight").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", true);
        localStorage.removeItem("dynamic-bg");
        $("#moonlight").css("display", "");
        $("#sunlight").css("display", "none");
        changeStuffForDarkMode();
        changeStuffForDynamicTheme();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });

    $("#moonlight").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", false);
        localStorage.removeItem("dynamic-bg");
        $("#moonlight").css("display", "none");
        $("#sunlight").css("display", "");
        changeStuffForDarkMode();
        changeStuffForDynamicTheme();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });
    /**
     * function to switch night mode (when you click inside checkbox) and reload disqus due to that
     */
    $("#flexSwitchCheckDefault").change(function (e) {
        localStorage.setItem("dark-mode", $(this).prop("checked"));
        changeStuffForDarkMode();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });

    $(document).on('click', '#dynamic_bg', function (e) {
        e.preventDefault();
        if (localStorage.hasOwnProperty("dynamic-bg")) {
            currentBg = (Number(localStorage.getItem("dynamic-bg-index")) + 1) % 6;
        } else {
            currentBg = (currentBg + 1) % 6;
        }
        if (currentBg == 0) {
            localStorage.removeItem("dynamic-bg");
            localStorage.setItem("dynamic-bg-index", 0);
            changeStuffForDarkMode();
            changeStuffForDynamicTheme();
        } else {
            $.ajax({
                async: false,
                type: "GET",
                url: "/dynamictheme/" + currentBg,
                success: function (ooo) {
                    localStorage.setItem("dynamic-bg", ooo);
                    localStorage.setItem("dynamic-bg-index", currentBg);
                    var theme = JSON.parse(ooo);
                    changeStuffForDarkMode(theme.nightMode);
                    changeStuffForDynamicTheme();
                },
            });
        }
    });

    $(document).on("click", "a.translate-link", function () {
        var langValue = $(this).children().first().attr("data-countryval");
        localStorage.setItem("lang", langValue);
        var langAlreadyThere = window.location.toString().indexOf("?lang") > -1;
        if (langAlreadyThere) {
            window.location.search = "?lang=" + langValue;
        } else {
            window.location = window.location + "?lang=" + langValue;
        }
    });

    $(document).on('submit', '#recognitionForm', function (e) {
        e.preventDefault();
        submitRecognitionForm(new FormData(this));
    });

    function submitRecognitionForm(fd) {
        $('#recognizeSpinner').show();
        $('#recordSpinner').show();
        $('#recognizeBtn').prop('disabled', true);

        fetch('/recognize', { method: 'POST', body: fd })
            .then(function (r) { return r.json(); })
            .then(function (data) {
                if (!data.jobId) { showRecognitionError('Unexpected response'); return; }
                pollRecognitionJob(data.jobId);
            })
            .catch(function (e) { showRecognitionError(e.message); });
    }

    function pollRecognitionJob(jobId) {
        fetch('/recognize/status/' + jobId)
            .then(function (r) { return r.json(); })
            .then(function (data) {
                if (data.status === 'DONE') {
                    window.location.href = '/recognize/result/' + jobId;
                } else if (data.status === 'ERROR' || data.status === 'NOT_FOUND') {
                    showRecognitionError(data.error || 'Recognition failed');
                } else {
                    setTimeout(function () { pollRecognitionJob(jobId); }, 5000);
                }
            })
            .catch(function (e) { showRecognitionError(e.message); });
    }

    function showRecognitionError(msg) {
        $('#recognizeSpinner').hide();
        $('#recordSpinner').hide();
        $('#recognizeBtn').prop('disabled', false);
        resetRecordUI();
        $('#recordMsg').text($('#recognitionModal').data('submitFailed') + ': ' + msg);
    }

    // --- audio recording for recognition modal ---

    var mediaRecorder = null;
    var recordChunks = [];
    var recordTimerInterval = null;
    var recordElapsed = 0;
    var MAX_RECORD_SECONDS = 15;

    function recordPad(n) { return String(n).padStart(2, '0'); }

    function recordUpdateTimer() {
        recordElapsed++;
        var m = Math.floor(recordElapsed / 60);
        var s = recordElapsed % 60;
        $('#recordTimer').text(m + ':' + recordPad(s));
        $('#recordProgress').css('width', Math.min(100, (recordElapsed / MAX_RECORD_SECONDS) * 100) + '%');
        if (recordElapsed >= MAX_RECORD_SECONDS) {
            stopRecording();
        }
    }

    function startRecording(stream) {
        recordChunks = [];
        mediaRecorder = new MediaRecorder(stream, { audioBitsPerSecond: 256000 });
        mediaRecorder.ondataavailable = function (e) {
            if (e.data.size > 0) recordChunks.push(e.data);
        };
        mediaRecorder.onstop = function () {
            stream.getTracks().forEach(function (t) { t.stop(); });
            var blob = new Blob(recordChunks, { type: mediaRecorder.mimeType });
            submitRecording(blob, mediaRecorder.mimeType);
        };
        mediaRecorder.start(100);
        recordElapsed = 0;
        $('#recordTimer').text('0:00');
        $('#recordProgress').css('width', '0%');
        $('#recordBtns').hide();
        $('#recordingStatus').show();
        $('#recordMsg').text('');
        recordTimerInterval = setInterval(recordUpdateTimer, 1000);
    }

    function stopRecording() {
        clearInterval(recordTimerInterval);
        $('#recordingStatus').hide();
        $('#recordSpinner').show();
        if (mediaRecorder && mediaRecorder.state !== 'inactive') {
            mediaRecorder.stop();
        }
    }

    function resetRecordUI() {
        $('#recordBtns').show();
        $('#recordingStatus').hide();
        $('#recordSpinner').hide();
        $('#sysAudioHint').hide();
        recordElapsed = 0;
    }

    function submitRecording(blob, mimeType) {
        var ext = mimeType.indexOf('ogg') !== -1 ? 'ogg'
            : mimeType.indexOf('mp4') !== -1 ? 'mp4' : 'webm';
        try {
            var fd = new FormData($('#recognitionForm')[0]);
            fd.set('audio', new File([blob], 'recording.' + ext, { type: mimeType }));
            submitRecognitionForm(fd);
        } catch (e) {
            resetRecordUI();
            $('#recordMsg').text($('#recognitionModal').data('submitFailed') + e.message);
        }
    }

    $(document).on('click', '#recordMicBtn', function () {
        $('#recordMsg').text('');
        $('#sysAudioHint').hide();
        navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: false,
                noiseSuppression: false,
                autoGainControl: false,
                channelCount: 1,
                sampleRate: 44100
            }
        })
            .then(function (stream) { startRecording(stream); })
            .catch(function (e) {
                $('#recordMsg').text($('#recognitionModal').data('micDenied') + e.message);
            });
    });

    $(document).on('click', '#recordSysBtn', function () {
        $('#recordMsg').text('');
        $('#sysAudioHint').show();
        navigator.mediaDevices.getDisplayMedia({ video: false, audio: true })
            .then(function (stream) {
                stream.getVideoTracks().forEach(function (t) { t.stop(); });
                if (stream.getAudioTracks().length === 0) {
                    resetRecordUI();
                    $('#recordMsg').text($('#recognitionModal').data('noAudio'));
                    return;
                }
                startRecording(stream);
            })
            .catch(function (e) {
                resetRecordUI();
                $('#recordMsg').text($('#recognitionModal').data('sysDenied') + e.message);
            });
    });

    $(document).on('click', '#stopRecordBtn', stopRecording);

    $('#recognitionModal').on('hidden.bs.modal', function () {
        clearInterval(recordTimerInterval);
        if (mediaRecorder && mediaRecorder.state !== 'inactive') {
            mediaRecorder.stop();
        }
        resetRecordUI();
        $('#recordMsg').text('');
    });

});