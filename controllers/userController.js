'use strict'

const mongoose = require('mongoose');
const User = require('../models/user');
const service = require('../services/index');
const errCodes = require('../config/errCodes');
const https = require('https');

function signIn(req , res, next){
    if (user.facebookId) {
      console.log("Comparando con user facebook");
      User.findOne({facebookId: req.body.facebookId}, function(err,user){
        if(err) return res.status(500).send({message: err});
        if (!user) return res.status(404).send({message: "No existe el usuario"});
        signInWithFacebook(req,res,next,user);
      });

    } else if (user.googleId) {
      console.log("Comparando con user google");
      User.findOne({googleId: req.body.googleId}, function(err,user){
        if(err) return res.status(500).send({message: err});
        if (!user) return res.status(404).send({message: "No existe el usuario"});
        signInWithGoogle(req,res,next,user);
      });
    }else {
      User.findOne({email: req.body.email}, function(err,user){
        if(err) return res.status(500).send({message: err});
        if (!user) return res.status(404).send({message: "No existe el usuario"});
        console.log("Comparando con user local");
        signInLocally(req,res,next,user);
      });
    }
}

function signUp(req, res, next){
  if (req.body.googleToken) {
    signUpWithGoogle(req,res,next);
  } else if (req.body.facebookToken) {
    signUpWithFacebook(req,res,next);
  } else {
    createUser(req,res,next)
  }
}

function signUpWithFacebook(req,res,next){
  var token = req.body.facebookToken;
  if (token) {
    var urlForValidateToken = 'https://graph.facebook.com/me?access_token=' + token;
    https.get(urlForValidateToken, (resp) => {
      let data = '';
      resp.on('data', (chunk) => {
        data += chunk;
      });
      resp.on('end', () => {
        var response = JSON.parse(data);
        if (response.error && resp.statusCode == 400) {
          return res.status(403).jsonp({
            errors: [
              {
                code: errCodes.Invalid_Token,
                message: "Invalid facebook token"
              }
            ]
          });
        } else{
          req.body.facebookId = response.id;
          createUser(req,res,next);
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

function signUpWithGoogle(req,res,next,user){
  var token = req.body.googleToken;
  if (token) {
    var urlForValidateToken = 'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=' + token;
    https.get(urlForValidateToken, (resp) => {
      let data = '';
      // A chunk of data has been recieved.
      resp.on('data', (chunk) => {
        data += chunk;
      });
      resp.on('end', () => {
        var response = JSON.parse(data);
        if (response.error && resp.statusCode == 400) {
          return res.status(403).jsonp({
            errors: [
              {
                code: errCodes.Invalid_Token,
                message: "Invalid google token"
              }
            ]
          });
        } else {
            req.body.googleId = response.sub;
            createUser(req,res,next);
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

function createUser(req,res,next,user){
  User.create(req.body).then(function(user,err){
    if(err) return next(err);
    res.status(200).send({
      token: service.createToken(user),
      user: user
    });
  }).catch(err => {
    next(err);
  });
}

// SIGN IN
function signIn(req , res, next){
    if (req.body.facebookToken) {
        console.log("Comparando con user facebook");
        signInWithFacebook(req,res,next);
    } else if (req.body.googleToken) {
      console.log("Comparando con user google");
      signInWithGoogle(req,res,next);
    }else {
      User.findOne({email: req.body.email}, function(err,user){
        if(err) return res.status(500).send({message: err});
        if (!user) return res.status(404).send({message: "No existe el usuario"});
        console.log("Comparando con user local");
        signInLocally(req,res,next,user);
      });
    }
}

function signInWithFacebook(req,res,next){
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
        } else {
          User.findOne({facebookId: response.id}, function(err,user){
            if(err) return res.status(500).send({message: err});
            if (!user) return res.status(404).send({message: "No existe el usuario"});
            req.user = user
            return res.status(200).jsonp({
              message: "Ok facebook login",
              token: service.createToken(user)
            });
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
    var urlForValidateToken = 'https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=' + token;
    https.get(urlForValidateToken, (resp) => {
      let data = '';
      // A chunk of data has been recieved.
      resp.on('data', (chunk) => {
        data += chunk;
      });
      resp.on('end', () => {
        var response = JSON.parse(data);
        if (response.error && response.statusCode == 400) {
          return res.status(403).jsonp({
            errors: [
              {
                code: errCodes.Invalid_Token,
                message: "Invalid facebook token"
              }
            ]
          });
        } else {
          User.findOne({googleId: response.sub}, function(err,user){
            if(err) return res.status(500).send({message: err});
            if (!user) return res.status(404).send({message: "No existe el usuario"});
            req.user = user
            res.status(200).send({
              message: "Ok google login",
              token: service.createToken(user)
            });
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

function signInLocally(req,res,next,user){
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
  if (req.body.gender)objForUpdate.gender = req.body.gender;
  if (req.body.photo) objForUpdate.photo = req.body.photo;
  if (req.body.password) objForUpdate.password = req.body.password;
  if (role != "") objForUpdate.role = role;

  var setObj = { $set: objForUpdate};
  var userId = req.params.userId;
  User.findById(userId, function(err,user){
    if (err) return next(err);
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
