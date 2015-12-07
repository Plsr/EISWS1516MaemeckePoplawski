import mongoose from "mongoose";

const UniversitySchema = new mongoose.Schema({
  name: String,
  position: {
    lat: {
      type: Number,
      required: true
    },
    long: {
      type: Number,
      required: true
    }
  },
  courses: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Course"
    }
  ]
});

// Register the UniversitySchema as a model called "University"
mongoose.model("University", UniversitySchema);
