let router = require("express").Router();
let chatBoxController = require('../controllers/chatBox.controller');

router.post('/new', chatBoxController.newChat);
router.post('/removeMessage/', chatBoxController.removeMessage)

router.get('/browse/:users', chatBoxController.browseChatByUserIDs);
// router.get('/browse/:chatBoxID', chatBoxController.browseChatByChatBoxID);
router.get('/browseAllChat/:user', chatBoxController.browseAllChatByUserID)
router.get('/get20LastMessages/:roomID', chatBoxController.get20MessagesFromDB)
router.get('/getRecentMessages/:roomID/:number', chatBoxController.getRecentMessagesFromDB)

// Export API routes
module.exports = router;
