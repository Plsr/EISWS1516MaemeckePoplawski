import mongoose from "mongoose";
import randomstring from "randomstring";
import { ValidationError, HTTPError } from "../helpers/Errors";

const User = mongoose.model("User");

// Get a user by its id
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
        if (!user)
          throw new HTTPError(404, `User with ID ${req.params.userid} not found`);

        // User found => send it back.
        res.json(user);
      },

      // Reject callback
      err => { throw err; }
    )

    // Just for error handling in promise
    .then(
      undefined,
      err => {
        return next(err);
      }
    )
}

// Create a user authentication by a user ID and password
export function userAuthCreate(req, res, next) {

  // Validate request
  req.checkBody("userid", "Malformed userid")
    .notEmpty().withMessage("userid is required")
    .isMongoId();
  req.checkBody("password", "Password is required")
    .notEmpty();
  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  // Find a user by its ID and check if passwords match.
  // If passwords match, save an auth_token and send it back
  User.findOne({ _id: req.body.userid })
    .select("+password")
    .exec()
    .then(
      user => {
        if (!user) throw new HTTPError(403, "User and password mismatch");

        // If passwords match we can safely continue
        if (user.password === req.body.password) {
          return user;
        }

        else {
          throw new HTTPError(403, "User and password mismatch");
        }
      },
      err => { throw err; }
    )
    .then(user => {
      // Generate a new auth_token
      user.auth_token = randomstring.generate();
      return user.save()
        .then(
          savedUser => (savedUser),
          err => { throw err }
        )
    })
    .then(
      user => {
        // Send auth_token back
        return res.json({ auth_token: user.auth_token });
      },
      err => {
        return next(err);
      }
    );
}


// Authenticate a user based on its ID and token
export function userAuth(req, res, next) {
  req.checkHeaders("x-auth-user", "Invalid x-auth-user userid")
    .notEmpty().withMessage("The x-auth-user Header is required for this request")
    .isMongoId();
  req.checkHeaders("x-auth-token", "Invalid x-auth-token")
    .notEmpty().withMessage("The x-auth-token Header is required for this request");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  let userid = req.headers["x-auth-user"];
  let token = req.headers["x-auth-token"];

  // Find a user by its id and auth_token,
  // if we found a user, we will
  //   1. save the user in our req and
  //   2. call the next handler
  User.findOne({ _id: userid, auth_token: token })
    .select("+password +email +auth_token")
    .exec()
    .then(
      user => {
        if (!user) throw new HTTPError(403, "Invalid authentication");

        req.auth_user = user;
        // Conitnue with the next callback
        return next();
      },
      err => { throw err; }
    )
    .then(
      undefined,
      err => {
        // If there was an error (server or client), we'll continue with the
        // error handling
        return next(err);
      }
    );
}
