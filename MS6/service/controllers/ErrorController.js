export function error404(req, res, next) {
  let err = new Error("Not Found");
  err.status = 404;
  next(err);
}

export function error(err, req, res, next) {
  let status = err.status || 500;
  res.status(status);

  // We don't want to output an error-stack, if there is one
  delete err.stack;
  res.json(err);
}
