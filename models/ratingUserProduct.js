const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const RatingUserProductSchema = new Schema({
  user: {
    type: Schema.Types.ObjectId,
    required: [true, 'User is required'],
    ref: 'User'
  },
  product: {
    type: Schema.Types.ObjectId,
    required: [true, 'Product is required'],
    ref: 'Product'
  },
  stars: {
    type: Number,
    required: [true, "Rating is required"]
  }
});

module.exports = mongoose.model('RatingUserProduct',RatingUserProductSchema);
