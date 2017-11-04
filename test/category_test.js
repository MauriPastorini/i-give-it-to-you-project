const assert = require('assert');
const async = require('async');
const Category = require('../models/category');

//Describe tests
describe('Category model tests', function(){

  //Create new category
  it('Saves a new category', function(done){
    var cate = new Category(
      { "name": "Nuevo" }
    );
    cate.save().then(function(){
      assert(cate.isNew === false);
      done();
    });
  });

  it('Saves and get multiples categories', function(done){
    var nuevo = new Category(
      { "name": "Nuevo" }
    );
    var usado = new Category({
      "name":"Usado"
    });
    var cates = [nuevo,usado];

    console.log(cates);
    async.eachSeries(cates, function(cate,asyncdone){
      console.log("DEEEEEENTRO",cate);
      cate.save(asyncdone);
    }, function(err) {
      if (err){
        console.log(err);
      };
      assert(err==null);
      assert(nuevo.isNew === false && usado.isNew === false);
      Category.find({}).then(function(res2){
          console.log('Toy');
          console.log(res2);
          assert(res2.length === 2);
          done(err);
      });
    });
  });
});
