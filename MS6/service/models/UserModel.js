import mongoose from "mongoose";

const UserSchema = new mongoose.Schema({
  name: String,
  email: {
    type: String,
    required: true
  },
  password: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ["STUDENT", "ALUMNI", "INTERESSENT"],
    required: true
  },
  verified: {
    type: Boolean,
    default: false
  },
  auth_token: String
});

// Register the UserSchema as a model called "User"
mongoose.model("User", UserSchema);
