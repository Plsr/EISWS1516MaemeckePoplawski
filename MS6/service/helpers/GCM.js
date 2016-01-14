import { GCM } from "gcm";

var gcm = new GCM(__config.gcm_api_key);

export function sendGCM(toUser, fromUser, entry) {
  // Only send if user is registered with gcm in app
  if (!toUser.device_id) return;

  // Stop if user tries to send to himself
  // if (toUser.device_id === fromUser.device_id) return;

  let message = {
    registration_id: toUser.device_id,
    "data.entry": entry._id + "",
    "data.fromuser": fromUser._id + ""
  };

  gcm.send(message, (err, messageId) => {
    if (err) return console.log("Failed to send to gcm:", err);
    else console.log(`Sent gcm to ${toUser.name} with id ${toUser._id}`);
  });
}
