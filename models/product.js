const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const idValidator = require('mongoose-id-validator');
const codes = require('../config/errCodes');

// const GeoSchema = new Schema({
//   type:{
//     type:"String",
//     default: "Point"
//   },
//   coordinates: {
//     type:[Number],
//     index: "2dsphere"
//   }
// });

const ProductSchema = new Schema({
  name:{
    type:String,
    required:[true,'Name field is required']
  },
  description:{
    type:String,
    required:[true,'Description field is required']
  },
  category:{
    type: Schema.Types.ObjectId,
    required:[true,'Category field is required'],
    ref: 'Category'
  },
  moderated:{
    type: Boolean,
    default: false
  },
  condition:{
    type: String,
    required:[true, 'State field is required'],
    enum:["New","Like New", "Very Good", "Good", "Acceptable"]
  },
  location:{
    type: [Number],
    index: '2d',
    required: [true, 'Location field is required']
  },
  image_path:[{
    type: String,
    required: [true, 'image_path is required']
  }],
  ownerUser:{
    type: Schema.Types.ObjectId,
    required: [true, 'User owner is required'],
    ref: 'User'
  },
  applicantsUsers:[{
    type: Schema.Types.ObjectId,
    ref: 'User'
  }],
  applicantsUsers_count:{
    type: Number,
    required: true,
    default: 0
  },
  userToDeliver:{
    type: Schema.Types.ObjectId,
    ref: 'User'
  },
  deliverConfirmationOwner: {
    type: Date
  },
  deliverConfirmationUserToDeliver:{
    type: Date
  },
  countTendency:{
    type: Number
  },
  created_at: {
    type: Date,
    required: true,
    default: Date.now
  }
});
ProductSchema.plugin(idValidator);
ProductSchema.pre('save', function(next){
  if (this.isNew) {
    this.moderated = false;
    this.countTendency = 0;
  }
  next();
});
ProductSchema.statics.getSelect = function(role){
  if (role == "admin") {
    return [];
  } else if(role == "user"){
    return ['-moderated'];
  }
}

var handlePostErrorsMessages = function(error, res, next) {
  console.log("ENTRE a handle post product");
  if(error){
    var errors = [];
    if (error.name == "ValidationError") {
      console.log("Entre a validation error");
      for (var field in error.errors) {
        console.log("Entre a field");
        console.log(error.errors[field]);
        if (error.errors[field].kind == "user defined" && error.errors[field].path == "ownerUser" && error.errors[field].reason == undefined)  {
          errors.push({
            code: codes.User_Owner_Not_exists,
            message: error.errors[field].message
          })
        };
        if (error.errors[field].kind == "user defined" && error.errors[field].path == "category" && error.errors[field].reason == undefined)  {
          errors.push({
            code: codes.Category_Not_exists,
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



ProductSchema.post('save', handlePostErrorsMessages);
ProductSchema.post('update', handlePostErrorsMessages);
ProductSchema.post('findOneAndUpdate', handlePostErrorsMessages);
ProductSchema.post('insertMany', handlePostErrorsMessages);

module.exports = mongoose.model('Product', ProductSchema);
