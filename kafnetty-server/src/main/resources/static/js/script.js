(function () {
    let chat = {
        addMessage: function () {
            this.messageToSend = this.$textarea.val();
            this.sendMessage();
        },
        addMessageEnter: function (event) {
            // enter was pressed
            if (event.keyCode === 13) {
                this.addMessage();
            }
        },
        bindEvents: function () {
            this.$button.on('click', this.addMessage.bind(this));
            this.$textarea.on('keyup', this.addMessageEnter.bind(this));
            this.$roomSetList.on("click", "li", this.chatClick.bind(this));
            this.$createRoomFormCancel.on("click", this.createRoomFormCancel.bind(this));
            this.$createRoomFormOk.on("click", this.createRoomFormOk.bind(this));
            this.$roomAddButton.on("click", this.createRoom.bind(this));
            this.$loginFormOk.on("click", this.loginFormOkClick.bind(this));

            this.$logoutButton.on("click", this.loginUser.bind(this));
            this.$profileButton.on("click", this.updateProfile.bind(this));
            this.$profileFormCancel.on("click", this.updateProfileCancel.bind(this));
            this.$profileFormOk.on("click", this.updateProfileOk.bind(this));
        },
        cacheDOM: function () {
            this.$chatHistory = $('.chat-history');
            this.$roomSet = $('.room-set');
            this.$button = $('button');
            this.$textarea = $('#message-to-send');
            this.$chatAbout = $('.chat-about');
            this.$chatHistoryList = this.$chatHistory.find('ul');
            this.$roomSetList = this.$roomSet.find('ul');
            this.$createRoomForm = $('#create-room');
            this.$createRoomShowElements = $('#create-room, .fidebox');
            this.$createRoomFormCancel = this.$createRoomForm.find('#create-room-cancel');
            this.$createRoomFormOk = this.$createRoomForm.find('#create-room-ok');
            this.$roomNameInput = this.$createRoomForm.find('#create-room-name');
            this.$createRoomValidate = this.$createRoomForm.find('.validate-error');
            this.$roomAddButton = $('.cell-add');
            this.$loginForm = $('#login-form');
            this.$loginFormShowElements = $('#login-form, .fidebox');
            this.$loginFormOk = this.$loginForm.find('#login-form-ok');
            this.$loginFormNameInput = this.$loginForm.find('.field-name');
            this.$loginFormValidate = this.$loginForm.find('.validate-error');
            this.$profileForm = $('#profile-form');
            this.$profileFormOk = this.$profileForm.find('#profile-form-ok');
            this.$profileFormCancel = this.$profileForm.find('#profile-form-cancel');
            this.$profileFormNikname = this.$profileForm.find('#nikname');
            this.$profileFormEmail = this.$profileForm.find('#email');
            this.$profileFormValidate = this.$profileForm.find('.validate-error');
            this.$profileFormShowElements = $('#profile-form, .fidebox');

            this.$logoutButton = $('.chat-header .kn-exit');
            this.$profileButton = $('.chat-header .kn-cog');
        },
        chatClick: function (event) {
            let roomId = $(event.target).attr('data-id')
            if (roomId === undefined)
                return;
            this.updateTitleChat();
            console.log("roomId=" + roomId);
            this.goToRoom(roomId);
            this.coloredChatList();
        },
        coloredChatList: function () {
            this.$roomSetList.find('.chat-item').css({"font-weight": "normal", "color": "white"});
            if (this.roomId === null)
                return;
            this.$roomSetList.find('.chat-item[data-id=' + this.roomId + ']').css({
                "font-weight": "bold",
                "color": "#E38968"
            });
            this.updateTitleChat(this.$roomSetList.find('.chat-item[data-id=' + this.roomId + ']').text());
        },
        connectToChatServer: function (userLogin) {
            if (!window.WebSocket) {
                window.WebSocket = window.MozWebSocket;
            }
            if (window.WebSocket) {
                let roomId = "2bd09cbf-ef16-469f-82ab-f51ae9913aa0";
                let config = {
                    "messageType": "CLIENT",
                    "operationType": "LOGON",
                    "login": userLogin,
                    "roomId": roomId,
                    "token": "dfgfdsgfdsgfdsgfdsgfdsg"
                }
                let configJSON = JSON.stringify(config);
                // Encode the String
                let encodedString = Base64.encode(configJSON);

                try {
                    this.socket = new WebSocket("ws://" + window.location.host + "/websocket/?request=" + encodedString);
                } catch (e) {
                    console.log("server unnavigable");
                    this.$loginFormValidate.text(e.messageText);
                    this.$loginFormValidate.show();
                    return;
                }
                this.socket.addEventListener('message', this.receiveMessage.bind(this));
                this.socket.addEventListener('open', this.socketOpen.bind(this));
                this.socket.addEventListener('close', this.socketClose.bind(this));
                this.socket.addEventListener('error', this.socketError.bind(this));
            }
        },
        createRoom: function () {
            this.$roomNameInput.val('');
            this.$createRoomShowElements.fadeIn('slow');
        },
        createRoomFormCancel: function () {
            this.$createRoomValidate.text('');
            this.$createRoomValidate.hide();
            this.$createRoomShowElements.hide();
        },
        createRoomFormOk: function () {
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
        }, getFormattedTime: function (date) {
            return date.toLocaleString();
        },
        goToRoom: function (roomId, force = false) {
            this.scrollToBottom();
            if (roomId != null && (this.roomId !== roomId) || force === true) {
                this.roomId = roomId;
                if (!window.WebSocket) {
                    return;
                }
                if (this.socket.readyState === WebSocket.OPEN) {

                    let request = {
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
        init: function () {
            this.cacheDOM();
            this.bindEvents();
            this.loginUser();
        },
        loginFormOkClick: function () {
            this.$loginFormValidate.text('');
            this.$loginFormValidate.hide();
            if (this.$loginFormNameInput.val().trim() === '') {
                return;
            } else if (this.$loginFormNameInput.val().trim().length > 2) {
                this.login = this.$loginFormNameInput.val().trim();
                this.connectToChatServer(this.login);
            } else {
                this.$loginFormValidate.text("Your username must be at least two characters.(oleg, vadim, sergey)");
                this.$loginFormValidate.show();
            }
        },
        resetUser: function () {
            this.login=null;
            this.email=null;
            this.roomId=null;
            this.messageToSend='';
            this.messagesLength=0;
            this.nickName=null;
            this.senderId=null;
            if (window.WebSocket && this.socket && this.socket.readyState === WebSocket.OPEN) {
                this.socket.close();
                this.socket = null;
            }
            this.$textarea.val('');
            this.$chatHistoryList.empty();
            this.$roomSetList.empty();
            this.$chatAbout.html('');
            this.$loginFormNameInput.val('');
            this.$loginFormShowElements.fadeIn('slow');
        },
        loginUser: function () {
            this.resetUser();
            this.$loginFormNameInput.val('');
            this.$loginFormShowElements.fadeIn('slow');
        },
        updateProfile: function () {
           if(this.senderId != null) {
               this.$profileFormNikname.val(this.nickName);
               this.$profileFormEmail.val(this.email);
               this.$profileFormValidate.text('');
               this.$profileFormValidate.hide();
               this.$profileFormShowElements.fadeIn('slow');
           }
        },
        updateProfileCancel: function () {
                this.$profileFormNikname.val('');
                this.$profileFormEmail.val('');
                this.$profileFormValidate.text('');
                this.$profileFormValidate.hide();
                this.$profileFormShowElements.hide();
        },
        updateProfileOk: function () {
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
        messageToSend: '',
        messagesLength: 0,
        nickName: null,
        email: null,
        processClientProfile: function (data) {
            this.senderId = data.id;
            this.nickName = data.nickName;
            this.email = data.email;
            if (data.roomId != null) {
                this.roomId = data.roomId;
                this.coloredChatList();
                this.goToRoom(data.roomId, true);
            }
        },
        processInfo: function (info) {
            this.$chatHistoryList.append(this.renderedInfoMessage(info));
            this.scrollToBottom();
        },
        processMessage: function (message) {
            if (!this.$chatHistoryList.find('[message-id=' + message.id + ']').length) {
                this.messagesLength = this.messagesLength + 1;
                this.updateTitleChat();
                this.$chatHistoryList.append(this.renderedMessage(message));
                this.scrollToBottom();
            }
        },
        processMessageList: function (operationType, messages) {
            this.$chatHistoryList.empty();
            this.messagesLength = messages.length;
            this.updateTitleChat();
            for (let i = 0; i < messages.length; i++) {
                this.$chatHistoryList.append(this.renderedMessage(messages[i]));
            }
            this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
        },
        processRoom: function (room) {
            if (!this.$roomSetList.find('.chat-item[data-id=' + room.id + ']').length) {
                let templateRoom = Handlebars.compile($("#room-item-template").html());
                let contextResponse = {
                    name: room.name,
                    id: room.id
                };
                this.$roomSetList.append(templateRoom(contextResponse));
                searchRoomFilter.init();
                this.coloredChatList();
            }
        },
        processRoomList: function (operationType, rooms) {
            let templateRoomList = Handlebars.compile($("#room-set-item-template").html());
            this.$roomSetList.empty();
            this.$roomSetList.append(templateRoomList({objects: rooms}));
            searchRoomFilter.init();
            this.coloredChatList();
        },
        receiveMessage: function (event) {
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
        renderedInfoMessage(info) {
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
                messageText: info.messageText,
                time: time_
            };
            return templateinfoMesssage(contextResponse);
        },
        renderedMessage(message) {
            let templateResponse = Handlebars.compile($("#message-response-template").html());
            if (message.senderId === this.senderId) {
                templateResponse = Handlebars.compile($("#message-template").html());
            }
            let contextResponse = {
                id: message.id,
                login: message.sender,
                messageText: message.messageText,
                time: this.getFormattedTime(new Date(message.ts))
            };
            return templateResponse(contextResponse);
        },
        roomId: null,
        scrollToBottom: function () {
            this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
        },
        sendMessage: function () {
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
        senderId: null,
        socket: null,
        socketClose: function (event) {
            console.log("websocket closed");
        },
        socketError: function (error) {
            //console.log("websocket error");
            console.log('WebSocket error: ', error);
            this.$loginFormValidate.text("Неверный логин. (oleg, vadim, sergey)");
            this.$loginFormValidate.show();
        },
        socketOpen: function (event) {
            console.log("websocket opened");
            this.$loginFormValidate.text('');
            this.$loginFormValidate.hide();
            this.$loginFormShowElements.hide();
        },
        updateTitleChat: function () {
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
                chatName: chatName,
                chatStatistic: countMessage
            };
            this.$chatAbout.html(templateResponse(contextResponse));
        },
        uuidv4: function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        }
    };

    chat.init();

    searchRoomFilter = {
        options: {valueNames: ['name']},
        init: function () {
            var chatList = new List('chat-list', this.options);
            var noItems = $('<li id="no-items-found">No items found</li>');

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