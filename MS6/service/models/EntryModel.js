import mongoose from "mongoose";

const EntrySchema = new mongoose.Schema({
  title: String,
  text: String,
  createdAt: Date,
  type: {
    type: String,
    enum: ["ERFAHRUNG", "ALUMNIBERICHT", "ANDERS"],
    required: true
  },
	recommendation: Boolean,
	user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User"
  },
  course: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Course"
  },
  subentries: [
    {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Entry"
    }
  ],
	parententry: {
		type: mongoose.Schema.Types.ObjectId,
		ref: "Entry"
	}
});

// Set date automatic when creating new Entry
EntrySchema.pre("save", function(done) {
  this.createdAt = new Date();
  done();
});

// Virtuals for HATEOAS

EntrySchema.virtual("link.self").get(function () {
  return `${__config.host}/entries/${this._id}`;
});

EntrySchema.virtual("link.list").get(function () {
  return `${__config.host}/courses/${this.course}`;
});

EntrySchema.set("toJSON", {
  virtuals: true,
  versionKey: false, // auto select("-__v")
  transform: function (doc, ret, options) {
    delete ret.id; // delete virtual `id`, use `_id`
  }
});
EntrySchema.set("toObject", {
  virtuals: true,
  versionKey: false,
  transform: function (doc, ret, options) {
    delete ret.id; // delete virtual `id`, use `_id`
  }
});

// Register the EntrySchema as a model called "Entry"
mongoose.model("Entry", EntrySchema);
