class GeneralError extends Error {
  constructor(message) {
    super();
    this.message = message;
  }

  getCode() {
    if (this instanceof BAD_REQUEST) return 400;
    if (this instanceof NotFound) return 404;
    if (this instanceof Unauthorized) return 401;
    if (this instanceof Conflict) return 409;
    return 500;
  }
}

class BAD_REQUEST extends GeneralError {}
class NotFound extends GeneralError {}
class Unauthorized extends GeneralError {}
class Conflict extends GeneralError {}

module.exports = {
  GeneralError,
  BAD_REQUEST,
  NotFound,
  Unauthorized,
  Conflict,
};
