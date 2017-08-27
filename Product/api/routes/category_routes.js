const express = require('express');
const router = express.Router();
const Category = require('../models/category');

//get a list of categories from the db
router.get('/category',function(req,res,next){
    Category.find({}).then(function(res){
      res.send(res);
    });
});

// //add a new ninja to the db
// router.post('/ninjas', function(req,res,next){
//   Ninja.create(req.body).then(function(ninja){
//     res.send(ninja);
//   }).catch(next);
// });
//
// //update a ninja in the db
// router.put('/ninjas/:id', function(req,res,next){
//   Ninja.findByIdAndUpdate({_id: req.params.id},req.body).then(function(ninja){
//     Ninja.findOne({_id: req.params.id}).then(function(ninja){
//       res.send(ninja);
//     });
//   });
// });
//
// //delete a ninja from the db
// router.delete('/ninjas/:id', function(req,res,next){
//     Ninja.findByIdAndRemove({_id: req.params.id}).then(function(ninja){
//       res.send(ninja);
//     }).catch(next);
// });

module.exports = router;
