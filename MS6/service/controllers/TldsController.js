import fs from "fs";
import path from "path";

export function tldList(req, res, next) {
  let tldpath = path.resolve(__dirname, "..", "tlds.json");

  // Read tld json and send it back
  fs.readFile(tldpath, "utf8", (err, data) => {
    if (err) return next(err);
    res.json({
      tlds: JSON.parse(data)
    });
  });
}
