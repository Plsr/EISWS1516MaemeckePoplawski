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

  // If the user is authenticated and thus requesting itself,
  // it can see his email address
  let select = "";
  if ("auth_user" in req && req.auth_user._id == req.params.userid) {
    select = "+email";
  }

  User.findOne({ _id: req.params.userid })
    .select(select)
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

// Create a user authentication by a user email and password
export function userAuthCreate(req, res, next) {

  // Validate request
  req.checkBody("email")
    .notEmpty().isEmail().withMessage("Email is required as a valid email address");
  req.checkBody("password", "Password is required")
    .notEmpty();
  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  // Find a user by its ID and check if passwords match.
  // If passwords match, save an auth_token and send it back
  User.findOne({ email: req.body.email })
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
        // Continue with the next callback
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


// Call userAuth, but only when the user want's to authenticate.
// This can be used to display more or less data depending on the users
// authorization
export function userOptionalAuth(req, res, next) {
  if (req.headers["x-auth-user"] || req.headers["x-auth-token"]) {
    return userAuth(req, res, next);
  }

  return next();
}


// Update some data of a user, e.g. email, password and name
export function userUpdate(req, res, next) {
  if (req.params.userid != req.auth_user._id) {
    return next(new HTTPError(403, "Can't update another user. You can only update yourself."));
  }

  // Validate request
  req.checkBody("email").optional()
    .isEmail().withMessage("Email has to be a valid email address");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  req.sanitizeBody("name").trim();

  let body = req.body,
    user = req.auth_user;

  // Override new transmitted data, fall back to the current data for
  // non-transmitted data
  user.name = body.name || user.name;
  user.password = body.password || user.password;
  user.email = body.email || user.email;

  // Save the user and output it
  user.save()
    .then(
      savedUser => {
        return User.findOne({ _id: user._id }).select("+email")
          .then(
            outputUser => (outputUser),
            err => { throw err; }
          );
      },
      err => { throw err; }
    )
    .then(
      updatedUser => {
        return res.json(updatedUser);
      },
      err => {
        return next(err);
      }
    );
}


export function userCreate(req, res, next) {

  // Validate request
  req.checkBody("name")
    .notEmpty().withMessage("Name is required");
  req.checkBody("password")
    .notEmpty().withMessage("Password is required");
  req.checkBody("email")
    .notEmpty().withMessage("Password is required")
    .isEmail().withMessage("Email has to be a valid email address");
  req.checkBody("type")
    .notEmpty().withMessage("Type is required")
    .isIn(["INTERESSENT", "STUDENT", "ALUMNI"]).withMessage("Type has to be INTERESSENT, STUDENT or ALUMNI.");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  req.sanitizeBody("name").trim();

  let user = new User({
    name: req.body.name,
    email: req.body.email,
    type: req.body.type,
    password: req.body.password
  });

  // Save user, get the filtered document from the databse and send it back
  // to the user.
  user.save()
    .then(
      savedUser => {
        return User.findOne({ _id: savedUser._id }).select("+email")
          .then(
            newUser => (newUser),
            err => { throw err; }
          );
      },
      err => { throw err; }
    )
    .then(
      newUser => {
        res.status(201); // 201 Created
        return res.json(newUser);
      },
      err => {
        return next(err);
      }
    );
}


// Delete a user
export function userDelete(req, res, next) {

  // Validate request
  req.checkBody("userid", "Malformed userid")
    .notEmpty().withMessage("userid is required")
    .isMongoId();
  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  if (req.params.userid != req.auth_user._id) {
    return next(new HTTPError(403, "Can't delete another user. You can only delete yourself."));
  }

  User.findOneAndRemove({ _id: req.auth_user._id })
    .exec()
    .then(
      deletedDoc => {
        return res.status(204).end(); // 204 No Content
      },
      err => {
        return next(err);
      }
    );

}
