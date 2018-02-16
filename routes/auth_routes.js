const passportSetup = require('../controllers/auth_controller');
const passport = require('passport');
const request = require('request');

exports.injectRoutes = function(router){
    router.get('/auth/login', function(req,res){
      res.render('login');
    });

    router.get('/auth/logout', (req,res)=>{
      res.send('logging out')
  });
    //auth with google
    router.get('/auth/google', passport.authenticate('google',{
      session: false,
      scope: ['profile','email']
    },
      function(req,res) {
        res.send("OK");
      }
    ));

    //callback route for google to redirect to
    router.get('/auth/google/redirect',function(req,res,next){console.log(req.get('Authorization'));next()}, passport.authenticate('google',{
        session: false
    }),
    function(req,res,next){
      var token = req.user.token;
      req.headers['token'] = token;
      console.log("TOKEN SET: " + JSON.stringify(req.headers));
      next();
    },
    passport.authenticate('jwt', { session: false }),
    function(req,res){
      var token = req.user.token;
      console.log("TOKEEEEEEEEEN", req.user);
      res.status(200).send({
        message: "Te has logueado correctamente",
        token: req.user.token
      });
    });

    router.get('/auth/facebook', passport.authenticate('facebook',{
      session: false,
      scope: 'email'
    },
      function(req,res) {
        res.send("OK");
      }
    ));

    //callback route for google to redirect to
    router.get('/auth/facebook/redirect', passport.authenticate('facebook',{
        session: false,
        scope: 'email'
    }),
    function(req,res,next){
      res.status(200).send({
        message: "Te has logueado correctamente",
        token: req.user.token
      });
      var token = req.user.token;
      // req.headers['token'] = token;
      // console.log("TOKEN SET: " + JSON.stringify(req.headers));
      // next();
    }
    // ,
    // passport.authenticate('jwt', { session: false }),
    // function(req,res){
    //   res.status(200).send({
    //     message: "Te has logueado correctamente",
    //     token: req.user.token
    //   });
    // }
  );


    router.get('/token-valid',function(req,res,next){console.log(req.get('Authorization'));next()},passport.authenticate('jwt', { session: false }),
      function(req, res) {
        res.status(200).send({
          message: "Te has logueado correctamente",
          token: req.user.token
        });
      }
    );
  // router.post('/auth/google', authPassportController)
  // router.post('/auth/login', authPassportController)
}
