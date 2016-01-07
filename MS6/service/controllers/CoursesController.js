import mongoose from "mongoose";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Course = mongoose.model("Course");

export function courseList(req, res, next) {
  // Find all courses and list them without their entries
  Course.find()
    .select("-__v -entries")
    .populate({
      path: "university",
      select: "-__v -courses"
    })
    .exec()
    .then(
      courses => {
        // duplicate `courses` to make it mutable
        let _courses = JSON.parse(JSON.stringify(courses));
        _courses.forEach(course => {
          // add ref-url to course
          course.ref = `${req.protocol}://${req.get("host")}/courses/${course._id}`
        });

        return res.json({ courses: _courses });
      },
      err => (next(err))
    );
}

export function courseGet(req, res, next) {
  // List a single Course with its entries

  // Validate request
  req.checkParams("courseid")
    .notEmpty().isMongoId().withMessage("Course is required and has to be an ObjectId");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  Course.findOne({ _id: req.params.courseid })
    .select("-__v")
    .populate({ // populate university into course
      path: "university",
      select: "-__v -courses"
    })
    .populate({
      path: "entries", // populate entries into course
      select: "-text -course -__v -subentries",
      populate: { // A sub-document populate.     // (•_•)
        path: "user",                             // ( •_•)>⌐■-■
        select: "-__v"                            // (⌐■_■)
      }                                           // YEEEEAAAHHH
    })
    .exec()
    .then(
      course => {
        if(!course)
          throw new HTTPError(404, `Course with ID ${req.params.courseid} not found`);

        // duplicate `course` to make it mutable
        let _course = JSON.parse(JSON.stringify(course));
        _course.entries.forEach(entry => {
          // make ref-url to entry
          entry.ref = `${req.protocol}://${req.get("host")}/entries/${entry._id}`
          entry.user.ref = `${req.protocol}://${req.get("host")}/users/${entry.user._id}`
        });
        return res.json(_course);
      }
    )
    .then(
      undefined,
      err => (next(err))
    )
}
