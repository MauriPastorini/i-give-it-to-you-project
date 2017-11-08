'use strict'

const jwt = require('jwt-simple');
const moment = require('moment');
const config = require('../config/config');

function createToken(user){
  const payload = {
    sub: user._id,
    iat: moment().unix(),
    exp: moment().add(14, 'days').unix(), //Caduque en 14 dias
  }
  return jwt.encode(payload, config.SECRET_TOKEN);
}

function decodeToken(token){
  const decode = new Promise(function(resolve, reject){
    try{
      const payload = jwt.decode(token, config.SECRET_TOKEN);
      if (payload.exp <= moment().unix()) {
        reject({
          status: 401,
          message: 'El token ha expirado'
        })
      }
      resolve(payload);
    } catch(err){
      reject({
        status: 403,
        message: "Invalid Token"
      })
    }
  });
  return decode;
}

// function getUserIdFromToken(req){
//   // if (!req.headers.authorization) {
//   //   return res.status(403).send({message: 'Token missing'});
//   // }
//   //
//   // const token = req.headers.authorization.split(" ")[1];
//   // decodeToken(token)
//   //   .then(function(response){
//   //     req.user = response;
//   //
//   //   })
// }

module.exports = {
  createToken,
  decodeToken
  // getUserIdFromToken
}
