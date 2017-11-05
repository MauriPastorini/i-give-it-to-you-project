'use strict'

const services = require('../services');

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

module.exports = isAuth;
