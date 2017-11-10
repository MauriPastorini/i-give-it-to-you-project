const mongoose = require('mongoose');
const Product = mongoose.model('Product');

function getAllProducts(req,res,next,userIdSolicitude = ""){
  var query = {};
  console.log("req.query", req.query);
  if (userIdSolicitude == "") {
    if (req.query.moderated) query["moderated"] = req.query.moderated;
  }
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
  var select = [];
  var populate = {path:''};
  var isInclusion = null;
  if(req.query.image_path ){
    if (req.query.image_path == "true" && (isInclusion == null ||  isInclusion == true)){
      select.push('image_path');
      isInclusion = true;
    } else if (req.query.image_path == "false"&& (isInclusion == null ||  isInclusion == false)){
      select.push('-image_path');
      isInclusion = false;
    }
    else {
      return res.status(400).send("Bad format in query param image_path");
    }
  }
  if(req.query.category ){
    if (req.query.category == "true" && (isInclusion == null ||  isInclusion == true)){
      select.push('category');
      isInclusion = true;
    } else if (req.query.category == "false"&& (isInclusion == null ||  isInclusion == false)){
      select.push('-category');
      isInclusion = false;
    }
    else {
      return res.status(400).send("Bad format in query param category");
    }
  }
  if (select.length == 0 || (select.length != 0 && select.indexOf('category') != -1) || (isInclusion == false && select.indexOf('-category') == -1)) {
    populate = {path: 'category'};
  }

  Product.findById(req.params.id, select)
  .populate(populate)
  .exec(function(err,products){
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

function acceptModeratedProduct(req,res,next){
  Product.findById(req.params.id, function(err,product){
    if(err) return next(err);
    product.moderated = true;
    product.save(function(err2,updatedProduct){
      if(err2) return next(err2);
      res.status(200).jsonp(updatedProduct);
    });
  });
}

function rejectModeratedProduct(req,res,next){
  Product.findById(req.params.id, function(err,product){
    if(err) return next(err);
    product.moderated = false;
    product.save(function(err2,updatedProduct){
      if(err2) return next(err2);
      res.status(200).jsonp(updatedProduct);
    });
  });
}

function addNewImagePathOfProduct(req, res, next){
  var userId = req.params.userId;
  var productId = req.params.productId;
  var productsInBody = req.body;
  Product.findById(productId, function(err,product){
    if (err) return next(err);
    var productsToAdd = productsInBody;
    if (!Array.isArray(productsInBody)) {
      productsToAdd = [productsInBody];
    }
    productsToAdd.forEach(function(element){
      product.image_path.push(element.image_path);
    });
    product.save(function(err2,updatedProduct){
      if (err2) return next(err2);
      res.status(200).jsonp(updatedProduct);
    });
  });
}

function deleteProductById(req, res, next){
  var productId = req.params.id;
  Product.findByIdAndRemove(productId, function(err, product){
    if(err) return next(err);
    res.status(200).send({message: "Success deleting product"});
  });
}

module.exports = {
  getAllProducts,
  getAllProductsOfUser,
  getProductById,
  postNewProduct,
  acceptModeratedProduct,
  rejectModeratedProduct,
  addNewImagePathOfProduct,
  deleteProductById
}
