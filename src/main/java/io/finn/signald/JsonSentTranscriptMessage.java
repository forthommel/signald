/*
 * Copyright (C) 2020 Finn Herzfeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.finn.signald;

import io.finn.signald.clientprotocol.v1.JsonAddress;
import org.whispersystems.signalservice.api.messages.multidevice.SentTranscriptMessage;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class JsonSentTranscriptMessage {
  JsonAddress destination;
  long timestamp;
  long expirationStartTimestamp;
  JsonDataMessage message;
  Map<String, Boolean> unidentifiedStatus = new HashMap<>();
  boolean isRecipientUpdate;

  JsonSentTranscriptMessage(SentTranscriptMessage s, String username) throws IOException, NoSuchAccountException {
    if (s.getDestination().isPresent()) {
      destination = new JsonAddress(s.getDestination().get());
    }
    timestamp = s.getTimestamp();
    expirationStartTimestamp = s.getExpirationStartTimestamp();
    message = new JsonDataMessage(s.getMessage(), username);
    for (SignalServiceAddress r : s.getRecipients()) {
      if (r.getNumber().isPresent()) {
        unidentifiedStatus.put(r.getNumber().get(), s.isUnidentified(r.getNumber().get()));
      }
      if (r.getUuid().isPresent()) {
        unidentifiedStatus.put(r.getUuid().get().toString(), s.isUnidentified(r.getUuid().get()));
      }
    }
    isRecipientUpdate = s.isRecipientUpdate();
  }
}
