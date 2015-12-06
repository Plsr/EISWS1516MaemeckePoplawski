import mongoose from "mongoose";
import ValidationError from "../helpers/ValidationError";

const User = mongoose.model("User");

export function userGet(req, res, next) {

  // Validate request
  req.checkParams("userid", "Malformed userid")
    .notEmpty().withMessage("userid is required")
    .isMongoId();
  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  User.findOne({ _id: req.params.userid })
    .exec()
    .then(
      // Resolve callback
      user => {
        // User not found
        if (!user) return next();

        // User found => send it back.
        res.json(user);
      },

      // Reject callback
      err => {
        // User not found (because of invalid ObjectID or other error)
        return next();
      }
    );
}
