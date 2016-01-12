var colors = require("colors");
console.log("[i] Transpiling ES2015 files...".blue);

// Require Hook for ESNext Syntax
// See https://babeljs.io/docs/usage/require/
require("babel-register");

// Run index.js
require("./index");

// We could also directly run `babel-node index.js` or just `node index.js` with
// a ES2015/ESNext compatible installed version of Node.js.
