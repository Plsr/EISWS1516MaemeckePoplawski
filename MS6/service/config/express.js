import express from "express";
import logger from "morgan";
import bodyParser from "body-parser";
import expressValidator from "express-validator";

import api from "../routes/api";
import { error404, error } from "../controllers";

const app = express();

// Middleware
// ---
// HTTP Request Logger
app.use(logger("dev"));

// express-validator can validate and sanitize the request
// see https://github.com/ctavan/express-validator
app.use(expressValidator());

// Body Parser: We only need to parse application/json
app.use(bodyParser.json());


// Routes
// ---
// API starting point
app.use("/", api);


// Error handling
// ---
// 404: No handler in a route called res.end() and the function signature has
// a length of 3 (req, res, next). In this case we don't have an error, but
// our api-routes weren't satisfied, too. This means it is an 404 NOT FOUND.
// This handler will create an Error object and pass it to the next handler.
app.use(error404);

// The function signature has a length of 4: err, req, res, next.
// Since we have an error, we can deal with it here and treat errors correctly.
app.use(error);


module.exports = app;
