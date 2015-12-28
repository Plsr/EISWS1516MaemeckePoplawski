import mongoose from "mongoose";
import randomstring from "randomstring";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Entry = mongoose.model("Entry");

export function entryCreate(req, res, next) {
  // Validate request
  req.checkBody("title")
    .notEmpty().withMessage("Title is required");
  req.checkBody("text")
    .notEmpty().withMessage("Text is required");

    return res.end();



}
