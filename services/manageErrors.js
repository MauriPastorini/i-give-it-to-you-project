module.exports = function manageErrorCodes(error){
  var errors = [];
  if (error.name == "ValidationError") {
    console.log("Entre a validation error");
    for (field in error.errors) {
      console.log("Entre a field");
      console.log(error.errors[field]);
      if (error.errors[field].kind == "required" && error.errors[field].path == "email")  {
        errors.push({
          code: 1,
          message: error.errors[field].message
        })
      };
      if (error.errors[field].kind == "required" && error.errors[field].path == "name")  {
        errors.push({
          code: 2,
          message: error.errors[field].message
        })
      };
    }
  }
  return {
    errors: errors
  };
}
