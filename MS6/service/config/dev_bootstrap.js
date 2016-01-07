import mongoose from "mongoose";
import colors from "colors";
import randomstring from "randomstring";

const User = mongoose.model("User");
const Course = mongoose.model("Course");
const Entry = mongoose.model("Entry");
const University = mongoose.model("University");

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
  // University will be filled in while creating
});

// A pre-created university in our daabase
const defaultUniversity = new University({
  name: "TH Köln",
  position: {
    lat: 51.023474,
    long: 7.564873
  }
  // Courses will be filled in while creating
});

// A pre-created entry in our database
const defaultEntry = new Entry({
  title: "Default Entry",
  text: "Default body text. kthxbye.",
  type: "ANDERS"
  // All other values are left blank intentionally since they depend
  // on other models and are not essential for testing right now.
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
            defaultUser._id = user._id;
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

  University
    .findOne({ name: defaultUniversity.name })
    .exec()
    .then((dummyUni) => {
      if (dummyUni) return dummyUni;

      return defaultUniversity.save()
        .then(
          uni => {
            console.log("[✓] Created dummy university".green);
            defaultUniversity._id = uni._id;
            return uni;
          },
          err => { throw err; }
        )
    })
    .then(
      courseInDb => {
        console.log("[i] Dummy university:\n%s".blue, courseInDb);
      },
      err => {
        console.log("[!] Error while saving dummy uni".red);
        console.log(err);
      }
    );

  Course
      .findOne({ name: defaultCourse.name })
      .exec()
      .then((dummyCourse) => {
        if (dummyCourse) return dummyCourse;

        defaultCourse.university = defaultUniversity._id;
        return defaultCourse.save()
          .then(
            course => {
              console.log("[✓] Created dummy course".green);
              // Push course in university
              defaultUniversity.courses.push(course);
              defaultUniversity.save();
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

  Entry
    .findOne({ title: defaultEntry.title })
    .select()
    .exec()
    .then((dummyEntry) => {
      if(dummyEntry) return dummyEntry;

      defaultEntry.user = defaultUser._id;

      return defaultEntry.save()
      .then(
        entry => {
          console.log("[✓] Created dummy entry".green);
          // push entry in course
          defaultCourse.entries.push(entry);
          defaultCourse.save();
          return entry;
        },
        err => { throw err; }
      )
    })
    .then (
      entryInDb => {
        console.log("[i] Dummy entry:\n%s".blue, entryInDb);
      },
      err => {
        console.log("[!] Error while saving dummy entry".red);
        console.log(err);
      }
    );

  return;
}
