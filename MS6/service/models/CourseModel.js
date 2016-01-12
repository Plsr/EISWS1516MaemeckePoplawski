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

// Virtuals for HATEOAS

CourseSchema.virtual("link.self").get(function () {
  return `${__config.host}/courses/${this._id}`;
});

CourseSchema.virtual("link.list").get(function () {
  return `${__config.host}/courses/`;
});

CourseSchema.set("toJSON", {
  virtuals: true,
  versionKey: false, // auto select("-__v")
  transform: function (doc, ret, options) {
    delete ret.id; // delete virtual `id`, use `_id`
  }
});
CourseSchema.set("toObject", {
  virtuals: true,
  versionKey: false,
  transform: function (doc, ret, options) {
    delete ret.id; // delete virtual `id`, use `_id`
  }
});

// Register the CourseSchema as a model called "Course"
mongoose.model("Course", CourseSchema);
