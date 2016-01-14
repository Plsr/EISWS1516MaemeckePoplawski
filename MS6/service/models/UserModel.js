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
  },
  device_id: {
    type: String,
    select: false,
    default: ""
  }
});

// Virtuals for HATEOAS

UserSchema.virtual("link.self").get(function () {
  return `${__config.host}/users/${this._id}`;
});

UserSchema.virtual("id").get(function () {
  return undefined;
});

UserSchema.set("toJSON", {
  virtuals: true,
  versionKey: false, // auto select("-__v")
  transform: function (doc, ret, options) {
    delete ret.id; // delete virtual `id`, use `_id`
  }
});
UserSchema.set("toObject", {
  virtuals: true,
  versionKey: false,
  transform: function (doc, ret, options) {
    delete ret.id; // delete virtual `id`, use `_id`
  }
});

// Register the UserSchema as a model called "User"
mongoose.model("User", UserSchema);
