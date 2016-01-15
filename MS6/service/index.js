import colors from "colors";

// Expose the configuration global. This can come handy in some situations.
global.__config = require("./config/env/DEV");

// Using require instead of `import ... from ...` here, because imports will be
// executed before anything else – but we need to attach our __config first
// in the global scope.
const db = require("./config/database");

// Running on port 3000, unless there's a PORT specified
const port = process.env.PORT || 3000;

// Connect to the database before doing anything else
db()
  .then(() => {
    console.log("[✓] Database connected".green);
    return;
  })
  .then(() => {
    // If everything went alright, let's start express
    const app = require("./config/express");
    app.listen(port);
    console.log("[✓] Server listening on port %d".green, port);
    return;
  })
  .then(() => {
    // Bootstrap development data (such as test user, ...)
    // Uncomment this `require` and comment the `return` for database bootstrapping
    // require("./config/dev_bootstrap")();
    return;
  })
  .catch((e) => {
    console.log("[!] Error while starting: %s".red, e);
    console.log(e);
    if ("message" in e && e.message.match(/^(connect ECONNREFUSED)/)) {
      console.log("[i] Seems like your mongo daemon isn't running.".blue);
      console.log("    Try running `mongod` first.".blue);
    }
    process.exit(0);
  });
