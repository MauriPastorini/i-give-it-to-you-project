'use strict'

const mongoose = require('mongoose');
const User = require('../models/user');
const service = require('../services/index');
const errCodes = require('../config/errcodes');
const https = require('https');

function updateUserPhoto(req,res,next){

}

function signUp(req, res, next){
  console.log("ESTOY EN SIGN UPPP");
  console.log(req.body);
  User.create(req.body).then(function(user,err){
    if(err) return next(err);
    res.status(200).send({
      token: service.createToken(user),
      user: user
    });
    //return res.status(200).send({token: service.createToken(user)});
  }).catch(err => {
    next(err);
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
    if (user.facebookId) {
      console.log("Comparando con user facebook");
      signInWithFacebook(req,res,next,user);
    } else if (user.googleId) {
      console.log("Comparando con user google");
      signInWithGoogle(req,res,next,user);
    }else {
      console.log("Comparando con user local");
      signInLocally(req,res,next,user);
    }
  });
}

function signInWithFacebook(req,res,next,user){
  var token = req.body.facebookToken;
  if (token) {
    var urlForValidateToken = 'https://graph.facebook.com/me?access_token=' + token;
    https.get(urlForValidateToken, (resp) => {
      let data = '';
      // A chunk of data has been recieved.
      resp.on('data', (chunk) => {
        data += chunk;
      });
      resp.on('end', () => {
        console.log("TERMINE DE LEER FACEBOOK");
        console.log(data);
        var response = JSON.parse(data);
        if (response.error && response.error.code == 190) {
          return res.status(403).jsonp({
            errors: [
              {
                code: errCodes.Invalid_Token,
                message: "Invalid facebook token"
              }
            ]
          });
        } else if (response.id == user.facebookId) {
            req.user = user
            res.status(200).send({
              message: "Ok facebook login",
              token: service.createToken(user)
            });
          } else {
            return res.status(403).jsonp({
              errors: [
                {
                  code: errCodes.User_Does_Not_Exists,
                  message: "Valid token but facebook user doesnt exists"
                }
              ]
            });
          }
        });
      }).on("error", (err) => {
      next(err);
    });
  } else{
    return res.status(422).jsonp({
      errors: [
        {
          code: errCodes.Missing_Information,
          message: "Some information for authentication is missing"
        }
      ]
    });
  }
}

function signInWithGoogle(req,res,next,user){
  var token = req.body.googleToken;
  if (token) {
    var urlForValidateToken = 'https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=' + googleToken;
    https.get(urlForValidateToken, (resp) => {
      let data = '';
      // A chunk of data has been recieved.
      resp.on('data', (chunk) => {
        data += chunk;
      });
      resp.on('end', () => {
        console.log("TERMINE DE LEER FACEBOOK");
        console.log(data);
        var response = JSON.parse(data);
        if (response.error && response.error.code == 190) {
          return res.status(403).jsonp({
            errors: [
              {
                code: errCodes.Invalid_Token,
                message: "Invalid facebook token"
              }
            ]
          });
        } else if (response.id == user.facebookId) {
            req.user = user
            res.status(200).send({
              message: "Ok facebook login",
              token: service.createToken(user)
            });
          } else {
            return res.status(403).jsonp({
              errors: [
                {
                  code: errCodes.User_Does_Not_Exists,
                  message: "Valid token but facebook user doesnt exists"
                }
              ]
            });
          }
        });
      }).on("error", (err) => {
      next(err);
    });
  } else{
    return res.status(422).jsonp({
      errors: [
        {
          code: errCodes.Missing_Information,
          message: "Some information for authentication is missing"
        }
      ]
    });
  }
}

function signInWithGoogle(req,res,next, googleToken){
  var token = req.body.googleToken;
  if (token) {
    var urlForValidateToken = 'https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=' + googleToken;
    https.get(urlForValidateToken, (resp) => {
      let data = '';
      // A chunk of data has been recieved.
      resp.on('data', (chunk) => {
        data += chunk;
      });
      resp.on('end', () => {
        console.log("TERMINE DE LEER GOOGLE");
        console.log(data);
        console.log(JSON.parse(data).explanation);
      });
    }).on("error", (err) => {
      next(err);
    });
  } else{
    return res.status(422).jsonp({
      errors: [
        {
          code: errCodes.Missing_Information,
          message: "Some information for authentication is missing"
        }
      ]
    });
  }
}

function signInLocally(req,res,next){
  User.comparePassword(req.body.password, user.password, function(err, isMatch){
    console.log(err);
    console.log(isMatch);
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
  if (req.body.photo) objForUpdate.photo = req.body.photo;
  if (req.body.password) objForUpdate.password = req.body.password;
  if (role != "") objForUpdate.role = role;

  var setObj = { $set: objForUpdate};
  var userId = req.params.userId;
  User.findById(userId, function(err,user){
    if (err) return next(err);
    console.log("VOYT A LEER USUARIO");
    console.log(user);
    if(user){
      user.set(objForUpdate);
      user.save(function(err2,updatedUser){
        if (err2) return next(err2);
        res.status(200).jsonp(updatedUser);
      });
    } else{
      res.status(404).send();
    }
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
