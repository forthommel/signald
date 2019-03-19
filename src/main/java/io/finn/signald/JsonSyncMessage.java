/**
 * Copyright (C) 2018 Finn Herzfeld
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

import org.whispersystems.signalservice.api.messages.multidevice.ContactsMessage;
import org.whispersystems.signalservice.api.messages.multidevice.ReadMessage;
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage;
import org.whispersystems.signalservice.internal.util.Base64;

import java.util.List;

class JsonSyncMessage {
    JsonDataMessage sentMessage;
    JsonAttachment contacts;
    boolean contactsComplete;
    JsonAttachment groups;
    List<String> blockedNumbers;
    List<String> blockedGroups;
    String requestType;
    List<ReadMessage> readMessages;
    // JsonVerifiedMessage verified;
    JsonConfigurationMessage configuration;

    JsonSyncMessage(SignalServiceSyncMessage syncMessage, Manager m) {
        if(syncMessage.getContacts().isPresent()) {
          ContactsMessage c = syncMessage.getContacts().get();
          contacts = new JsonAttachment(c.getContactsStream(), m);
          contactsComplete = c.isComplete();
        }

        if(syncMessage.getGroups().isPresent()) {
          groups = new JsonAttachment(syncMessage.getGroups().get(), m);
        }



        if (syncMessage.getSent().isPresent()) {
            this.sentMessage = new JsonDataMessage(syncMessage.getSent().get().getMessage(), null);
        }

        if (syncMessage.getBlockedList().isPresent()) {
            blockedNumbers = syncMessage.getBlockedList().get().getNumbers();
            for(byte[] groupId : syncMessage.getBlockedList().get().getGroupIds()) {
                blockedGroups.add(Base64.encodeBytes(groupId));
            }
        }

        if(syncMessage.getRequest().isPresent()) {
            requestType = syncMessage.getRequest().get().getRequest().toString();
        }

        if (syncMessage.getRead().isPresent()) {
            this.readMessages = syncMessage.getRead().get();
        }
    }
}
