var get = require('simple-get');
var cheerio = require('cheerio');

// Get Contents of a Page by a url
exports.getPageContent = function (url, cb) {
  get.concat(url, function (err, data, res) {
    if (err) throw err
    cb(data.toString());
  });
}

// Get a part of a html file by a css selector
exports.getPagePart = function (selector, content, cb) {
  var $ = cheerio.load(content, { normalizeWhitespace: false, xmlMode: false, decodeEntities: true });
  var output = [];

  $(selector).each(function (index, element) {
    output.push($(element).html());
  });
  cb(output);
}
