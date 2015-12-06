import mongoose from "mongoose";
import colors from "colors";

const User = mongoose.model("User");

// A pre-created user in our database
const defaultUser = new User({
  email: "test@test.de",
  password: "test",
  type: "STUDENT",
  name: "Max Mustermann"
});

module.exports = () => {
  User
    // Check if default user exists
    .findOne({ email: defaultUser.email })
    .exec()
    .then((dummyUser) => {
      if (dummyUser) return dummyUser;

      // Create default user, if he doesn't exist
      return defaultUser.save()
        .then(() => { console.log("[✓] Created dummy user".green); })
        .then(() => (defaultUser));
    })
    .then((userInDb) => {
      console.log("[i] Dummy user:\n%s".blue, userInDb);
    });

  return;
}
