import mongoose from "mongoose";

const EntrySchema = new mongoose.Schema({
  title: String,
  text: String,
  type: {
    type: String,
    enum: ["ERFAHRUNG", "ALUMNIBERICHT", "ANDERS"],
    required: true
  },
	poscount: {
		type: Number
	}
	negcount {
		type: Number
	}
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
  ]
	parententry: {
		type: mongoose.Schema.Types.ObjectId,
		ref: "Entry"		
	}
});

// Register the EntrySchema as a model called "Entry"
mongoose.model("Entry", EntrySchema);
