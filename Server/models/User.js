const mongoose = require('mongoose');

const UserSchema = mongoose.Schema({
  userName: String,
  password: String,
  name: String,
  email: String,
  avatar: String,
  phone: String,
  age: String,
  place: String,
  gender: String,
  isLoginByFacebook: Boolean,

  genderSelection: {type: String, default: "All"},
  ageSelection: {type: String, default: "18"},
  locationDistance: {type: String, default: "0"},
  
  lastLocation: String,
  status: {type: Boolean, default: true},

  friends: [{type: mongoose.Schema.Types.ObjectId}],
  replies: [this],
  create_date: {
    type: Date,
    default: Date.now 
  }
});

module.exports = mongoose.model('user', UserSchema);
