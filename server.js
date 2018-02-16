const db = require('./connection_database');
const express = require('express');
const passport = require('passport');
const routes = require('./routes/api')
const bodyParser = require('body-parser');

const config = require('config');
const myConfig = require('./config/config');
myConfig.configServer();

const morgan = require('morgan');

const app = express();
app.set('view engine', 'ejs');

app.get('/', (req,res)=>{
  res.render('home');
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
  // console.log(err);
  console.log("Middleware error handling validation")
  console.log(err);
  // res.status(422).send({error:err._message});
  res.status(422).send(err); //WARNING: Currently I am sending all the jason error, then with only the code error and logging it, will be enough for Security.
});

// Start express app
app.listen(process.env.PORT || config.port, function(){
  console.log('Now listening for request in ' + process.env.PORT + " OR " + config.port);
});
