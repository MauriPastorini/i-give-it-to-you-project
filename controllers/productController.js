const mongoose = require('mongoose');
const Product = mongoose.model('Product');

exports.getAllProducts = function(req,res){
  Product.find({}, function(err,products){
    if (err) {
      res.status(500).send({success: false, message: "Internal error on getAllProducts"});
    }else {

      res.status(200).jsonp(products)
    }
  });
};

exports.postNewProduct = function(req,res,next){
  Product.create(req.body).then(function(product){
    Product.findOne({_id: product._id}).populate('category').exec(function(err, productPop) {
      res.send(productPop);
      });
  }).catch(next);
}
