const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ReviewUserProductSchema = new Schema({
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
  description: String,
  stars: {
    type: Number,
    required: [true, "Rating is required"],
    min: 1,
    max: 5
  }
});
ReviewUserProductSchema.index({user: 1, product: 1},{unique: true});
module.exports = mongoose.model('Review',ReviewUserProductSchema);
