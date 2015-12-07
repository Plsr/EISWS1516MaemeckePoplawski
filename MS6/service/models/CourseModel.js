import mongoose from "mongoose";

const CourseSchema = new mongoose.Schema({
  name: String,
  university: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "University"
  },
  entries: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Entry"
    }
  ]
});

// Register the CourseSchema as a model called "Course"
mongoose.model("Course", CourseSchema);
