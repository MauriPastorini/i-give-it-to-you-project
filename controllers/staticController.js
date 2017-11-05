var mongoose = require('mongoose');

exports.healthCheck = function(req, res){
  res.send('OKs');
}
