// Custom Error class for HTTP errors
export class HTTPError extends Error {
  constructor(error) {
    super();
    let _error = {};
    // Be able to
    if (arguments.length === 2 &&
      typeof arguments[1] === "string" &&
      typeof arguments[0] === "number") {

      _error.status = arguments[0];
      _error.msg = arguments[1];
    } else {
      _error = error;
    }
    this.status = _error.status || 500;
    this.message = _error.msg;
  }
}

// Custom Error class for failed request validations
export class ValidationError extends Error {
  constructor(errors) {
    super();
    this.status = 400;
    this.message = "Request is not valid. See details for more informations.";

    // Print error details
    this.details = {};
    errors.forEach(error => {
      this.details[error.param] = {
        parameter: error.param,
        reason: error.msg,
        received: error.value
      }
    });
  }
}
