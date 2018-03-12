'use strict'

const services = require('../services');
const User = require('../models/user');

function isToken(req){
  if (req.headers.authorization) {
    console.log("VOY A DAR OK");
    return true;
  } else{
    console.log("NO VOY A DAR OK");
    return false;
  }
}

function isAuth (req, res, next, lock = true){
  if (lock && !req.headers.authorization) {
    return res.status(403).send({message: 'Token missing'});
  }
  if (req.headers.authorization) {
    const token = req.headers.authorization.split(" ")[1];
  services.decodeToken(token)
    .then(function(response){
      console.log("TOKEN DECODED OK");
      req.user = response;
      next();
    })
    .catch(function(reject){
      console.log("TOKEN ERROR");
      if (lock) {
        return res.status(reject.status).send({
          success: false,
          message: reject.message
        });
      } else{
        next();
      }
    });
  } else {
    next();
  }
};

function isAdmin (req, res, next){
  if (!req.headers.authorization) {
    return res.status(403).send({message: 'Token missing'});
  }

  const token = req.headers.authorization.split(" ")[1];
  services.decodeToken(token)
    .then(function(response){
      req.user = response;
      console.log("req.user.userId",req.user.sub);
      User.findOne({_id: req.user.sub}, function(err, userDb){
        if(err) return res.status(500).send({message: err});
        if(!userDb) return res.status(203).send({message: "Token correct, but user doesnt exists"});
        if (userDb.role == 'admin') {
          next();
        } else{
          return res.status(403).send({
            success: false,
            message: "Unthorized role for request"
          });
        }
      });

    })
    .catch(function(reject){
      return res.status(reject.status).send({
        success: false,
        message: reject.message
      });
    })
};

function isUser (req, res, next){
  if (!req.headers.authorization) {
    return res.status(403).send({message: 'Token missing'});
  }

  const token = req.headers.authorization.split(" ")[1];
  services.decodeToken(token)
    .then(function(response){
      req.user = response;
      User.findOne({_id: req.user.sub}, function(err, userDb){
        if(err) return res.status(500).send({message: err});
        if (userDb.role == 'user') {
          next();
        } else{
          return res.status(403).send({
            success: false,
            message: "Unthorized role for request"
          });
        }
      });

    })
    .catch(function(reject){
      return res.status(reject.status).send({
        success: false,
        message: reject.message
      });
    })
};

function injectUser(req,res,next,lock = true){
  console.log("ESTOY EN INJECT USER");
  if (req.user) {
    User.findById(req.user.sub, function(err, user){
        if(err) return next(err);
        req.user.user = user;
        next();
    });
  } else{
    if (lock) {
      return res.status(reject.status).send({
        success: false,
        message: reject.message
      });
    } else {
      console.log("SIGO DE LARGO");
      next();
    }
  }


}

module.exports = {
  isAuth,
  isAdmin,
  isUser,
  injectUser,
  isToken
}
