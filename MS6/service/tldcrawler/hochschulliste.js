var get = require('./crawler').getPageContent;
var extract = require('./crawler').getPagePart;
var $ = require('cheerio');


var hochschullisteURL = "https://de.wikipedia.org/wiki/Liste_der_Hochschulen_in_Deutschland";
var hochschullisteSelector = "table";

var wikiHochschulen = [];

module.exports = function(cb) {
  // Get content of Wikipedia Hochschulliste
  get(hochschullisteURL, function (page) {

    // Get only Hochschulliste Table
    extract(hochschullisteSelector, page, function (pagePart) {

      // Get all table rows
      extract('tr', pagePart[0], function (trs) {

        // For each table row, copy the link to the Wikipedia Page
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
