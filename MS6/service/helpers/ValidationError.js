// Custom Error class for failed request validations
export default class ValidationError extends Error {
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
