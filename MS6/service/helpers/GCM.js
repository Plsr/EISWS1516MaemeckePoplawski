import { GCM } from "gcm";

var gcm = new GCM(__config.gcm_api_key);

export function sendGCM(toUser, fromUser, entry) {
  if (!toUser.device_id) return;

  let message = {
    registration_id: toUser.device_id,
    "data.entryid": entry._id,
    "data.fromuserid": fromUser._id
  };

  gmc.send(message, (err, messageId) => {
    if (err) return console.log("Failed to send to gcm:", err);
    else console.log(`Sent gcm to ${toUser.name} with id ${toUser._id}`);
  });
}
