var productController = require('../controllers/productController');
var auth = require('../middlewares/auth');
var permissions = require('../middlewares/permissions');
const User = require('../models/user');

exports.injectRoutes = function(routes){
  routes.route('/product')
    .get(auth.isAuth, function(req,res,next){
                      console.log("req.user.sub",req.user.sub);
                      var userId = req.user.sub;
                      User.findById(userId, function(err,user){
                        console.log(user);
                        if (User.isAdmin(user)) {
                          productController.getAllProducts(req,res,next);
                        }else{
                          productController.getAllProductsOfUser(req,res,next);
                        }
                      });
                    })
    .post(auth.isAuth, productController.postNewProduct);
  routes.route('/product/:id')
    .get(productController.getProductById)
    .delete(auth.isAdmin, productController.deleteProductById);
  routes.post('/product/:id/accept', auth.isAdmin, productController.acceptModeratedProduct);
  routes.post('/product/:id/reject', auth.isAdmin, productController.rejectModeratedProduct);
  routes.post('/product/:productId/image', auth.isAuth, permissions.productIsOwnerOfUser, productController.addNewImagePathOfProduct);

}
