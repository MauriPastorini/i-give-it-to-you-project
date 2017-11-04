const mongoose = require('mongoose');
const Schema = mongoose.Schema;

//create ninja Schema & model
const CategorySchema = new Schema({
  name:{
    type:String,
    required:[true,'Name field for category is required']
  }
});

const Category = mongoose.model('category', CategorySchema);
module.exports = Category;
