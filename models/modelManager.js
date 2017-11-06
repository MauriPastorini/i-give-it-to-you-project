const mongoose = require('mongoose');

exports.importModels = function(){
  var category = require('./category');
  var product = require('./product');

  initServer();
}

function initServer(){
  var Category = mongoose.model('Category')
  var fs = require('fs');
  var categoriesObj = JSON.parse(fs.readFileSync('config/categories.json', 'utf8'));

  // Category.remove({}, function(err){
  //   Category.create(categoriesObj,function(err){
  //     if (err) {
  //       console.log("ERROR IMPORTANDO DATOS");
  //     } else {
  //       console.log("Categorias importadas con exito");
  //     }
  //   });
  // })
}
