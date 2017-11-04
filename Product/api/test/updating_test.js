const assert = require('assert');
const Ninja = require('../models/ninja');

//Describe tests
describe('Deleting records of ninja', function(){

var char;

beforeEach(function(done){
  char = new Ninja(
    { "name": "E Honda", "rank": "no belt", "available": true, "geometry" : {"type": "point", "coordinates": [-81.500, 24.10]},
  "weight": 50
  }
  );
  char.save().then(function(){
    assert(char.isNew === false);
    done();
  });
});

  //Find one
  it('Update a record from the database', function(done){
    Ninja.findOneAndUpdate({name:'E Honda'},{name:'MauriHonda'}).then(function(){
      Ninja.findOne({_id: char._id}).then(function(result){
        assert(result.name === 'MauriHonda');
        done();
      });
    });
  });
  
  //UPDATE OPERATORS
  it('Update a record: increase weight', function(done){
    Ninja.update({}, { $inc:{ weight: 1 } }).then(function(){
      Ninja.findOne({name: 'E Honda'}).then(function(result){
        assert(result.weight === 51);
        done();
      });
    });
  });
});
