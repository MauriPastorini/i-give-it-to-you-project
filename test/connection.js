const mongoose = require('mongoose');

//ES6 Promises
mongoose.Promise = global.Promise;

//Connect to the db before tests run
before(function(done){
  //Connecto to mongodb
  mongoose.connect('mongodb://localhost/test_veniporel', { useMongoClient: true });
  mongoose.connection.once('open',function(){
    console.log('Connection with mongoose and mongoDB has been made!');
    done();
  }).on('error',function(error){
    console.log('Error opening mongoose connection: ', error);
  });
});
//Drop the ninjas collection Before each test
beforeEach(function(done){
  //Drop the collections
  mongoose.connection.collections.ninjas.drop(function(){
    mongoose.connection.collections.categories.drop(function(){
        done();
     });
  });
});
