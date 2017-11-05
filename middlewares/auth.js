'use strict'

const services = require('../services');

function isAuth (req, res, next){
  if (!req.headers.authorization) {
    return res.status(403).send({message: 'No tiene autorizacion'});
  }

  const token = req.headers.authorization.split(" ")[1];
  services.decodeToken(token)
    .then(function(response){
      req.user = response;
      next();
    })
    .catch(function(reject){
      res.status(reject.status);
    })
};

module.exports = isAuth;
