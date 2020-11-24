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

package io.finn.signald.clientprotocol.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.finn.signald.storage.AddressResolver;
import io.finn.signald.util.GroupsUtil;
import org.signal.storageservice.protos.groups.local.DecryptedGroup;
import org.signal.zkgroup.InvalidInputException;
import org.signal.zkgroup.groups.GroupMasterKey;
import org.whispersystems.signalservice.api.groupsv2.DecryptedGroupUtil;
import org.whispersystems.signalservice.api.messages.SignalServiceGroupV2;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.util.UuidUtil;
import org.whispersystems.util.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonGroupV2Info {
  public String id;
  public String masterKey;
  public int revision;

  // Fields from DecryptedGroup
  public String title;
  public Integer timer;

  @JsonProperty public List<JsonAddress> members;
  public List<JsonAddress> pendingMembers;
  public List<JsonAddress> requestingMembers;
  public String inviteLinkPassword;

  public JsonGroupV2Info() {}

  public JsonGroupV2Info(JsonGroupV2Info o) {
    id = o.id;
    masterKey = o.masterKey;
    update(o);
  }

  public JsonGroupV2Info(SignalServiceGroupV2 signalServiceGroupV2, DecryptedGroup decryptedGroup) {
    if (signalServiceGroupV2 != null) {
      masterKey = Base64.encodeBytes(signalServiceGroupV2.getMasterKey().serialize());
      id = Base64.encodeBytes(GroupsUtil.GetIdentifierFromMasterKey(signalServiceGroupV2.getMasterKey()).serialize());
      revision = signalServiceGroupV2.getRevision();
    }

    if (decryptedGroup != null) {
      title = decryptedGroup.getTitle();
      timer = decryptedGroup.getDisappearingMessagesTimer().getDuration();
      members = decryptedGroup.getMembersList().stream().map(e -> new JsonAddress(DecryptedGroupUtil.toUuid(e))).collect(Collectors.toList());
      pendingMembers = decryptedGroup.getPendingMembersList().stream().map(e -> new JsonAddress(DecryptedGroupUtil.toUuid(e))).collect(Collectors.toList());
      requestingMembers = decryptedGroup.getRequestingMembersList().stream().map(e -> new JsonAddress(UuidUtil.fromByteStringOrUnknown(e.getUuid()))).collect(Collectors.toList());
      //            inviteLinkPassword =
      //            decryptedGroup.getInviteLinkPassword().toString();
    }
  }

  public void update(JsonGroupV2Info other) {
    assert id.equals(other.id);
    assert masterKey.equals(other.masterKey);
    revision = other.revision;
    title = other.title;
    timer = other.timer;
    inviteLinkPassword = other.inviteLinkPassword;

    if (other.members != null) {
      members = new ArrayList<>();
      for (JsonAddress m : other.members) {
        members.add(new JsonAddress(m));
      }
    } else {
      members = null;
    }

    if (other.pendingMembers != null) {
      pendingMembers = new ArrayList<>();
      for (JsonAddress m : other.pendingMembers) {
        pendingMembers.add(new JsonAddress(m));
      }
    } else {
      pendingMembers = null;
    }

    if (other.requestingMembers != null) {
      requestingMembers = new ArrayList<>();
      for (JsonAddress m : other.requestingMembers) {
        requestingMembers.add(new JsonAddress(m));
      }
    } else {
      requestingMembers = null;
    }
  }

  public void resolveMembers(AddressResolver resolver) {
    if (members != null) {
      members = members.stream().map(e -> e.resolve(resolver)).collect(Collectors.toList());
    }
    if (pendingMembers != null) {
      pendingMembers = pendingMembers.stream().map(e -> e.resolve(resolver)).collect(Collectors.toList());
    }
    if (requestingMembers != null) {
      requestingMembers = requestingMembers.stream().map(e -> e.resolve(resolver)).collect(Collectors.toList());
    }
  }

  public JsonGroupV2Info sanatized() {
    JsonGroupV2Info output = new JsonGroupV2Info(this);
    output.masterKey = null;
    return output;
  }

  @JsonIgnore
  public List<SignalServiceAddress> getMembers() {
    if (members == null) {
      return null;
    }
    List<SignalServiceAddress> l = new ArrayList<>();
    for (JsonAddress m : members) {
      l.add(m.getSignalServiceAddress());
    }
    return l;
  }

  @JsonIgnore
  public SignalServiceGroupV2 getSignalServiceGroupV2() throws IOException, InvalidInputException {
    GroupMasterKey groupMasterKey = new GroupMasterKey(Base64.decode(masterKey));
    return SignalServiceGroupV2.newBuilder(groupMasterKey).withRevision(revision).build();
  }
}
