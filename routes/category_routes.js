var categoryController = require('../controllers/categoryController')

exports.injectRoutes = function(router){
  router.route('/category')
    .get(categoryController.getAllCategories);
};
