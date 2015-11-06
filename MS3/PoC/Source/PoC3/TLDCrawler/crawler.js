var get = require('simple-get');
var cheerio = require('cheerio');

exports.getPageContent = function (url, cb) {
  get.concat(url, function (err, data, res) {
    if (err) throw err
    cb(data.toString());
  });
}

exports.getPagePart = function (selector, content, cb) {
  var $ = cheerio.load(content, { normalizeWhitespace: false, xmlMode: false, decodeEntities: true });
  var output = [];

  $(selector).each(function (index, element) {
    output.push($(element).html());
  });
  cb(output);
}
