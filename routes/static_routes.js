var staticController = require('../controllers/staticController');

exports.injectRoutes = function(router){
  router.route('/')
    .get(staticController.healthCheck);
}
