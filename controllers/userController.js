'use strict'

const mongoose = require('mongoose');
const User = require('../models/user');
const service = require('../services/index');
const manageErrorCodes = require('../services/manageErrors');

function signUp(req, res, next){
  console.log("ESTOY EN SIGN UPPP");
  console.log(req.body);
  User.create(req.body).then(function(user,err){
    if(err) return next(err);
    res.status(200).send({
      message: "Usuario registrado correctamente",
      token: service.createToken(user)
    });
    //return res.status(200).send({token: service.createToken(user)});
  }).catch(err => {
    res.status(422).send(err.errors);
  });
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

function updateUser(req, res, next, role = ""){
  var objForUpdate = {};
  if (req.body.country)objForUpdate.country = req.body.country;
  if (req.body.avatar) objForUpdate.avatar = req.body.avatar;
  if (req.body.password) objForUpdate.password = req.body.password;
  if (role != "") objForUpdate.role = role;

  var setObj = { $set: objForUpdate};
  var userId = req.params.userId;
  User.findById(userId, function(err,user){
    if (err) return next(err);
    user.set(objForUpdate);
    user.save(function(err2,updatedUser){
      if (err2) return next(err2);
      res.status(200).jsonp(updatedUser);
    });
  });
}

function setUserToAdmin(req,res,next){
  console.log("SET USER TO ADMIN");
  updateUser(req, res, next,"admin");
}

function deleteUser(req,res,next){
  var userId = req.params.id;
  User.findByIdAndRemove(userId, function(err,data){
    if (err) return next(err);
    res.status(200).send({message: "Success deleting user"});
  });
}

module.exports = {
  signIn,
  signUp,
  getAllUsers,
  updateUser,
  setUserToAdmin,
  deleteUser
}
