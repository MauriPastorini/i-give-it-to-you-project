'use strict'

const services = require('../services');
const User = require('../models/user');

function isAuth (req, res, next){
  if (!req.headers.authorization) {
    return res.status(403).send({message: 'Token missing'});
  }

  const token = req.headers.authorization.split(" ")[1];
  services.decodeToken(token)
    .then(function(response){
      req.user = response;
      next();
    })
    .catch(function(reject){
      return res.status(reject.status).send({
        success: false,
        message: reject.message
      });
    })
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

module.exports = {
  isAuth,
  isAdmin,
  isUser
}
