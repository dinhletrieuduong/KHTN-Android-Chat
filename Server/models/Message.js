const mongoose = require('mongoose');

const MessageSchema = mongoose.Schema({
  userID: String,
  userName: String,
  message: String,
  isSent: Boolean,
  isImage: Boolean,
  isSeen: Boolean,
  sentTime: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('message', MessageSchema);