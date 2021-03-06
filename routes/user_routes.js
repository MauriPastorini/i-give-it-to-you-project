var userController = require('../controllers/userController');
var auth = require('../middlewares/auth');
var permissions = require('../middlewares/permissions');

exports.injectRoutes = function(router){
  router.route('/user')
    .get(auth.isAdmin, userController.getAllUsers);
  router.route('/user/:userId')
    .put(auth.isAuth, permissions.canAccessUserInfo, userController.updateUser)
    .delete(auth.isAdmin, userController.deleteUser);
  router.route('/user/:id/admin')
    .post(auth.isAdmin, userController.setUserToAdmin);
  router.post('/user/signin', userController.signIn);
  router.post('/user/signup', userController.signUp);
  router.post('/user/:userId/photo', userController.updateUser);
  router.get('/user/token', auth.isAuth, function(req,res,next){res.send(200);});
}
