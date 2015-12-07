import mongoose from "mongoose";

const VerificationSchema = new mongoose.Schema({
  code: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User"
  }
});

// Register the VerificationSchema as a model called "Verification"
mongoose.model("Verification", VerificationSchema);
