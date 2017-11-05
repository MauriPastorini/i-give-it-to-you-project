var productController = require('../controllers/productController');

exports.injectRoutes = function(routes){
  routes.route('/product')
    .get(productController.getAllProducts)
    .post(productController.postNewProduct);
}
