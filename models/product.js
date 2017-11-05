const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const idValidator = require('mongoose-id-validator')

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
  }
  //add in geo location
});
ProductSchema.plugin(idValidator);

const Product = mongoose.model('Product', ProductSchema);
