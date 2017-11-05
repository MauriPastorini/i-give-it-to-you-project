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
  image_path:{
    type: String,
    required: [true, 'image_path is required']
  }
});
ProductSchema.plugin(idValidator);
ProductSchema.pre('save', function(next){
  if (this.isNew) {
    this.moderated = false;
  }
  next();
});
const Product = mongoose.model('Product', ProductSchema);
