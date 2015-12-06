import mongoose from "mongoose";

const User = mongoose.model("User");

export function userGet(req, res, next) {
  User.findOne({ _id: req.params.id })
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
