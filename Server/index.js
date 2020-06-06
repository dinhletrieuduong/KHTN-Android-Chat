const express = require('express');
const http = require('http');
const app = express();
const server = http.createServer(app);
const io = require('socket.io').listen(server);
const bodyParser = require('body-parser');

// parse requests of content-type - application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: true }));
// parse requests of content-type - application/json
app.use(bodyParser.json());

// Configuring the database
const dbConfig = require('./config/database.config.js');
const mongoose = require('mongoose');

mongoose.Promise = global.Promise;

// Connecting to the database
mongoose.connect(dbConfig.url, { useNewUrlParser: true,  useUnifiedTopology: true })
  .then(() => {
    console.log("Successfully connected to the database");    
  })
  .catch(err => {
    console.log('Could not connect to the database. Exiting now...', err);
    process.exit();
  });

// define a simple route
app.get('/', (req, res) => {
  res.json({"message": "Welcome"});
});
// Import routes
let userRoute = require("./routes/user.routes");
let chatRoute = require("./routes/chatBox.routes");
let chatBoxController = require('./controllers/chatBox.controller');
let Message = require('./models/Message');

// Use Api routes in the App
app.use('/api/user', userRoute);
app.use('/api/chat', chatRoute);

let clients = {}, rooms = {};

io.on('connection', (socket) => {
  console.log('user connected')

  socket.on('disconnect', () => {
    for (const roomID in rooms) {
      if (rooms.hasOwnProperty(roomID)) {
        const sockets = rooms[roomID];
        // console.log('Sockets:', sockets);
        delete sockets.users[socket.id];
      }
    }
    socket.broadcast.emit("user_disconnect", false);
  });

  socket.on('join_room', (roomID, userID) => {
    if (!rooms[roomID]) 
      rooms[roomID] = { users: {} };
    rooms[roomID].users[socket.id] = userID;
    socket.join(roomID);
    
    chatBoxController.updateSeenStatusMessages(roomID, userID);

    io.to(roomID).emit('user_joined_room', {userID: userID, numberClients: Object.keys(rooms[roomID].users).length});
  });

  socket.on('message_detection', (roomID, userID, userName, messageContent, isSent, isImage) => {
    //log the message in console 
    console.log(userName + " chat: " + messageContent);
    let isSeen = false;
    if (Object.keys(rooms[roomID].users).length === 2)
      isSeen = true;
    //create a message object 
    let message = new Message ({
      userID: userID,
      userName: userName,
      isSent: isSent,
      isImage: isImage,
      isSeen: isSeen,
      message: messageContent,
    });
    // send the message to all users including the sender using io.emit() 
    io.to(roomID).emit('message', message);
    chatBoxController.saveMessageToDB(roomID, message);
  });

  socket.on('message_delete', (roomID, messageID) => {
    console.log('message_deleted');
    
    io.to(roomID).emit('message_deleted', {messageID: messageID});
  })

});

server.listen(5000, () => {
  console.log('Node app is running on port 5000');
});