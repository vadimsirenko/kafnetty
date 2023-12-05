(function () {
    let chat = {
        "addMessage": function () {
            this.messageToSend = this.$textarea.val();
            this.sendMessage();
        },
        "addMessageEnter": function (event) {
            // enter was pressed
            if (event.keyCode === 13) {
                this.addMessage();
            }
        },
        "bindEvents": function () {
            // Chat events
            this.$button.on('click', this.addMessage.bind(this));
            this.$textarea.on('keyup', this.addMessageEnter.bind(this));
            // Room events
            this.$roomSetList.on("click", "li", this.chatClick.bind(this));
            this.$createRoomFormCancel.on("click", this.createRoomFormCancel.bind(this));
            this.$createRoomFormOk.on("click", this.createRoomFormOk.bind(this));
            this.$roomAddButton.on("click", this.createRoom.bind(this));
            // Sign-in sign-up events
            this.$signUpForm.find('.btn').on("click", this.signUp.bind(this));
            // profile events
            this.$profileButton.on("click", this.updateProfile.bind(this));
            this.$profileFormCancel.on("click", this.updateProfileCancel.bind(this));
            this.$profileFormOk.on("click", this.updateProfileOk.bind(this));

            $(".sign_up_li").click(function () {
                $(this).addClass('active');
                $(".sign_in_li").removeClass('active');
                $(".sign_up").show();
                $(".sign_in").hide();
            });

            $(".sign_in_li").click(function () {
                $(this).addClass('active');
                $(".sign_up_li").removeClass('active');
                $(".sign_in").show();
                $(".sign_up").hide();
            });

            $(".sign_up").validate(
                {
                    errorClass: "error fail-alert",
                    validClass: "valid success-alert",
                    errorElement: "div",
                    rules: {
                        fullName: {
                            required: true,
                            minlength: 3
                        },
                        nickName: {
                            required: true,
                            minlength: 3
                        },
                        password: {
                            required: true,
                            minlength: 5
                        },
                        email: {
                            required: true,
                            email: true
                        }
                    },
                    messages: {
                        fullName: {
                            required: "The Name is required",
                            minlength: "The Name must be at least 3 characters long"
                        },
                        nickName: {
                            required: "The Nick name is required",
                            minlength: "The nNick name must be at least 3 characters long"
                        },
                        password: {
                            required: "The Password is required",
                            minlength: "The Password must be at least 5 characters long"
                        },
                        email: {
                            required: "The Email is required",
                            email: "The Email must have the format: abc@domain.tld"
                        }
                    }
                }
            );
            $(".sign_in").validate(
                {
                    errorClass: "error fail-alert",
                    validClass: "valid success-alert",
                    errorElement: "div",
                    rules: {
                        password: {
                            required: true
                        },
                        email: {
                            required: true
                        }
                    },
                    messages: {
                        password: {
                            required: "The Password is required",
                        },
                        email: {
                            required: "The Email is required"
                        }
                    }
                }
            );
        },
        "cacheDOM": function () {
            // Chat controls
            this.$chatHistory = $('.chat-history');
            this.$roomSet = $('.room-set');
            this.$button = $('.chat-message button');
            this.$textarea = $('#message-to-send');
            this.$chatAbout = $('.chat-about');
            this.$chatHistoryList = this.$chatHistory.find('ul');
            // Room controls
            this.$roomSetList = this.$roomSet.find('ul');
            this.$createRoomForm = $('#create-room');
            this.$createRoomShowElements = $('#create-room, .fidebox');
            this.$createRoomFormCancel = this.$createRoomForm.find('#create-room-cancel');
            this.$createRoomFormOk = this.$createRoomForm.find('#create-room-ok');
            this.$roomNameInput = this.$createRoomForm.find('#create-room-name');
            this.$createRoomValidate = this.$createRoomForm.find('.validate-error');
            this.$roomAddButton = $('.cell-add');
            // Sign-in sign-up controls
            this.$signUpForm = $('.sign_up');
            // profile controls
            this.$profileForm = $('#profile-form');
            this.$profileFormOk = this.$profileForm.find('#profile-form-ok');
            this.$profileFormCancel = this.$profileForm.find('#profile-form-cancel');
            this.$profileFormNikname = this.$profileForm.find('#nikname');
            this.$profileFormEmail = this.$profileForm.find('#email');
            this.$profileFormValidate = this.$profileForm.find('.validate-error');
            this.$profileFormShowElements = $('#profile-form, .fidebox');
            this.$profileButton = $('.chat-header .kn-cog');

            // logout controls
            this.$logoutButton = $('.chat-header .kn-exit');
        },
        "chatClick": function (event) {
            let roomId = $(event.target).attr('data-id')
            if (roomId === undefined)
                return;
            this.updateTitleChat();
            console.log("roomId=" + roomId);
            this.goToRoom(roomId);
            this.coloredChatList();
        },
        "coloredChatList": function () {
            this.$roomSetList.find('.chat-item').css({"font-weight": "normal", "color": "white"});
            if (this.roomId === null)
                return;
            this.$roomSetList.find('.chat-item[data-id=' + this.roomId + ']').css({
                "font-weight": "bold",
                "color": "#E38968"
            });
            this.updateTitleChat(this.$roomSetList.find('.chat-item[data-id=' + this.roomId + ']').text());
        },
        "convertFormToJSON": function (form) {
            const array = $(form).serializeArray(); // Encodes the set of form elements as an array of names and values.
            const json = {};
            $.each(array, function () {
                json[this.name] = this.value || "";
            });
            return JSON.stringify(json);
        },
        "createRoom": function () {
            this.$roomNameInput.val('');
            this.$createRoomShowElements.fadeIn('slow');
        },
        "createRoomFormCancel": function () {
            this.$createRoomValidate.text('');
            this.$createRoomValidate.hide();
            this.$createRoomShowElements.hide();
        },
        "createRoomFormOk": function () {
            if (this.$roomNameInput.val().trim() === '' || !window.WebSocket) {
                return;
            }
            try {
                if (this.socket.readyState === WebSocket.OPEN) {

                    let message = {
                        "id": this.uuidv4(),
                        "messageType": "ROOM",
                        "operationType": "CREATE",
                        "ts": new Date().getTime(),
                        "name": this.$roomNameInput.val().trim()
                    };
                    let messageJSON = JSON.stringify(message);
                    this.socket.send(messageJSON);
                    this.$roomNameInput.val('');
                    this.$createRoomValidate.text('');
                    this.$createRoomValidate.hide();
                    this.$createRoomShowElements.hide();
                } else {
                    this.$createRoomValidate.text("The socket is not open.");
                    this.$createRoomValidate.show();
                }
            } catch (e) {
                this.$createRoomValidate.text(e);
                this.$createRoomValidate.show();
            }
        },
        "createWS": function (data) {
            if (!window.WebSocket) {
                window.WebSocket = window.MozWebSocket;
            }
            if (window.WebSocket) {
                try {
                    this.socket = new WebSocket("ws://" + window.location.host + "/websocket/?token=" + data.token);
                } catch (e) {
                    console.log("server unnavigable");
                    console.log(e.messageText);
                    //this.$loginFormValidate.text(e.messageText);
                    //this.$loginFormValidate.show();
                    return;
                }
                this.socket.addEventListener('message', this.receiveMessage.bind(this));
                this.socket.addEventListener('open', this.socketOpen.bind(this));
                this.socket.addEventListener('close', this.socketClose.bind(this));
                this.socket.addEventListener('error', this.socketError.bind(this));
            }
        },
        "email": null,
        "getFormattedTime": function (date) {
            return date.toLocaleString();
        }, "goToRoom": function (roomId, force = false) {
            this.scrollToBottom();
            if (roomId != null && (this.roomId !== roomId) || force === true) {
                this.roomId = roomId;
                if (!window.WebSocket) {
                    return;
                }
                if (this.socket.readyState === WebSocket.OPEN) {

                    let request = {
                        "id": this.uuidv4(),
                        "messageType": "MESSAGE_LIST",
                        "operationType": "RECEIVE",
                        "senderId": this.senderId,
                        "ts": new Date().getTime(),
                        "roomId": roomId
                    };
                    let requestJSON = JSON.stringify(request);
                    this.socket.send(requestJSON);
                } else {
                    alert("The socket is not open.");
                }
            }
        },
        "init": function () {
            this.cacheDOM();
            this.bindEvents();
            this.loginUser();
        },
        "loginUser": function () {
            this.resetUser();
            this.clearSignFormInputValues();
            this.showLoginForm();
        },
        "messageToSend": '',
        "messagesLength": 0,
        "nickName": null,
        "processClientProfile": function (data) {
            this.senderId = data.id;
            this.nickName = data.nickName;
            this.email = data.email;
            if (data.roomId != null) {
                this.roomId = data.roomId;
                this.coloredChatList();
                this.goToRoom(data.roomId, true);
            }
        },
        "processInfo": function (info) {
            this.$chatHistoryList.append(this.renderedInfoMessage(info));
            this.scrollToBottom();
        },
        "processMessage": function (message) {
            if (this.$chatHistoryList.find('[message-id=' + message.id + ']').length) {
                return;
            }
            this.messagesLength = this.messagesLength + 1;
            this.updateTitleChat();
            this.$chatHistoryList.append(this.renderedMessage(message));
            this.scrollToBottom();
        },
        "processMessageList": function (operationType, messages) {
            this.$chatHistoryList.empty();
            this.messagesLength = messages.length;
            this.updateTitleChat();
            for (let i = 0; i < messages.length; i++) {
                this.$chatHistoryList.append(this.renderedMessage(messages[i]));
            }
            this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
        },
        "processRoom": function (room) {
            if (!this.$roomSetList.find('.chat-item[data-id=' + room.id + ']').length) {
                let templateRoom = Handlebars.compile($("#room-item-template").html());
                let contextResponse = {
                    "name": room.name,
                    "id": room.id
                };
                this.$roomSetList.append(templateRoom(contextResponse));
                searchRoomFilter.init();
                this.coloredChatList();
            }
        },
        "processRoomList": function (operationType, rooms) {
            let templateRoomList = Handlebars.compile($("#room-set-item-template").html());
            this.$roomSetList.empty();
            this.$roomSetList.append(templateRoomList({"objects": rooms}));
            searchRoomFilter.init();
            this.coloredChatList();
        },
        "receiveMessage": function (event) {
            let data = JSON.parse(event.data);

            if (data.messageType === "MESSAGE") {
                console.log(data);
                this.processMessage(data);
            }
            if (data.messageType === "ROOM_LIST") {
                console.log(data);
                this.processRoomList(data.operationType, data.rooms);
            }
            if (data.messageType === "ROOM") {
                console.log(data);
                this.processRoom(data);
            }
            if (data.messageType === "CLIENT") {
                console.log(data);
                this.processClientProfile(data);
            }
            if (data.messageType === "INFO") {
                console.log(data);
                this.processInfo(data);
            }
            if (data.messageType === "MESSAGE_LIST" && data.roomId === this.roomId) {
                this.processMessageList(data.operationType, data.messages);
            }
        },
        "renderedInfoMessage"(info) {
            let templateinfoMesssage = null;
            if (info.operationType === "LOGON") {
                templateinfoMesssage = Handlebars.compile($("#message-logon-info-template").html());
            } else if (info.operationType === "LOGOFF") {
                templateinfoMesssage = Handlebars.compile($("#message-logoff-info-template").html());
            } else {
                templateinfoMesssage = Handlebars.compile($("#message-info-template").html());
            }
            let time_ = this.getFormattedTime(new Date(info.ts));
            let contextResponse = {
                "messageText": info.messageText,
                "time": time_
            };
            return templateinfoMesssage(contextResponse);
        },
        "renderedMessage"(message) {
            let templateResponse = Handlebars.compile($("#message-response-template").html());
            if (message.senderId === this.senderId) {
                templateResponse = Handlebars.compile($("#message-template").html());
            }
            let contextResponse = {
                "id": message.id,
                "login": message.sender,
                "messageText": message.messageText,
                "time": this.getFormattedTime(new Date(message.ts))
            };
            return templateResponse(contextResponse);
        },
        "resetUser": function () {
            this.email = null;
            this.roomId = null;
            this.messageToSend = '';
            this.messagesLength = 0;
            this.nickName = null;
            this.senderId = null;
            if (window.WebSocket && this.socket && this.socket.readyState === WebSocket.OPEN) {
                this.socket.close();
                this.socket = null;
            }
            this.$textarea.val('');
            this.$chatHistoryList.empty();
            this.$roomSetList.empty();
            this.$chatAbout.html('');
            this.clearSignFormInputValues();
            this.showLoginForm();
        },
        "roomId": null,
        "scrollToBottom": function () {
            this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
        },
        "sendMessage": function () {
            this.scrollToBottom();
            if (this.messageToSend.trim() !== '' && this.roomId != null) {

                if (!window.WebSocket || this.messageToSend.trim() === "") {
                    return;
                }
                if (this.socket.readyState === WebSocket.OPEN) {

                    let message = {
                        "id": this.uuidv4(),
                        "messageType": "MESSAGE",
                        "operationType": "CREATE",
                        "ts": new Date().getTime(),
                        "messageText": this.messageToSend,
                        "roomId": this.roomId,
                        "senderId": this.senderId,
                        "sender": this.nickName
                    };
                    let messageJSON = JSON.stringify(message);
                    this.socket.send(messageJSON);
                    this.$textarea.val('');

                } else {
                    alert("The socket is not open.");
                }
            }
        },
        "senderId": null,
        "signUp": function (src) {
            let form = $(src.target.closest('form'));
            if (form.valid()) {
                let form_data = this.convertFormToJSON(form);
                console.log(form_data);

                $.ajax({
                    "url": window.location.origin + '/logon',
                    "method": 'POST',
                    "data": form_data,
                    "dataType": 'json',
                    "success": function (data) {
                        this.createWS(data)
                    }.bind(this),
                    "error": function (jqXHR, exception) {
                        if (jqXHR.status === 0) {
                            alert('Not connect. Verify Network.');
                        } else if (jqXHR.status === 404) {
                            alert('Requested page not found (404).');
                        } else if (jqXHR.status === 500) {
                            alert('Internal Server Error (500).');
                        } else if (exception === 'parsererror') {
                            alert('Requested JSON parse failed.');
                        } else if (exception === 'timeout') {
                            alert('Time out error.');
                        } else if (exception === 'abort') {
                            alert('Ajax request aborted.');
                        } else {
                            alert('Uncaught Error. ' + jqXHR.responseText);
                        }
                    }
                });
            }
        },
        "socket": null,
        "socketClose": function () {
            console.log("websocket closed");
        },
        "socketError": function (error) {
            console.log('WebSocket error: ', error);
        },
        "socketOpen": function () {
            console.log("websocket opened");
            this.hideLoginForm();
        },
        "hideLoginForm": function () {
            this.clearSignFormInputValues();
            $('.form-wrapper, .fidebox').hide();
        },
        "showLoginForm": function () {
            this.clearSignFormInputValues();
            $(".sign_up").hide();
            $('.sign_in_li').addClass('active');
            $('.form-wrapper, .fidebox').fadeIn('slow');
        },
        "updateProfile": function () {
            if (this.senderId != null) {
                this.$profileFormNikname.val(this.nickName);
                this.$profileFormEmail.val(this.email);
                this.$profileFormValidate.text('');
                this.$profileFormValidate.hide();
                this.$profileFormShowElements.fadeIn('slow');
            }
        },
        "clearSignFormInputValues": function () {
            $('form input[type="text"],form input[type="password"],form input[type="email"]').val('');
        },
        "updateProfileCancel": function () {
            this.$profileFormNikname.val('');
            this.$profileFormEmail.val('');
            this.$profileFormValidate.text('');
            this.$profileFormValidate.hide();
            this.$profileFormShowElements.hide();
        },
        "updateProfileOk": function () {
            this.scrollToBottom();
            let nikname = this.$profileFormNikname.val().trim();
            let email = this.$profileFormEmail.val().trim();
            if (!window.WebSocket || nikname === "" || email === "") {
                return;
            }
            if (this.socket.readyState === WebSocket.OPEN) {

                let message = {
                    "id": this.senderId,
                    "messageType": "CLIENT",
                    "operationType": "UPDATE",
                    "email": email,
                    "nickName": nikname,
                    "login": null,
                    "roomId": this.roomId,
                    "ts": new Date().getTime()
                };
                let messageJSON = JSON.stringify(message);
                this.socket.send(messageJSON);
                this.$profileFormNikname.val('');
                this.$profileFormEmail.val('');
                this.$profileFormValidate.text('');
                this.$profileFormValidate.hide();
                this.$profileFormShowElements.hide();
            } else {
                alert("The socket is not open.");
            }
        },
        "updateTitleChat": function () {
            if (this.roomId === null)
                return;
            let chatName = this.$roomSetList.find('.chat-item[data-id=' + this.roomId + ']').text();
            if (chatName === null)
                return;
            let templateResponse = Handlebars.compile($("#chat-about-template").html());
            let countMessage = 'В чате пока нет сообщений';
            if (this.messagesLength != null && this.messagesLength > 0) {
                countMessage = "В чате уже " + this.messagesLength + " сообщений";
            }
            let contextResponse = {
                "chatName": chatName,
                "chatStatistic": countMessage
            };
            this.$chatAbout.html(templateResponse(contextResponse));
        },
        "uuidv4": function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        }
    };

    chat.init();
    searchRoomFilter = {
        options: {valueNames: ['name']},
        init: function () {
            const chatList = new List('chat-list', this.options);
            const noItems = $('<li id="no-items-found">No items found</li>');

            chatList.on('updated', function (list) {
                if (list.matchingItems.length === 0) {
                    $(list.list).append(noItems);
                } else {
                    noItems.detach();
                    chat.coloredChatList();
                }
            });
        }
    };
})();