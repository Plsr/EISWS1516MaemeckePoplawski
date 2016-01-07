import mongoose from "mongoose";
import randomstring from "randomstring";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Entry = mongoose.model("Entry");
const Course = mongoose.model("Course");

export function entryCreate(req, res, next) {
  // Allowed post types
  var postTypes = ["ERFAHRUNG", "ALUMNIBERICHT", "ANDERS"];

  // Validate request
  req.checkBody("title")
    .notEmpty().withMessage("Title is required");
  req.checkBody("text")
    .notEmpty().withMessage("Text is required");
  req.checkBody("type")
    .notEmpty().withMessage("Post Type is required")
    .isIn(postTypes).withMessage("Post Type not valid");
  req.checkBody("course")
    .notEmpty().withMessage("Course is required")
    .isMongoId();
  req.checkBody("parententry")
    .optional().isMongoId();

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  req.sanitizeBody("title").trim();

  // Check if there is a parententry given
  // if not, set it to null to make the clients life easier
  var parent = req.body.parententry;
  if(parent == undefined)
    parent = null;

  let entry = new Entry({
    title: req.body.title,
    text: req.body.text,
    type: req.body.type,
    user: req.auth_user._id,
    course: req.body.course,
    parententry: parent // Is optional, what happens if it's not there? undefined?
  });

  // Save entry, get the filtered document from the databse and send it back
  // to the user.
  entry.save()
    .then(
      savedEntry => {
        return Entry.findOne({ _id: savedEntry._id }).exec()
          .then(
            newEntry => (newEntry),
            err => { throw err; }
          );
      },
      err => { throw err; }
    )
    .then(
      newEntry => {
        // If there is no parentEntry, push the EntryID into the course entries
        if (!parent) {
          return Course.findOne({ _id: req.body.course })
            .exec()
            .then(
              _course => {
                _course.entries.push(newEntry);
                _course.save();
                return newEntry;
              },
              err => { throw err; }
            )
        }

        // If there is a parent, push the EntryID into the parent entry
        return Entry.findOne({ _id: parent })
          .exec()
          .then(
            _parent => {
              _parent.subentries.push(newEntry);
              _parent.save();
              return newEntry;
            },
            err => { throw err; }
          )
      }
    )
    .then(
      newEntry => {
        res.status(201); // 201 Created
        return res.json(newEntry);
      },
      err => {
        return next(err);
      }
    );
}

export function entryGet(req, res, next) {
  // Validate request
  req.checkParams("entryid")
    .notEmpty().withMessage("Entry ID is required")
    .isMongoId();

  let errors = req.validationErrors();
  if (errors)  return next(new ValidationError(errors));

  // Find entry by given id
  Entry.findOne({ _id: req.params.entryid })
    .populate('user')
    .select()
    .exec()
    .then(
      entry => {
        // Entry not found
        if(!entry)
          throw new HTTPError(404, `Entry with ID ${req.params.entryid} not found`);

        // Return entry
        res.json(entry);
      },
      err => {
        throw err;
      }
    )
    // Just for error handling in promise
    .then (
      undefined,
      err => {
        return next(err);
      }
    )
}
