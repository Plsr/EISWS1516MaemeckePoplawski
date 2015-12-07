import mongoose from "mongoose";
import colors from "colors";
import randomstring from "randomstring";

const User = mongoose.model("User");

// A pre-created user in our database
const defaultUser = new User({
  email: "test@test.de",
  password: "test",
  type: "STUDENT",
  name: "Max Mustermann",
  auth_token: randomstring.generate()
});

module.exports = () => {
  User
    // Check if default user exists
    .findOne({ email: defaultUser.email })
    .select("+email +password +auth_token")
    .exec()
    .then((dummyUser) => {
      if (dummyUser) return dummyUser;

      // Create default user, if he doesn't exist
      return defaultUser.save()
        .then(
          user => {
            console.log("[✓] Created dummy user".green);
            return user;
          },
          err => { throw err; }
        )
    })
    .then(
      userInDb => {
        console.log("[i] Dummy user:\n%s".blue, userInDb);
      },
      err => {
        console.log("[!] Error while saving dummy user".red);
        console.log(err);
      }
    );

  return;
}