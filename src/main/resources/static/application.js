var stompClient = null;

function setConnected(connected) {
    $("#disconnect").prop("disabled", !connected);
}

function connect(channel) {
    disconnect();

    $(".connect").removeClass("connected");
    $("#connect_" + channel).addClass("connected");

    var socket = new SockJS('/websocket');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/channel/' + channel, function (data) {
            showGreeting(JSON.parse(data.body));
        });
    });
}

function disconnect() {
    $(".connect").removeClass("connected");

    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


function showGreeting(data) {

    var date;
    var value;
    var nextUpdate = data.nextUpdate;


    $("#nextUpdate").html(nextUpdate);
    switch (data.type) {
        case "RANDOM":
            date = data.date;
            value = data.data;
            break;
        case "BITCOIN":
            var bitcoin = JSON.parse(data.data);
            date = bitcoin.time.updated;
            value = bitcoin.bpi.USD.rate_float;

            break
    }

    $("#date").html(date);
    $("#value").html(value);

}

$(function () {
    $("#connect_random").click(function () {
        connect('random');
    });
    $("#connect_bitcoin").click(function () {
        connect('bitcoin');
    });
    $("#disconnect").click(function () {
        disconnect();
    });
});

