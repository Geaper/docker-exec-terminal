var term;
var ws;

$(document).ready(function () {


    $('#btnConnect').click(function () {
        $('#btnConnect').html('<span class="fa fa-spinner fa-spin"></span> Connecting');
        var command = $('#command').val();
        var user = $('#user').val();
        $.ajax({
            url: '/connect-console',
            type: 'post',
            data: {containerName: containerName, command: command, user: user},
            success: function (url) {
                $('#connectDiv').hide();
                $('#terminalDiv').show();

                term = new Terminal({
                    screenKeys: true,
                    useStyle: true,
                    cursorBlink: true,
                    cols: 120,
                    rows: 35,
                    scrollback: 1500
                });

                ws = new WebSocket(url);
                term.open(document.getElementById("terminal"), true);
                term.attach(ws);
                term.focus();
            }
        });
    });

    var containerName;
    var containerNameUrl = $('#containerNameUrl').val();
    if(containerNameUrl !== "") {
        containerName = containerNameUrl;
    }
    else
        containerName = $('#containerName').val();

    $('#containerName').change(function () {
        containerName = $('#containerName').val();
    });


    $('#btnDisconnect').click(function () {
        ws.close();
        term.destroy();

        $('#connectDiv').show();
        $('#terminalDiv').hide();
        $('#btnConnect').html("Connect");
    });
});
