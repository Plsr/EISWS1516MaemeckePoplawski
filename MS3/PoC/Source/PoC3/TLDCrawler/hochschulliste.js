var get = require('./crawler').getPageContent;
var extract = require('./crawler').getPagePart;
var $ = require('cheerio');


var hochschullisteURL = "https://de.wikipedia.org/wiki/Liste_der_Hochschulen_in_Deutschland";
var hochschullisteSelector = "table";

var wikiHochschulen = [];

module.exports = function(cb) {
  get(hochschullisteURL, function (page) {
    extract(hochschullisteSelector, page, function (pagePart) {
      extract('tr', pagePart[0], function (trs) {
        trs.forEach(function (tr, index) {
          extract('td', tr, function (td) {
            wikiHochschulen.push($('a', td[0]).attr('href'));
          });
        });
        wikiHochschulen.shift();
        cb(wikiHochschulen);
      });
    });
  });
}
