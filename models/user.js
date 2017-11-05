'use strict'

const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcrypt-nodejs');
const crypto = require('crypto');
// const countries = require('country-data').countries

const UserSchema = new Schema({
  email:{
    type:String,
    unique: true,
    lowercase:true,
    required: [true, "The email is required"]
  },
  username: {
    type: String,
    unique: true,
    required: [true, "The username is required"]
  },
  country: {
    type: String,
    enum: ["Uruguay", "Argentina"],
    required: [true, "The country is required"]
  },
  avatar: String,
  password: {
    type: String,
    required: [true, "The password is required"]
  },
  signupDate: {
    type: Date,
    default: Date.now()
  },
  lastLogin: Date
});

UserSchema.pre('save', function(next){
  let user = this
  if (!user.isModified('password')) return next();
  bcrypt.genSalt(10, function(err, salt){
    if(err) return next(err);
    bcrypt.hash(user.password, salt,null,function(err,hash){
        if(err) return next(err);
        user.password = hash;
        next();
    });
  });
});

UserSchema.statics.comparePassword = function(candidatePassword, dbPassword, cb) {
    bcrypt.compare(candidatePassword, dbPassword, function(err, isMatch) {
      if (err) {
        return cb(err);
      }else {
        return cb(null,isMatch);
      }
    });
};

UserSchema.statics.test = function(){console.log("OK")};

UserSchema.methods.gravatar = function(){
  if(!this.email) return 'https://gravatar.com/avatar/?s=200&d=retro';
  const md5 = crypto.createHash('md5').update(this.email).digest('hex');
  return 'https://gravatar.com/avatar/${md5}?s=200&d=retro';
}

module.exports = mongoose.model('User', UserSchema);
