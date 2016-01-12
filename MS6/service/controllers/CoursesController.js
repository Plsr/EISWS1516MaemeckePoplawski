import mongoose from "mongoose";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Course = mongoose.model("Course");

export function courseList(req, res, next) {
  // Find all courses and list them without their entries
  Course.find()
    .select("-entries")
    .populate({
      path: "university",
      select: "-courses"
    })
    .exec()
    .then(
      courses => (res.json({ courses: courses })),
      err => (next(err))
    );
}

// List a single Course with its entries
export function courseGet(req, res, next) {
  // Validate request
  req.checkParams("courseid")
    .notEmpty().isMongoId().withMessage("Course is required and has to be an ObjectId");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  Course.findOne({ _id: req.params.courseid })
    .populate({ // populate university into course
      path: "university",
      select: "-courses"
    })
    .populate({
      path: "entries", // populate entries into course
      select: "-text -subentries",
      populate: { // populate user in course      // (•_•)
        path: "user",                             // ( •_•)>⌐■-■
      }                                           // (⌐■_■)
    })                                            // YEEEEAAAHHH
    .exec()
    .then(
      course => {
        if(!course)
          throw new HTTPError(404, `Course with ID ${req.params.courseid} not found`);

        // We need to delete "course" from each entry since it's not necessary
        // here, but using select("-course") would result in course being
        // ignored for HATEOAS references and returning undefined.
        // `course` is immutable, so we need to duplicate it
        let _course = course.toObject();
        _course.entries.forEach(entry => {
          delete entry.course;
        });

        return res.json(_course);
      }
    )
    .then(
      undefined,
      err => (next(err))
    )
}
