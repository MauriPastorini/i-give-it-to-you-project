var userController = require('../controllers/userController');
var auth = require('../middlewares/auth');

exports.injectRoutes = function(router){
  router.route('/user')
    .get(auth.isAdmin, userController.getAllUsers);
  router.route('/user/:id')
    .put(auth.isAdmin, userController.updateUser);
  router.post('/user/signin', userController.signIn);
  router.post('/user/signup', userController.signUp);
}
