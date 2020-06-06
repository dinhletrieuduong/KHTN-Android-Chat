let router = require("express").Router();
let userController = require('../controllers/user.controller');

router.post("/login", userController.login);
router.post("/loginByFacebook/", userController.loginByFacebook)
router.post("/logout/", userController.logout)
router.post("/signup", userController.signUp);
router.post("/friends", userController.addFriend);
router.post('/lastLocation', userController.lastLocation);
router.post('/updateProfile', userController.updateProfile);

router.get("/profile/:userID", userController.getProfile)
router.get("/friends/:userID", userController.findFriends);
router.get('/nearBy/:userID/', userController.nearBy);

// Export API routes
module.exports = router;
