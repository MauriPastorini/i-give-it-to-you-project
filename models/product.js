const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ProductSchema = new Schema({
  name:{
    type:String,
    required:[true,'Name field is required']
  },
  category:{
    type: String,
    required:[true,'Name field is required']
  },
  moderated:{
    type: Boolean,
    default: false
  }
  //add in geo location
});

const Ninja = mongoose.model('ninja', NinjaSchema);
module.exports = Ninja;
