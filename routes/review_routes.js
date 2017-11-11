var reviewController = require('../controllers/reviewController');
var auth = require('../middlewares/auth');
var permissions = require('../middlewares/permissions');

exports.injectRoutes = function(router){
  router.route('/review')
    .post(auth.isAuth, reviewController.postNewReview);
}
