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
      User.findOne({_id: req.user.userId}, function(err, userDb){
        if(err) return res.status(500).send({message: err});
        console.log(userDb);
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
      User.findOne({_id: req.user.userId}, function(err, userDb){
        if(err) return res.status(500).send({message: err});
        console.log(userDb);
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
