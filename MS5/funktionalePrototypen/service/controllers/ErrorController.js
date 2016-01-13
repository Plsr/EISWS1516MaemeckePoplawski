import { HTTPError } from "../helpers/Errors";

// Express 404 error handler, passes a HTTP 404 to final error handler
export function error404(req, res, next) {
  let err = new HTTPError(404, "Not Found");
  next(err);
}

// Error handler for final express callback-chain
export function error(err, req, res, next) {
  let status = err.status || 500;
  res.status(status);

  // We don't want to output an error-stack
  delete err.stack;
  res.json(err);
}
