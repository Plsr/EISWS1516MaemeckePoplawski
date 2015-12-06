import mongoose from "mongoose";

// TODO: email should be unique and potential erros in userCreate and userUpdate
// should be catched

const UserSchema = new mongoose.Schema({
  name: String,
  email: {
    type: String,
    required: true,
    select: false // Prevents password from accidentially being shown.
                  // You can add it with User.find().select("+password")
  },
  password: {
    type: String,
    required: true,
    select: false
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
  auth_token: {
    type: String,
    select: false
  }
});

// Register the UserSchema as a model called "User"
mongoose.model("User", UserSchema);
