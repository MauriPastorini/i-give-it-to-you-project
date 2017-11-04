const assert = require('assert');
const Ninja = require('../models/ninja');

//Describe tests
describe('Finding records of ninja', function(){

var char;

beforeEach(function(done){
  char = new Ninja(
    { "name": "E Honda", "rank": "no belt", "available": true, "geometry" : {"type": "point", "coordinates": [-81.500, 24.10]} }
  );
  char.save().then(function(){
    assert(char.isNew === false);
    done();
  });
});

  //Find one
  it('Find a record from the database', function(done){
    Ninja.findOne({name:'E Honda'}).then(function(ninja){
      assert(ninja.name === 'E Honda');
      done();
    });
  });

  it('Find a record by ID from the database', function(done){
    Ninja.findOne({ _id:char._id}).then(function(ninja){
      assert(ninja._id.toString() === char._id.toString());
      done();
    });
  });
});
