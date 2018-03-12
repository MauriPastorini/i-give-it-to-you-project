'use strict'

const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcrypt-nodejs');
const crypto = require('crypto');
const codes = require('../config/errCodes')
// const countries = require('country-data').countries

const UserSchema = new Schema({
  googleId: {
    type: String,
    unique: true,
    sparse: true
  },
  facebookId: {
    type: String,
    unique: true,
    sparse: true
  },
  email:{
    type:String,
    unique: true,
    lowercase:true,
    required: [true, "The email is required"]
  },
  name:{
    type: String,
    required: [true, "The name is required"]
  },
  lastName:{
    type: String,
    required: [true, "The last name is required"]
  },
  birthday:{
    type:Date,
    // required: [true, "The birthday is required"]
  },
  // username: {
  //   type: String
  //   // unique: true,
  //   // required: [true, "The username is required"]
  // },
  gender: {
    type: String,
    enum: ["male", "female", "other"]
    // required: [true, "The gender is required"]
  },
  country: {
    type: String,
    enum: ["Uruguay", "Argentina"],
    // required: [true, "The country is required"]
  },
  photo: String,
  password: {
    type: String,
    // required: [true, "The password is required"]
  },
  signupDate: {
    type: Date,
    default: Date.now()
  },
  lastLogin: Date,
  role: {
    type: String,
    default: 'user',
    required: true,
    enum: ["user","admin"]
  },
  totalStars: {
    type: Number,
    default: 0
  }
},{strict: "throw"});

UserSchema.pre('save', function(next){
  console.log("PRE SAVE");
  var isNew = this.isNew;
  let user = this;
  validateSaveOrUpdate(user, isNew, next);
});

// UserSchema.pre('update', function(next){
//   var isNew = this.isNew;
//   let user = this;
//   validateSaveOrUpdate(user, isNew, next);
// });

function validateSaveOrUpdate(user, isNew, next){
  if (isNew){
    user.role = 'user';
    user.totalStars = 0;
  }
  // if (!user.googleId && !user.isModified('password')) return next();
  if (user.facebookId || user.googleId || !user.isModified('password')){
    console.log("No voy a encriptar password");
    return next();
  }
  console.log("VOOY A ENCRIPTAR");

  bcrypt.genSalt(10, function(err, salt){
    if(err) return next(err);
    bcrypt.hash(user.password, salt,null,function(err,hash){
        if(err) return next(err);
        user.password = hash;
        next();
    });
  });
  // console.log("user",user);
  // if (user.googleId) {
  //   console.log("IS FROM GOOGLE")
  //   user.birthday = null;
  // }
}

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

UserSchema.statics.isAdmin = function(user){
  return user.role == 'admin';
}

UserSchema.statics.isUser = function(user){
  return user.role == 'user';
}

var handlePostErrorsMessages = function(error, res, next) {
  console.log("ENTRE a handle post");
  if(error){
    var errors = [];
    if (error.name == "ValidationError") {
      console.log("Entre a validation error");
      for (var field in error.errors) {
        console.log("Entre a field");
        console.log(error.errors[field]);
        if (error.errors[field].kind == "required" && error.errors[field].path == "email")  {
          errors.push({
            code: codes.User_Email_Required,
            message: error.errors[field].message
          })
        };
        if (error.errors[field].kind == "required" && error.errors[field].path == "name")  {
          errors.push({
            code: codes.User_Name_Required,
            message: error.errors[field].message
          })
        };
        if (error.errors[field].kind == "required" && error.errors[field].path == "lastName")  {
          errors.push({
            code: codes.User_Lastname_Required,
            message: error.errors[field].message
          })
        };
        if (error.errors[field].kind == "required" && error.errors[field].path == "role")  {
          errors.push({
            code: codes.User_Name_Required,
            message: error.errors[field].message
          })
        };
      }
    }
    if (error.name === 'MongoError' && error.code === 11000) {
      console.log("ENTRE A MONGO ERROR ");
      console.log(error.errmsg.split(":")[2]);
      var field = error.errmsg.split(":")[2].substring(0,error.errmsg.split(":")[2].lastIndexOf("_")).trim();
      errors.push(
          {
            code: codes.Duplicated_Attribute,
            message: "Duplicatated attribute: " + field,
            field: field
          }
        );
    }
    error.errors = errors;
    next(error);
  } else{
    next();
  }
};
// //
// UserSchema.pre('save', handlePreErrorsMessages);
// UserSchema.pre('update', handlePreErrorsMessages);
// UserSchema.pre('findOneAndUpdate', handlePreErrorsMessages);
// UserSchema.pre('insertMany', handlePreErrorsMessages);

UserSchema.post('save', handlePostErrorsMessages);
UserSchema.post('update', handlePostErrorsMessages);
UserSchema.post('findOneAndUpdate', handlePostErrorsMessages);
UserSchema.post('insertMany', handlePostErrorsMessages);
// //


module.exports = mongoose.model('User', UserSchema);
