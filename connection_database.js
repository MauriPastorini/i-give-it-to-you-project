const mongoose = require('mongoose');
const config = require('config');

//Mongoose: connect to mongodb
//ES6 Promises
mongoose.Promise = global.Promise;
// var connectionString = 'mongodb://mauri_admin:mauri123@ds249325.mlab.com:49325/venisdb';
// mongoose.connect(config.DBHost, { useMongoClient: true });
var connectionString = process.env.MONGODB_URI || config.DBHost; //If there is a enviroment variable MONGODB_URI it takes that connectionString. If not, it takes the one in config.js under config directory
mongoose.connect(connectionString, { useMongoClient: true });
var db = mongoose.connection;
db.once('open',function(){
  console.log('Connection with mongoose and mongoDB has been made!' + process.env.MONGODB_URI + " OR " + config.DBHost);
}).on('error',function(error){
  console.log('Error opening mongoose connection: ', error);
});
const modelManager = require('./models/modelManager');
modelManager.importModels();

module.exports = db;
