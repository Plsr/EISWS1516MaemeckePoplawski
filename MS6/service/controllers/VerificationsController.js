import mongoose from "mongoose";
import nodemailer from "nodemailer";
import randomstring from "randomstring";
import { ValidationError, HTTPError } from "../helpers/Errors";

const Verification = mongoose.model("Verification");
const User = mongoose.model("User");

// Generate a new verification and send mail with link to given email address
export function verificationCreate(req, res, next) {

  // Validate request
  req.checkBody("email")
    .notEmpty().isEmail().withMessage("A valid email is required");

  let errors = req.validationErrors();
  if (errors) return next(new ValidationError(errors));

  // Check if user is already verified
  if (req.auth_user.verified) {
    return next(new HTTPError(403,
      `User with ID ${req.auth_user._id} is already verified`));
  }

  // Create SMTP Connection
  let sendfrom = {
    email: "hochschulify%40gmail.com",
    pass: "EUDv34FA"
  };
  let transporter = nodemailer
    .createTransport(`smtps://${sendfrom.email}:${sendfrom.pass}@smtp.gmail.com`);

  // Mail which will be send to given email address
  let code = randomstring.generate();
  let mail = {
    from: "Hochschulify <hochschulify@gmail.com>",
    to: `${req.auth_user.name} <${req.body.email}>`,
    subject: "Deine Hochschulify Verifikation",
    html: `Hallo ${req.auth_user.name},<br>
  öffne den nachfolgenden Link auf deinem Smartphone, um dich bei Hochschulify verifizieren zu lassen.<br><br>
  <a href="http://hochschulify.rfunk.de/${code}">Jetzt verifizieren</a><br><br>
  Sollte der Link nicht funktionieren, nutze diesen: http://hochschulify.rfunk.de/${code}`
  };

  let verification = new Verification({
    code: code,
    email: req.body.email,
    user: req.auth_user._id
  });

  // First, check if there is already a verification for the user. If there
  // is one, delete it and create a new one. If not, it will simply create
  // a verification.
  Verification.findOneAndRemove({ user: req.auth_user._id})
    .exec()
    .then(
      ongoingVeri => {
        return verification.save()
      }
    )
    .then(
      // transporter.sendMail() will return a Promise
      savedVeri => (transporter.sendMail(mail))
    )
    .then(
      mailsent => {
        return res.status(201).json({ // Status 201 CREATED
          status: "success",
          mailto: req.body.email
        });
      },
      err => (next(err))
    );

}

// Check if the verification is valid and verify the user by updating him
// as verified.
export function verificationGet(req, res, next) {

  Verification.findOne({ code: req.params.code })
    .exec()
    .then(
      veri => {
        if (!veri) throw new HTTPError(404, "Verification not found");
        if (veri.user + "" !== req.auth_user._id + "") {
          throw new HTTPError(403,
            "Verification not valid. It's not your verification.");
        }

        // Since everything is OK here, we can delete the verification and
        // move on.
        return Verification.findOne({ _id: veri._id }).remove().exec();
      }
    )
    .then(
      // Update the user as verified
      veri => {
        req.auth_user.verified = true;
        return User.findOneAndUpdate(
          { _id: req.auth_user._id },
          { $set: { verified: true, type: "STUDENT" } },
          { new: true }
        );
      }
    )
    .then(
      user => (res.json(user)),
      err => (next(err))
    );
}
