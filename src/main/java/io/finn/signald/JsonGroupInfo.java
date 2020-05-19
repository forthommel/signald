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

import org.whispersystems.signalservice.api.messages.SignalServiceGroup;
import org.whispersystems.signalservice.internal.util.Base64;

import org.asamk.signal.storage.groups.GroupInfo;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

class JsonGroupInfo {
    String groupId;
    List<String> members;
    String name;
    String type;
    Long avatarId;

    JsonGroupInfo(SignalServiceGroup groupInfo, String username) throws IOException, NoSuchAccountException {
        Manager manager = Manager.get(username);
        this.groupId = Base64.encodeBytes(groupInfo.getGroupId());
        if (groupInfo.getMembers().isPresent()) {
            this.members = groupInfo.getMembers().get();
        }
        if (groupInfo.getName().isPresent()) {
            this.name = groupInfo.getName().get();
        } else {
            GroupInfo group = manager.getGroup(groupInfo.getGroupId());
            if(group != null) {
                this.name = group.name;
            }
        }

        this.type = groupInfo.getType().toString();
    }

    JsonGroupInfo(GroupInfo groupInfo, Manager m) {
        this.groupId = Base64.encodeBytes(groupInfo.groupId);
	this.name = groupInfo.name;
	this.members =  new ArrayList<String>(groupInfo.members);
	this.avatarId = groupInfo.getAvatarId();
    }
}
