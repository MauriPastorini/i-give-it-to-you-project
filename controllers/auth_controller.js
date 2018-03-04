const passport = require('passport');
const GoogleStrategy = require('passport-google-oauth20');
const BearerStrategy = require('passport-http-bearer').Strategy;
const FacebookStrategy = require('passport-facebook').Strategy
const keys = require('../config/keys');
const User = require('../models/user');
const Service = require('../services/index');

passport.use(
  new GoogleStrategy({
    callbackURL: '/api/auth/google/redirect',
    clientID: keys.google.clientID,
    clientSecret: keys.google.clientSecret
  },(accessToken, refreshToken, profile, done)=>{
    console.log("access token", accessToken);
    var googleId = profile.id;
    console.log("googleId", googleId);
    var name = profile.name.givenName;
    var lastName = profile.name.familyName;
    var photo = profile._json.image.url;
    var gender = profile.gender;
    var email = profile.emails[0].value;

    var user = User.findOne({googleId: googleId}).then((userFromGoog) =>{
      console.log("USER FROM DB GOOGLE",userFromGoog);
      if (!userFromGoog) {
        console.log("NEW USER");
        new User({
          googleId: googleId,
          email: email,
          name: name,
          lastName: lastName,
          gender: gender,
          photo: photo
        }).save().then((user,err) => {
          if(err) return res.status(400).jsonp(err);
          console.log('NEW USER CREATED', user);
          var response = {
            success: true,
            message: "Usuario registrado correctamente",
            token: Service.createToken(user)
          }
          done(null, response);
        });
      } else{
        console.log("USER ALREADY Exsists");
        var response = {
          success: true,
          message: "Usuario registrado correctamente",
          token: Service.createToken(userFromGoog)
        }
        done(null, response);
      }
    });
  })
);

passport.use(
       new BearerStrategy(
           function(token, done) {
               User.findOne({ googleId: token },
                   function(err, user) {
                       if(err) {
                           return done(err)
                       }
                       if(!user) {
                           return done(null, false)
                       }

                       return done(null, user, { scope: 'all' })
                   }
               );
           }
       )
   );


const JwtStrategy = require('passport-jwt').Strategy,
    ExtractJwt = require('passport-jwt').ExtractJwt;
const cfg = require('../config/config');

var opts = {}
opts.jwtFromRequest = ExtractJwt.fromHeader('token');
opts.secretOrKey = cfg.SECRET_TOKEN;
passport.use(new JwtStrategy(opts,
  function(jwt_payload, done) {
    console.log("JWT STRATEGY");
    console.log(jwt_payload);
    User.findOne({_id: jwt_payload.sub}, function(err, user) {
        if (err) return done(err, false);
        if (user) {
            return done(null, user);
        } else {
            return done(null, false);
        }
    });
}));

optionsFacebook = {
       clientID: keys.facebook.clientID,
       clientSecret: keys.facebook.clientSecret,
       callbackURL: '/api/auth/facebook/redirect',
       profileFields: ['id', "birthday", "emails", "first_name", "last_name", 'displayName', 'gender', 'picture.type(large)']
   };
passport.use(
    new FacebookStrategy(
        optionsFacebook,
        function(accessToken, refreshToken, profile, done) {
          console.log("access token", accessToken);
          console.log("profile", profile);
          console.log("emails", profile.emails);
          var facebookId = profile.id;
          console.log("facebookId", facebookId);
          var name = profile.name.givenName;
          var lastName = profile.name.familyName;
          var avatar = profile.photos[0].value;
          var gender = profile.gender;
          // var email = profile.emails[0].value;

          var user = User.findOne({facebookId: facebookId}).then((userFromFacebook) =>{
            console.log("USER FROM DB FACEBOOK",userFromFacebook);
            if (!userFromFacebook) {
              console.log("NEW USER");
              new User({
                facebookId: facebookId,
                email: email,
                name: name,
                lastName: lastName,
                gender: gender,
                avatar: avatar
              }).save().then((user,err) => {
                if(err) return res.status(400).jsonp(err);
                console.log('NEW USER CREATED', user);
                var response = {
                  success: true,
                  message: "Usuario registrado correctamente",
                  token: Service.createToken(user)
                }
                done(null, response);
              });
            } else{
              console.log("USER ALREADY Exsists");
              var response = {
                success: true,
                message: "Usuario registrado correctamente",
                token: Service.createToken(userFromFacebook)
              }
              done(null, response);
            }
          }
        )
      }
    )
  )

  function signUp(req,res){
    var facebookId = req.body.facebookId;
    if (facebookId) {
      var user = User.findOne({facebookId: facebookId}).then((userFromFacebook) =>{
        console.log("USER FROM DB FACEBOOK: ",userFromFacebook);
        if (!userFromFacebook) {
          var name = req.body.name;
          var lastName = req.body.lastName;
          var photo = req.body.photos[0].value;
          var gender = req.body.gender;
          var email = req.body.email;
          var birthday = req.body.birthday;
          var country = req.body.country;
          console.log("NEW USER");
          new User({
            facebookId: facebookId,
            email: email,
            name: name,
            lastName: lastName,
            gender: gender,
            photo: photo
          }).save().then((user,err) => {
              if(err) return res.status(400).jsonp(err);
              console.log('NEW USER CREATED', user);
              var response = {
                success: true,
                message: "Usuario registrado correctamente",
                token: Service.createToken(user)
              }
              done(null, response);
            });
          } else {
            console.log("USER ALREADY Exsists");
            var response = {
              success: true,
              message: "Usuario registrado correctamente",
              token: Service.createToken(userFromFacebook)
            }
            done(null, response);
          }
        }
      )
    }
  }

  module.exports = {
    signUp
  }
