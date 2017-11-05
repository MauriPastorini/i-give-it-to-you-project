const db = require('./connection_database');

const express = require('express');
const routes = require('./routes/api')
const bodyParser = require('body-parser');

const config = require('config');
const morgan = require('morgan');

//set up express app
const app = express();

//Morgan: don't show the log when it is test
if(config.util.getEnv('NODE_ENV') !== 'test') {
    //use morgan to log at command line
    app.use(morgan('combined')); //'combined' outputs the Apache style LOGs
}

//Static express for files html,images,etc
app.use(express.static('public'));

//BodyParser: Middleware for parsing de request data
app.use(bodyParser.json());
//Habilitar los siguientes middle ware cuando los necesite
app.use(bodyParser.urlencoded({extended: true}));
// app.use(bodyParser.text());
// app.use(bodyParser.json({ type: 'application/json'}));

//Middleware of routes
app.use('/api', routes);

//error handling middleware
app.use(function(err,req,res,next){
  // console.log(err);
  console.log("Middleware error handling validation")
  // res.status(422).send({error:err._message});
  res.status(422).send(err);
});

// listen for request
app.listen(process.env.PORT || config.port, function(){
  console.log('Now listening for request in ' + process.env.PORT + " OR " + config.port);
});
