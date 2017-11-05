var userController = require('../controllers/userController');
var auth = require('../middlewares/auth');

exports.injectRoutes = function(router){
  router.route('/user')
    .get(auth.isAdmin, function(req, res){
      console.log("TIENE ACCESO");
      res.status(200).send({ message: 'Tienes acceso'})
    });
  router.post('/user/signin', userController.signIn);
  router.post('/user/signup', userController.signUp);

}
