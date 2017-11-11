const mongoose = require('mongoose');
const ReviewUserProduct = mongoose.model('Review');
const Product = mongoose.model('Product');

function postNewReview(req,res,next){
  var userId = req.user.sub;
  var productId = req.body.productId;
  var reviewDescription = req.body.description;
  var stars = req.body.stars;

  if (!userId || !productId || !reviewDescription || !stars)
    return res.status(400).send({success:false, message: "Some parameters are missing"});

  Product.findById(productId, function(err,product){
    if(err) next(err);
    if(!product) return res.status(404).send({success:false,message:"Product doesnt exists"});
    if (product.ownerUser == userId || product.userToDeliver == userId) {
      var createObj = {
        product: productId,
        description: reviewDescription,
        stars: stars,
        user: userId
      }
      console.log("GOLA");
      ReviewUserProduct.create(createObj, function(err, product){
        if(err) return next(err);
        res.status(200).jsonp(product);
      });
    } else{
      return res.status(403).send({success:false,message:"Unthorized to review this product"});
    }
  });
}

module.exports = {
  postNewReview
}
