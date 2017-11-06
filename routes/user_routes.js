var userController = require('../controllers/userController');
var auth = require('../middlewares/auth');

exports.injectRoutes = function(router){
  router.route('/user')
    .get(userController.getAllUsers);
  router.post('/user/signin', userController.signIn);
  router.post('/user/signup', userController.signUp);
}
