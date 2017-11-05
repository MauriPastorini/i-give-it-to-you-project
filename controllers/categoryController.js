var mongoose = require('mongoose');
var Category = mongoose.model('Category');

exports.getAllCategories = function(req,res){
  Category.find({}, function(err, categories){
    if (err) {
      res.status(500).send({success: false, message: "Internal error on getAllCategories"});
    }else {
      res.status(200).jsonp(categories);
    }
  });
}
