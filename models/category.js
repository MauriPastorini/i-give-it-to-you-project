const mongoose = require('mongoose');
const Schema = mongoose.Schema;

//create ninja Schema & model
const CategorySchema = new Schema({
  name:{
    type:String,
    required:[true,'Name field for category is required'],
    unique:true
  }
});

const Category = mongoose.model('Category', CategorySchema);
module.exports = Category;
