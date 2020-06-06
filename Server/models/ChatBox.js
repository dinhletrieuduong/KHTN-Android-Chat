const mongoose = require('mongoose');
const UserSchema = require("../models/User.js");

const ChatBoxSchema = mongoose.Schema({
  chatBoxName: String,
  type: String,
  users: [{type: mongoose.Schema.ObjectId, ref: 'user'}],
  // users: [String],
  messages: Array,
  modifiedDate: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('chatbox', ChatBoxSchema);