var mongoose = require('mongoose');

mongoose.connect('mongodb://localhost/eis-api-poc');

var Schema = mongoose.Schema;
var ObjectId = Schema.ObjectId;

var beitragSchema = Schema({
  _parent: { type: ObjectId, ref: 'Beitrag' },
  title: String,
  children: [{ type: ObjectId, ref: 'Beitrag' }]
});

var Beitrag = mongoose.model('Beitrag', beitragSchema);
var miID, wiID;
var mi, wi;
var sub1, sub2, sub3;
var unter1ID, unter2ID, unter3ID;

function init(cb) {
  var beitrag1 = new Beitrag({
    _parent: null,
    title: "Medieninformatik"
  });

  var beitrag2 = new Beitrag({
    _parent: null,
    title: "Wirtschaftsinformatik"
  });

  beitrag1.save(function (err) {
    if (err) throw new Error(err);
    mi = beitrag1;

    miID = beitrag1._id;

    beitrag2.save(function (err) {
      if (err) throw new Error(err);
      wi = beitrag2;

      wiID = beitrag2._id;
      cb();
    });
  });
}

function ersteSchicht(cb) {
  var unter1 = new Beitrag({
    _parent: miID,
    title: "Ein Beitrag"
  });

  var unter2 = new Beitrag({
    _parent: miID,
    title: "Weiterer Beitrag"
  });

  var unter3 = new Beitrag({
    _parent: wiID,
    title: "Beitrag in WI Thread"
  });

  unter1.save(function (err) {
    if (err) throw new Error(err);
    unter1ID = unter1._id;
    sub1 = unter1;
    mi.children.push(unter1);
    mi.save();

    unter2.save(function (err) {
      if (err) throw new Error(err);
      unter2ID = unter2._id;
      sub2 = unter2;
      mi.children.push(unter2);
      mi.save();

      unter3.save(function (err) {
        if (err) throw new Error(err);
        unter3ID = unter3._id;
        sub3 = unter3;
        wi.children.push(unter3);
        wi.save();

        cb();
      });
    });
  });
}

function zweiteSchicht(cb) {
  var zweite1 = new Beitrag({
    _parent: unter1ID,
    title: "Ein Kommentar"
  });

  var zweite2 = new Beitrag({
    _parent: unter1ID,
    title: "Weiterer Kommentar"
  });

  var zweite3 = new Beitrag({
    _parent: unter3ID,
    title: "Hier, ein Kommentar"
  });

  zweite1.save(function (err) {
    if (err) throw new Error(err);
    sub1.children.push(zweite1);
    sub1.save(function (err) {
      if (err) throw new Error(err);
    });

    zweite2.save(function (err) {
      if (err) throw new Error(err);
      sub1.children.push(zweite2);
      sub1.save(function (err) {
        if (err) throw new Error(err);
      });

      zweite3.save(function (err) {
        if (err) throw new Error(err);
        sub3.children.push(zweite3);
        sub3.save(function (err) {
          if (err) throw new Error(err);
        });

        cb();
      });
    });
  });
}

init(function() {
  ersteSchicht(function() {
    zweiteSchicht(function() {
      Beitrag.find({ _parent: null })
        .select('title children parent')
        .lean()
        .populate({ path: 'children' })
        .exec(function(err, result) {
          Beitrag.populate(result, { path: 'children.children' }, function(err, _result) {
            Beitrag.populate(result, { path: 'children.children.children' }, function(err, _result) {
              console.log(require('util').inspect(result, { depth: null }));
              process.exit(0);
            });
          });
        });
    });
  });
});
