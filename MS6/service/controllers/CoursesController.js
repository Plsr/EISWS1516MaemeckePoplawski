import mongoose from "mongoose";
import async from "async";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Course = mongoose.model("Course");
const Entry = mongoose.model("Entry");

export function courseList(req, res, next) {
  // Find all courses, get their entries with type "ERFAHRUNG", calculate
  // their average recommendation and inject it into the returned course object
  Course.find()
    .select("-entries")
    .populate({
      path: "university",
      select: "-courses"
    })
    .exec()
    .then(
      courses => {
        // New array for results
        let _courses = new Array(courses.length);
        return async.forEachOf(courses, (course, key, loop) => {
          // Making it mutable
          course = course.toObject();

          let positive = 0;
          let total = 0;

          // Find all entries with type "ERFAHRUNG" of this particular course
          return Entry.find({ course: course._id, type: "ERFAHRUNG" })
            .select("-subentries")
            .exec()
            .then(
              entries => {
                entries.forEach(entry => {
                  total++;
                  if (entry.recommendation) positive++;
                });
                course.recommendation = Math.round((positive / total) * 100) / 100;
                if (entries.length < 1) course.recommendation = -1;
                _courses[key] = course;
                loop();
              },
              err => { loop(err); }
            );
        }, (err) => {
          if (err) return next(err);
          return res.json({ courses: _courses });
        });
      },
      err => (next(err))
    );
}

// List a single Course with its entries
export function courseGet(req, res, next) {
  // Amout of results per page
  let limitPerPage = 10;

  // Validate request
  req.checkParams("courseid")
    .notEmpty().isMongoId().withMessage("Course is required and has to be an ObjectId");
  req.checkQuery("page")
    .optional().isInt({ min: 1 }).withMessage("Page parameter has to be an Integer greater than 0");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  // Get the current page from params
  req.sanitizeQuery("page").toInt();
  let page = req.query.page || 1;

  Course.findOne({ _id: req.params.courseid })
    .select("-entries")
    .populate({ // populate university into course
      path: "university",
      select: "-courses"
    })
    .exec()
    .then(
      course => {
        if(!course)
          throw new HTTPError(404, `Course with ID ${req.params.courseid} not found`);

        return course;
      }
    )
    .then(course => {
      let skip = limitPerPage * (page-1);

      // Put entries into course result
      Entry.find({ parententry: null, course: course._id })
        .sort("-createdAt")
        .limit(limitPerPage)
        .skip(skip)
        .select("-subentries -course")
        .populate("user")
        .exec((err, entries) => {
          if (err) { throw err; }
          // `course` is immutable, so we need to duplicate it in order to
          // manipulate it
          let _course = course.toObject();
          _course.entries = entries;

          // Now we can add pagination links to our result. We need to have the
          // total amount for this
          Entry.count({ parententry: null, course: course._id }, (err, count) => {
            _course.link = {};
            if (page > 1)
              _course.link.prev = `${__config.host}/courses/${course._id}?page=${page-1}`;
            if (count > skip + limitPerPage)
              _course.link.next = `${__config.host}/courses/${course._id}?page=${page+1}`;

            return res.json(_course);
          });
        });
    })
    .then(
      undefined,
      err => (next(err))
    )
}
