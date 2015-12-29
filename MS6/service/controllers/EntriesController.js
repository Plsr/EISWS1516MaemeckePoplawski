import mongoose from "mongoose";
import randomstring from "randomstring";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Entry = mongoose.model("Entry");

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
  req.checkBody("user")
    .notEmpty().withMessage("User is required")
    .isMongoId();
  req.checkBody("course")
    .notEmpty().withMessage("Course is required")
    .isMongoId();
  req.checkBody("parententry")
    .optional().isMongoId();

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  return res.end();



}
