import mongoose from "mongoose";
import colors from "colors";
import randomstring from "randomstring";

const User = mongoose.model("User");
const Course = mongoose.model("Course");

// A pre-created user in our database
const defaultUser = new User({
  email: "test@test.de",
  password: "test",
  type: "STUDENT",
  name: "Max Mustermann",
  auth_token: randomstring.generate()
});

// A pre-created course in our database
const defaultCourse = new Course({
  name: "Default Course"
  // TODO: University is left blank intentionally at this point,
  //       fill later.
})

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

  Course
      .findOne({ name: defaultCourse.name })
      .select("+name")
      .exec()
      .then((dummyCourse) => {
        if (dummyCourse) return dummyCourse;

        return defaultCourse.save()
          .then(
            course => {
              console.log("[✓] Created dummy course".green);
              return course;
            },
            err => { throw err; }
          )
      })
      .then(
        courseInDb => {
          console.log("[i] Dummy course:\n%s".blue, courseInDb);
        },
        err => {
          console.log("[!] Error while saving dummy course".red);
          console.log(err);
        }
      );

  return;
}
