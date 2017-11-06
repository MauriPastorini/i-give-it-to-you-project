'use strict'

const mongoose = require('mongoose');
const User = require('../models/user');
const service = require('../services/index');

function signUp(req, res, next){
  User.create(req.body).then(function(user,err){
    if(err) return next(err);
    return res.status(200).jsonp(user);
    //return res.status(200).send({token: service.createToken(user)});
  }).catch(next);
  // const user = new User({
  //     email:req.body.email,
  //     displayName: req.body.displayName,
  //     password: req.body.password
  // });
  // user.save(function(err){
    // if(err) return next(err
  // });
}

function signIn(req , res, next){
  User.findOne({email: req.body.email}, function(err,user){
    if(err) return res.status(500).send({message: err});
    if (!user) return res.status(404).send({message: "No existe el usuario"});
    User.comparePassword(req.body.password, user.password, function(err, isMatch){
       if (err) return next(err);
       if (isMatch) {
         req.user = user
         res.status(200).send({
           message: "Te has logueado correctamente",
           token: service.createToken(user)
         });
       } else {
         res.status(403).send({message: "User and/or password incorrect"})
       }
    });
  });
}

function getAllUsers(req, res, next){
  var query = {};
  if (req.query.role) {
    query["role"] = req.query.role;
  }
  User.find(query,function(err, users){
    if (err) {
      res.status(500).send({success: false, message: "Internal error on getAllUsers"});
    }else {
      res.status(200).jsonp(users);
    }
  });
}

function updateUser(req, res, next){
  var objForUpdate = {}
  console.log("VAMOS", req);
  var userId = req.user.sub;

  if (req.body.country) objForUpdate.country = req.body.contry;
  if (req.body.avatar) objForUpdate.avatar = req.body.avatar;

  var setObj = { $set: objForUpdate};

  User.findOneAndUpdate(userId, setObj, {new: true}, function(err,user){
    if (err) {
      res.status(500).send({success: false, message: "Internal error on getAllUsers"});
    } else {
      res.status(200).jsonp(user);
    }
  });
}

module.exports = {
  signIn,
  signUp,
  getAllUsers,
  updateUser
}
