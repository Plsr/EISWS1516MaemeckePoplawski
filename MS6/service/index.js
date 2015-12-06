import colors from "colors";

// Expose the configuration global, that can come handy
import config from "./config/env/DEV";
global.__config = config;

// Using require instead of `import ... from ...` here, because imports will be
// executed before everything else – but we need to attach our __config first
// in the global scope.
const db = require("./config/database");

const port = process.env.PORT || 3000;

// Connect to the database
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
  .then(
    // Bootstrap development data (such as test user, ...)
    require("./config/dev_bootstrap")
  )
  .catch((e) => {
    console.log("[!] Error while starting: %s".red, e);
    console.log(e);
    if ("message" in e && e.message.match(/^(connect ECONNREFUSED)/)) {
      console.log("[i] Seems like your mongo daemon isn't running.".blue);
      console.log("    Are you sure there's a `mongod` running on your machine?.".blue);
    }
    process.exit(0);
  });
