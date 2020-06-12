const User = require('../models/User.js');
const util = require('./util');
exports.login = (req, res) => {
  User.findOne({ userName: req.body.userName })
		.then((user) => {
      if (util.isEmpty(user)) {
        res.status(400).json({message: "User not exists"});
      }
			else if (user.password === req.body.password) {
        user.status = true;
        user.save()
          .then(() => {
            res.status(200).json({
              userID: user._id,
              userName: user.userName,
              avatar: user.avatar,
            });
          })
          .catch(err => {
            console.log(err);
            res.status(500).json({
              message: "Errors",
            });
          })
			} else {
				res.status(400).json({
					message: "Username or password not invalid",
				});
			}
		})
		.catch((err) => {
			console.log(err);
			res.status(500).json({ message: "Errors" });
		});
}
exports.logout = (req, res) => {
  User.findById(req.body.userID)
		.then((user) => {
      user.status = false;
      return user.save()
    })
    .then(() => {
      res.status(200).json({message: 'Logout!'});
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: "Errors"});
    })
}
// Create and Save a new User
exports.signUp = (req, res) => {
  User.findOne({ userName: req.body.userName })
  .then((user) => {
    if (!user) {
      // Create a User
      const newUser = new User({
        userName: req.body.userName,
        password: req.body.password,
        name: req.body.name,
        gender: req.body.gender,
        phone: req.body.phone,
        age: req.body.age,
        avatar: req.body.avatar,
        isLoginByFacebook: false,
        email: "",
        lastLocation: "",
      });
      // Save User in the database
      newUser.save()
        .then((nUser) => {
          res.status(200).json({
            userID: nUser._id, 
            userName: nUser.userName, 
            avatar: nUser.avatar, 
          });
        })
        .catch((err) => {
          console.log(err);
          res.status(500).send({message: "Errors"});
        });
    } else {
      res.status(400).json({ message: "User exists" });
    }
  })
  .catch((err) => {
    console.log(err);
    res.status(500).json({ message: "Cannot sign up" });
  });
};
// Find friends of user by userID
exports.findFriends = (req, res) => {
	User.findById(req.params.userID)
		.populate("friends")
		.then((user) => {
			// user.friends.findById
			var listFriend = [];
			if (user) {
				for (var i = 0; i < user.friends.length; i++) {
					let friend = {
						userID: user.friends[i]._id,
						userName: user.friends[i].userName,
						avatar: user.friends[i].avatar,
						// userName
					};
					listFriend.push(friend);
        }
			  res.status(200).json({ friends: listFriend });
      }
      else {
        res.status(200).json({friends: []});
      }
		})
		.catch((err) => {
			console.log(err);
			res.status(400).json({ message: "Error" });
		});
};
// add new friend with friendID
exports.addFriend = (req, res) => {
	User.findById(req.body.userID)
		.then((friend) => {
      if (friend.friends.indexOf(req.body.friendID) === -1) {
        friend.friends.push(req.body.friendID);
        friend.save()
          .then(() => {
            // User.findById(req.body.friendID)
            //   .then((user) => {
            //     user.friends.push(req.body.userID);
            //     return user.save();
            //   })
            //   .then(() => {
            //     res.status(200).json({ friends: friend });
            //   })
            //   .catch((err) => {
            //     console.log(err);
            //     res.status(400).json({ message: "Error" });
            //   });
            res.status(200).json({ friends: friend });
          });
      }
      else
        res.status(400).json({message: 'Have Added!'});
    })
		.catch((err) => {
			console.log(err);
			res.status(400).json({ message: "Error" });
		});
};

exports.nearBy = (req, res) => {
  let userID = req.params.userID;
  User.findById(userID)
    .then(user => {
      let longitude = parseFloat(user.lastLocation.split(',')[0]);
      let latitude = parseFloat(user.lastLocation.split(',')[1]);
      let maxDistance = parseFloat(user.locationDistance);

      // let fromAge = parseInt(user.ageDistance.split(','))[0];
      // let toAge = parseInt(user.ageDistance.split(','))[1];
      // if (user.genderSelection === 'All') {
      //   User.find({})
      //     .then(users => {
      //       let nearbyUsers = [];
      //       users.forEach(nearbyUser => {
      //         if (nearbyUser._id.toString() !== userID) {
      //           // console.log(nearbyUser.userName);
      //           let longitudeUser = parseFloat(nearbyUser.lastLocation.split(',')[0]);
      //           let latitudeUser = parseFloat(nearbyUser.lastLocation.split(',')[1]);
      //           let usersDistance = util.getDistanceFromLatLonInKm(longitude, latitude, longitudeUser, latitudeUser);
      //           // console.log(isNaN(usersDistance));
      //           // if (usersDistance <= maxDistance && nearbyUser.age === user.ageSelection) {
      //           if (usersDistance <= maxDistance) {
      //             let userNearby = {
      //               userID: nearbyUser._id,
      //               userName: nearbyUser.userName,
      //               avatar: nearbyUser.avatar,
      //               distance: usersDistance,
      //               age: nearbyUser.age,
      //             }
      //             nearbyUsers.push(userNearby);
      //           }
      //         }
      //       });
      //       res.status(200).json({users: nearbyUsers});
      //     })
      //     .catch(err => {
      //       console.log(err);
      //       res.status(400).json({message: 'Errors'});
      //     })
      // }
      // else {
      //   User.find({gender: user.genderSelection})
      //     .then(users => {
      //       let nearbyUsers = [];
      //       users.forEach(nearbyUser => {
      //         if (nearbyUser._id.toString() !== userID) {
      //           // console.log(nearbyUser.userName);
      //           let longitudeUser = parseFloat(nearbyUser.lastLocation.split(',')[0]);
      //           let latitudeUser = parseFloat(nearbyUser.lastLocation.split(',')[1]);
      //           let usersDistance = util.getDistanceFromLatLonInKm(longitude, latitude, longitudeUser, latitudeUser);
      //           // console.log(isNaN(usersDistance));
      //           // if (usersDistance <= maxDistance && nearbyUser.age === user.ageSelection) {
      //           if (usersDistance <= maxDistance) {
      //             let userNearby = {
      //               userID: nearbyUser._id,
      //               userName: nearbyUser.userName,
      //               avatar: nearbyUser.avatar,
      //               distance: usersDistance,
      //               age: nearbyUser.age,
      //             }
      //             nearbyUsers.push(userNearby);
      //           }
      //         }
      //       });
      //       res.status(200).json({users: nearbyUsers});
      //     })
      //     .catch(err => {
      //       console.log(err);
      //       res.status(400).json({message: 'Errors'});
      //     })
      // }

      User.find({})
          .then(users => {
            let nearbyUsers = [];
            users.forEach(nearbyUser => {
              if (nearbyUser._id.toString() !== userID) {
                // console.log(nearbyUser.userName);
                let longitudeUser = parseFloat(nearbyUser.lastLocation.split(',')[0]);
                let latitudeUser = parseFloat(nearbyUser.lastLocation.split(',')[1]);
                let usersDistance = util.getDistanceFromLatLonInKm(longitude, latitude, longitudeUser, latitudeUser);
                // console.log(isNaN(usersDistance));
                // if (usersDistance <= maxDistance && nearbyUser.age === user.ageSelection) {
                if (usersDistance <= 10) {
                  let userNearby = {
                    userID: nearbyUser._id,
                    userName: nearbyUser.userName,
                    avatar: nearbyUser.avatar,
                    distance: usersDistance,
                    age: nearbyUser.age,
                    gender: nearbyUser.gender,
                  }
                  nearbyUsers.push(userNearby);
                }
              }
            });
            res.status(200).json({users: nearbyUsers});
          })
          .catch(err => {
            console.log(err);
            res.status(400).json({message: 'Errors'});
          })
      
    })
  
};

exports.lastLocation = (req, res) => {
  User.findById(req.body.userID)
  .then((user) => {
    user.lastLocation = req.body.lastLocation;
    return user.save();
  })
  .then(() => {
    res.status(200).json({message: 'Updated Last Location'})
  })
  .catch(err => {
    console.log(err);
    res.status(400).json({message: 'Error'});
  })
};

exports.updateStatus = (userID) => {
  User.findById(userID)
    .then(user => {
      user.status = !user.status;
      return user.save();
    })
    .catch(err => {
      console.log(err);
    })
};

exports.updateProfile = (req, res) => {
  User.findById(req.body.userID)
    .then(user => {
      user.name = util.isEmpty(req.body.name)? user.name : req.body.name;
      user.password = util.isEmpty(req.body.password) ? user.password : req.body.password;
      user.phone = util.isEmpty(req.body.phone) ? user.phone : req.body.phone;
      user.age = util.isEmpty(req.body.age) ? user.age : req.body.age;
      user.email = util.isEmpty(req.body.email) ? user.email : req.body.email;
      user.place = util.isEmpty(req.body.place)? user.place : req.body.place;
      user.gender = util.isEmpty(req.body.gender) ? user.gender : req.body.gender;
      user.avatar = util.isEmpty(req.body.avatar)? user.avatar : req.body.avatar;

      user.genderSelection = util.isEmpty(req.body.genderSelection) ? user.genderSelection : req.body.genderSelection;
      user.ageSelection = util.isEmpty(req.body.ageSelection) ? user.ageSelection : req.body.ageSelection;
      user.locationDistance = util.isEmpty(req.body.distance) ? user.locationDistance : req.body.distance;

      return user.save();
    })
    .then((user) => {
      res.status(200).json({
        userID: user._id,
        userName: user.userName,
        avatar: user.avatar,
      });
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'});
    })
}

exports.getProfile = (req, res) => {
  let userID = req.params.userID;
  User.findById(userID)
    .then(user => {
      res.status(200).json({
        userName: user.userName,
        avatar: user.avatar,
        age: user.age,
        name: user.name,
        gender: user.gender,

        email: user.email,
        place: user.place,
        phone: user.phone,
        ageSelection: user.ageSelection,
        genderSelection: user.genderSelection,
        locationDistance: user.locationDistance,

        isLoginByFacebook: user.isLoginByFacebook ? user.isLoginByFacebook : false,
      })
    })
    .catch(err => {
      console.log(err);
      res.status(400).json({message: 'Errors'})
    })
}

exports.loginByFacebook = (req, res) => {
  User.findOne({userName: req.body.userName})
    .then(user => {
      if (user) {
        res.status(200).json({
          userID: user._id,
          userName: user.userName,
          avatar: user.avatar,
          firstLogin: false, 
        });
      }
      else {
        const newUser = new User({
          userName: req.body.userName,
          avatar: req.body.avatar,
          isLoginByFacebook: true,
          age: req.body.age,
          phone: req.body.phone,
          name: req.body.name,
          lastLocation: "",
          gender: req.body.gender
        });
        // Save User in the database
        newUser.save()
          .then((nUser) => {
            res.status(200).json({
              userID: nUser._id, 
              userName: nUser.userName, 
              avatar: nUser.avatar,
              firstLogin: true 
            });
          })
          .catch((err) => {
            console.log(err);
            res.status(500).send({message: "Errors"});
          });
      }
    })
    .catch(err => {
      console.log(err);
      res.status(500).json({message: 'Errors'})
    })
}