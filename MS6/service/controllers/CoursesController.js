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
