const mongoose = require('mongoose');
const Product = mongoose.model('Product');
const auth = require('../middlewares/auth');
var geoip = require('geoip-lite');
const requestIp = require('request-ip');
const errCodes = require('../config/errCodes');

function getAllProducts(req,res,next){
  var query = {};
  var select = [];
  var sort = [];
  var lat;
  var lng;
  if(!req.query.lat || !req.query.lng){
    console.log("NOT LAT OR LNG")
    var ip = requestIp.getClientIp(req);
    if (ip == "::1") {
      ip = "167.60.15.23"; //IN DEBUG CANT GET THE IP OF REQUEST
    }
    console.log(ip);
    var geo = geoip.lookup(ip);
    lat = geo.ll[0];
    lng = geo.ll[1];
  } else {
    console.log("YEES LAT AND LNG");
    lat = req.query.lat;
    lng = req.query.lng;
  }
  query['location'] = {
    $near: [
      lat,
      lng
    ],
    $maxDistance: 1000
  };
  if(req.user) {
    console.log("Usuario realizando request");
    if (req.user.user.role == "admin") {
      select = Product.getSelect("admin");
      console.log("Es Admin");
      if (req.query.moderated) query["moderated"] = req.query.moderated;
    } else{
      console.log("No es admin");
      select = Product.getSelect("user");
      query["moderated"] = true;
    }
    if (req.query.my == "true")query["ownerUser"] = req.user.sub;
    if (req.query.solicitatedByMe == "true")query["solicitatedUser"] = req.user.sub;
  }
  var populate = [];
  if (req.query.category) query["category"] = req.query.category;
  if (req.query.recently) sort.push(['created_at',1]);
  if (req.query.maxTendency) sort.push(['countTendency', -1]);
  if (req.query.maxApplicants) sort.push(['applicantsUsers_count', -1]);
  if (req.query.minApplicants) sort.push(['applicantsUsers_count', 1]);
  if (req.query.bestOwnerUser) populate.push('ownerUser');sort.push(['ownerUser.totalStars', 1]);
  if (req.query.injectUsers) populate.push("applicantsUsers");
  console.log("Query: ", query);
  console.log("Select: ", select);
  console.log("Sort: ", sort);
  Product.find(query, select)
    .populate('category')
    // .populate(populate)
    // .populate({path: 'ownerUser', options: { sort: {'totalStars': -1}}})
    .exec(function(err,products){
      console.log("RESULTADOS GET ALLL PRODUCTS: ");
      console.log("ERR: ", err);
      console.log("Products: ", products);
      if (err) return next(err);
      res.status(200).jsonp(products);
  });
};

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

function deleteProductById(req, res, next){
  var productId = req.params.id;
  Product.findByIdAndRemove(productId, function(err, product){
    if(err) return next(err);
    res.status(200).send({message: "Success deleting product"});
  });
}

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

function addNewSolicitationOfProduct(req,res,next){
  var productId = req.params.productId;
  Product.findById(productId, function (err, product){
    if(err) return next(err);
    if(!product) return res.status(404).send({success: false, message: "Product doesnt exists"});
    if(product.ownerUser == req.user.sub) return res.status(400).send({success:false, message: "Cant applicant for your own product!"});
    product.applicantsUsers.push(req.user.sub);
    product.save(function(err2, updateProduct){
      if(err2) return next(err2);
      res.status(200).jsonp(updateProduct);
    })
  });
}

function deleteSolicitationOfProduct(req,res,next){
  var productId = req.params.productId;
  Product.findById(productId, function(err,product){
    if(err) return next(err);
    if(!product) return res.status(404).send({success: false, message: "Product doesnt exists"});

    var indexOf = product.applicantsUsers.indexOf(req.user.sub);
    if (indexOf > -1) {
      product.applicantsUsers.splice(indexOf,1);
      product.save(function(err2, updatedProduct){
        if(err2) return next(err2);
        return res.status(200).jsonp(updatedProduct);
      });
    } else{
      return res.status(422).send(
        {
          code: errCodes.The_user_did_not_solicitated_the_product,
          message: "User was not in products solicitation"
        }
      );
    }
  });
}

function confirmDeliveredFromUserOwnerOrDeliverUser(req,res,next){
  var productId = req.params.productId;
  var userId = req.user.sub;
  Product.findById(productId, function(err,product){
    if(err)return next(err);
    if(!product) return res.status(404).send({success: false, message: "Product doesnt exists"});
    if(product.userToDeliver == userId)
      req.deliverConfirmationUserToDeliver = Date.now();
    else if(product.ownerUser == userId)
      req.deliverConfirmationOwner = Date.now();
    else
      return res.status(403).send({success: false, message: "Unthorized, only owner and user to deliver can confirm"});
    updateProduct(req,res,next,product);
  });
}

function postUserToDeliver(req,res){
  req.userToDeliver = req.body.userToDeliver;
  console.log(req.body.userToDeliver);
  if(!req.userToDeliver) return res.status(400).send({success:false, message: "No user in body"});
  updateProduct(req,res);
}

function updateProduct(req,res,next, product = null){
  var objToUpdate = {};
  if(req.deliverConfirmationUserToDeliver) objToUpdate.deliverConfirmationUserToDeliver = req.deliverConfirmationUserToDeliver;
  if(req.deliverConfirmationOwner) objToUpdate.deliverConfirmationOwner = req.deliverConfirmationOwner;
  if(req.userToDeliver) objToUpdate.userToDeliver = req.userToDeliver;

  console.log(objToUpdate);
  var productId = req.params.productId;
  Product.findById(productId, function(err, product){
    if(err) return next(err);
    if(product.ownerUser == objToUpdate.userToDeliver) return res.status(400).send({success:false, message:"The user to deliver of a product cant be the same as the owner"})
    product.set(objToUpdate);
    product.save(function(err2,updatedProduct){
      if(err2) return next(err2);
      res.status(200).jsonp(updatedProduct);
    })
  });
}

module.exports = {
  getAllProducts,
  getProductById,
  postNewProduct,
  acceptModeratedProduct,
  rejectModeratedProduct,
  addNewImagePathOfProduct,
  deleteProductById,
  addNewSolicitationOfProduct,
  deleteSolicitationOfProduct,
  confirmDeliveredFromUserOwnerOrDeliverUser,
  postUserToDeliver
}
