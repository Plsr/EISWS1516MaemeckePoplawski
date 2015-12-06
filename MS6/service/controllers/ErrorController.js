export function error404(req, res, next) {
  let err = new Error("Not Found");
  err.status = 404;
  next(err);
}

export function error(err, req, res, next) {
  let status = err.status || 500;
  res.status(status);
  res.json({ "status": status, "error": err });
}
