'use strict'

const jwt = require('jwt-simple');
const moment = require('moment');
const config = require('../config/config');

function createToken(user){
  const payload = {
    userId: user._id,
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

module.exports = {
  createToken,
  decodeToken
}
