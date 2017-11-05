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

function signIn(req , res){
  User.find({email: req.body.email}, function(err,user){
    if(err) return res.status(500).send({message: err});
    if (!user) return res.status(404).send({message: "No existe el usuario"});

    req.user = user
    res.status(200).send({
      message: "Te has logueado correctamente",
      token: service.createToken(user)
    });
  });
}

module.exports = {
  signIn,
  signUp
}
