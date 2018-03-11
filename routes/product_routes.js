var productController = require('../controllers/productController');
var auth = require('../middlewares/auth');
var permissions = require('../middlewares/permissions');
const User = require('../models/user');

exports.injectRoutes = function(routes){
  routes.route('/product')
    .get(
      function(req,res,next){auth.isAuth(req,res,next,false);},
      function(req,res,next){auth.injectUser(req,res,next,false);},
      productController.getAllProducts)
    .post(auth.isAuth, productController.postNewProduct);
  routes.route('/product/:id')
    .get(productController.getProductById)
    .delete(auth.isAdmin, productController.deleteProductById);
  routes.post('/product/:id/accept', auth.isAdmin, productController.acceptModeratedProduct);
  routes.post('/product/:id/reject', auth.isAdmin, productController.rejectModeratedProduct);
  routes.post('/product/:productId/image', auth.isAuth, permissions.isOwnerOfProduct, productController.addNewImagePathOfProduct);
  routes.route('/product/:productId/solicitude')
    .post(auth.isAuth, productController.addNewSolicitationOfProduct)
    .delete(auth.isAuth, productController.deleteSolicitationOfProduct);
  routes.route('/product/:productId/user-to-deliver')
    .post(auth.isAuth, permissions.isOwnerOfProduct, productController.postUserToDeliver);
  routes.post('/product/:productId/delivered', auth.isAuth, permissions.isOwnerOrApplicantOfProduct, productController.confirmDeliveredFromUserOwnerOrDeliverUser);
}
