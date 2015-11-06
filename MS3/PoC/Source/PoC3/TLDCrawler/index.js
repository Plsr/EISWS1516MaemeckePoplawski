var wikiHochschulen = require('./hochschulliste');
var get = require('./crawler').getPageContent;
var extract = require('./crawler').getPagePart;
var $ = require('cheerio');
var async = require('async');
var parseDomain = require("parse-domain");
var fs = require('fs');

var hochschulenUrls = [];
var notPraseable = 0;
var found = 0;

var wikiRoot = "http://de.wikipedia.org";
var infoboxSelector = "#Vorlage_Infobox_Hochschule";
var linkSelector = ".external";

wikiHochschulen(function (wikiUrls) {
  async.each(wikiUrls, function (url, cb) {
    get(wikiRoot + url, function (content) {
      extract(infoboxSelector, content, function (infobox) {
        var $a = $(linkSelector, infobox[0]).attr('href');
        if ($a) {
          var domainObj = parseDomain($(linkSelector, infobox[0]).attr('href'));
          var domain = domainObj.domain + '.' + domainObj.tld;
          hochschulenUrls.push(domain);
          found++;
        } else {
          notPraseable++;
        }
        cb();
      });
    });
  }, function() {
    console.log('Found TLDs:', found);
    console.log('Could not parse:', notPraseable);
    fs.writeFile('tlds.json', JSON.stringify(hochschulenUrls, null, 2), function (err) {
      console.log('Finished.');
    });
  });
})
