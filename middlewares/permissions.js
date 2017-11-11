const User = require('../models/user');
const Product = require('../models/product');

function canAccessUserInfo(req,res,next){
  var userId = req.params.userId + "";
  if(!userId) res.status(404).send("Not userId selected");
  var userIdFromToken = req.user.sub;
  User.findById(userIdFromToken, function(err, userFromToken){
    if(err) next();
    User.findById(userId, function(err,user){
      if (!user) return res.status(404).send({success: false, message: "User does not exist"});
      var isAdmin = User.isAdmin(userFromToken);
      var isUser = User.isUser(userFromToken);
      if (isAdmin) {
        next();
      } else if(isUser){
        if (user._id == userIdFromToken) {
          next();
        } else{
          res.status(403).send({success: false, message: "Unthorized user, this action can be performed only by the user"});
        }
      };
    });
  });
};

function productIsOwnerOfUser(req,res,next){
  var userIdFromToken = req.user.sub;
  var productId = req.params.productId;
  Product.findById(productId, function(err,product){
    if (!product) next(err);
    if (product.ownerUser == userIdFromToken) {
      next();
    } else{
      res.status(403).send({success: false, message: "Unthorized user, this action can be performed only by the owner user of the product"});
    }
  });
};

function userExists(req,res,next){
  var userIdToCheck = req.params.userId;
  console.log(userIdToCheck);
  User.findById(userIdToCheck, function(err,user){
    if(err) return next(err);
    if (user) {
      next();
    } else{
      return res.status(404).send({success: false, message: "User in query params doesnt exists"});
    }
  });
}

module.exports = {
  canAccessUserInfo,
  productIsOwnerOfUser,
  userExists
}
