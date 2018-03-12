const db = require('./connection_database');
const express = require('express');
const passport = require('passport');
const routes = require('./routes/api')
const bodyParser = require('body-parser');
const codes = require('./config/errCodes')
const config = require('config');
const myConfig = require('./config/config');

myConfig.configServer();

const morgan = require('morgan');

const app = express();
app.set('view engine', 'ejs');

app.get('/', (req,res)=>{
  res.render('apiInfo');
});

//Morgan: don't show the log when it is test
if(config.util.getEnv('NODE_ENV') !== 'test') {
    //use morgan to log at command line
    app.use(morgan('combined')); //'combined' outputs the Apache style LOGs
}

//Static express for files html,images,etc.
app.use(express.static('public')); //This line is for develop enviroment, for production is not going to be static files to deliver

//BodyParser: Middleware for parsing de request data. Enable the other body parsers when it is needed
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
// app.use(bodyParser.text());
// app.use(bodyParser.json({ type: 'application/json'}));

app.use(passport.initialize());

//Middleware of routes
app.use('/api', routes);

//Error middleware: This function is called when a controller call "next()" method. The "next()" method is called when there is excpetion or error
//                  When "next()" function is called, express call other middleware declare like "app.use(function....)" in order of declaration
app.use(function(err,req,res,next){
  console.log("Err middleware")
  if (!err.errors || err.errors.length == 0) {
    var errors = [];
    if (err.name == "StrictModeError") {
      console.log("ENTRE A STRICT MODE ERROR");
      errors.push({
        code: codes.Fields_Not_Neccesary,
        message: err.message
      });
    }
    if (err.name == "ValidationError") {
      console.log("ENTRE A Validation MODE ERROR");
      console.log(err);
      errors.push({
        code: codes.Incorrect_fields_values,
        message: err.message
      });
    }
    err.errors = errors;
  }
  if (!err.errors || err.errors.length == 0) {
    console.log(err);
    res.status(422).send(err);
  } else{
    res.status(422).send(
      {
        errors: err.errors
      }
    );
  }
});

// Start express app
app.listen(process.env.PORT || config.port, function(){
  console.log('Now listening for request in ' + process.env.PORT + " OR " + config.port);
});
