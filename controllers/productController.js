const mongoose = require('mongoose');
const Product = mongoose.model('Product');

function getAllProducts(req,res,next,userIdSolicitude = ""){
  var query = {};
  console.log("req.query", req.query);
  if (req.query.category) query["category"] = req.query.category;
  if (userIdSolicitude != "") query["solicitatedUser"] = userIdSolicitude;

  Product.find(query).populate('category').exec(function(err,products){
    if (err) return next(err);
    res.status(200).jsonp(products);
  });
};

function getAllProductsOfUser(req, res, next){
  var userId = req.user.sub;
  getAllProducts(req,res,next,userId+"")
}

function getProductById(req,res, next){
  Product.findById(req.params.id).populate('category').exec(function(err,products){
    if (err) return next(err);
    if(!products) return res.status(204).jsonp({});
    res.status(200).jsonp(products);
  });
};

function postNewProduct(req,res,next){
  Product.create(req.body).then(function(product){
    Product.findOne({_id: product._id}).populate('category').exec(function(err, productPop) {
      res.send(productPop);
      });
  }).catch(next);
}

module.exports = {
  getAllProducts,
  getAllProductsOfUser,
  getProductById,
  postNewProduct
}
