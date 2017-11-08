const User = require('../models/user');

function canAccessUserInfo(req,res,next){
  var userId = req.params.id + "";
  if(!userId) res.status(404).send("Not userId selected");
  var userIdFromToken = req.user.sub;
  User.findById(userIdFromToken, function(err, userFromToken){
    if(err) next();
    User.findById(userId, function(err,user){
      if (!user) return res.status(404).send({success: false, message: "User does not exist"});
      var isAdmin = User.isAdmin(userFromToken);
      var isUser = User.isUser(userFromToken);
      if (isAdmin) {
        next();
      } else if(isUser){
        if (user._id == userIdFromToken) {
          next();
        } else{
          res.status(403).send({success: false, message: "Unthorized user, this action can be performed only by the user"});
        }
      };
    });
  });
};

module.exports = {
  canAccessUserInfo
}
