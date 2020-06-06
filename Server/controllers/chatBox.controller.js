const ChatBox = require("../models/ChatBox.js");
// const User = require("../models/User.js");

exports.newChat = (req, res) => {
  const newChatBox = new ChatBox({
    chatBoxName: "",
    type: "",
    users: [...req.body.users.split(',')],
    messages: [],
  });
  newChatBox.save()
    .then((chatBox) => {
      // console.log(chatBox);
      res.status(200).json({roomID: chatBox._id});
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    });

};
exports.browseChatByUserIDs = (req, res) => {
  const user1 = req.params.users.split(',')[0];
  const user2 = req.params.users.split(',')[1];
  ChatBox.find({users: user1})
    .then(chatBoxes => {
      let flag = 0;
      chatBoxes.forEach(chatBox => {
        if (chatBox.users.includes(user2)) {
          res.status(200).json({roomID: chatBox._id});
          flag = 1;
        }
      });
      if (!flag) {
        res.status(200).json({});
      }
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    })
};
exports.browseAllChatByUserID = (req, res) => {
  ChatBox.find({users: req.params.user})
    .sort({modifiedDate: -1})
    .populate('users')
    .then(chatBoxes => {
      let listChats = [];
      chatBoxes.forEach(chatBox => {
        let chatFriendID = '', chatFriendName = '', chatAvatar;
        chatBox.users.forEach(u => { 
          if(u._id.toString() !== req.params.user) {
            chatFriendID = u._id;
            chatFriendName = u.userName;
            chatAvatar = u.avatar;
          }
        });
        if (chatBox.messages.length !== 0) {
          let chat = {
            roomID: chatBox._id,
            chatBoxName: chatFriendName,
            chatAvatar: chatAvatar,
            lastMessage: chatBox.messages[chatBox.messages.length - 1].message,
            modifiedTime: new Date(chatBox.messages[chatBox.messages.length - 1].sentTime).toLocaleTimeString(),
            // users: chatBox.users
          };
          listChats.push(chat);
        }
      });
      res.status(200).json({listChatBox: listChats});
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    })
};

exports.get20MessagesFromDB = (req, res) => {
  ChatBox.findById(req.params.roomID)
    .then(chatBox => {
      let messages = chatBox.messages.slice(Math.max(chatBox.messages.length - 20, 0));
      
      res.status(200).json({messages: messages});
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    })
}

exports.getRecentMessagesFromDB = (req, res) => {
  let number = req.params.number;
  ChatBox.findById(req.params.roomID)
    .then(chatBox => {
      let messages = chatBox.messages.slice(Math.max(chatBox.messages.length - number, 0));
      res.status(200).json({messages: messages});
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    })
}

exports.saveMessageToDB = (roomID, message) => {
  ChatBox.findById(roomID)
    .then(chatBox => {
      chatBox.modifiedDate = message.sentTime;
      chatBox.messages.push(message);
      chatBox.save();
    })
    .catch(err => {
      console.log(err);
    })
};

exports.removeMessage = (req, res) => {
  ChatBox.findById(req.body.roomID)
    .then(chatBox => {
      let messages = chatBox.messages.filter(mess => {
        return mess._id.toString() !== req.body.messageID;
      });
      chatBox.messages = messages;
      return chatBox.save();
    })
    .then(() => {
      res.status(200).json({message: 'Removed!'});
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    })
};

exports.updateSeenStatusMessages = (roomID, userID) => {
  ChatBox.findById(roomID)
    .then(chatBox => {
      for (let i = chatBox.messages.length - 1; i >= 0 ; i--) {
        let message = chatBox.messages[i];
        if (message.userID !== userID && !message.isSeen) {
          ChatBox.updateOne({'messages._id': message._id}, 
                          {'$set': {'messages.$.isSeen': true}},
                          function(err, data) {
                            if (err)
                              console.log(err);
                        });
          // ChatBox.findOneAndUpdate({
          //   '_id': roomID,
          //   'messages._id': message._id
          // }, {
          //   '$set': {'messages.$.isSeen': true}
          // });
        }
        else
          break;
      }
    })
    .catch(err => {
      console.log(err);
    })
}