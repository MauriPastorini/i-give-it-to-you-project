const express = require('express');
const router = express.Router();

const static_routes = require('./static_routes');
const category_routes = require('./category_routes');
const product_routes = require('./product_routes');
const user_routes = require('./user_routes');
const review_routes = require('./review_routes');
const auth_routes = require('./auth_routes');

static_routes.injectRoutes(router);
category_routes.injectRoutes(router);
product_routes.injectRoutes(router);
user_routes.injectRoutes(router);
review_routes.injectRoutes(router);
auth_routes.injectRoutes(router);

// ******
// DELETE LATER: Example for geolocation near
const Ninja = require('../models/ninja');
router.get('/ninjas',function(req,res,next){
    Ninja.geoNear({
      type:'Point', coordinates:[parseFloat(req.query.lng), parseFloat(req.query.lat)]
    },{
      maxDistance:100000,spherical:true
    }).then(function(ninjas){
      res.send(ninjas);
    });
});
// ******

module.exports = router;
