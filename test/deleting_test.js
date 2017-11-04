const assert = require('assert');
const Ninja = require('../models/ninja');

//Describe tests
describe('Deleting records of ninja', function(){

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
  it('Delete a record from the database', function(done){
    Ninja.findOneAndRemove({name:'E Honda'}).then(function(){
      Ninja.findOne({name:'E Honda'}).then(function(result){
        assert(result === null);
        done();
      });
    });
  });
});
