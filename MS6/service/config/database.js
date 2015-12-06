import fs from "fs";
import path from "path";
import mongoose from "mongoose";
import colors from "colors";

const models = path.resolve(__dirname, "..", "models");

// Standard pattern for creating a mongoose connection
function connect() {
  let options = { server: { socketOptions: { keepAlive: 1 }}};
  return mongoose.connect(__config.db, options).connection;
}


module.exports = function db(text) {
  return new Promise((resolve, reject) => {
    console.log("[i] Bootstrapping models...".blue);

    // Bootstrap models
    fs.readdirSync(models)
      // Loop through all files in "models", but only .js files
      .filter(file => ~file.indexOf(".js"))
      // Execute each file (via require)
      .forEach(file => require(path.resolve(models, file)));

    // Now the models are availabe through `mongoose.model("Modelname")`

    console.log("[i] Connecting to database...".blue);
    // Open connection to mongodb through mongoose
    connect()
      .on("error", err => {
        reject(err);
      })
      .on("disconnect", () => {
        reject(new Error("Disconnected from mongodb"));
      })
      .once("open", () => {
        resolve();
      });
  });
}
