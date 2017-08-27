const assert = require('assert');
const Ninja = require('../models/ninja');

//Describe tests
describe('Saving records of ninja', function(){

  //Create tests
  it('Saves a record to the database', function(done){
    var char = new Ninja(
      { "name": "E Honda", "rank": "no belt", "available": true, "geometry" : {"type": "point", "coordinates": [-81.500, 24.10]} }
    );
    char.save().then(function(){
      assert(char.isNew === false);
      done();
    });
  });
});
