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

import org.whispersystems.signalservice.api.messages.calls.*;

import java.util.List;

class JsonCallMessage {
  OfferMessage offerMessage;
  AnswerMessage answerMessage;
  BusyMessage busyMessage;
  HangupMessage hangupMessage;
  List<IceUpdateMessage> iceUpdateMessages;
  Integer destinationDeviceId;
  boolean isMultiRing;

  JsonCallMessage(SignalServiceCallMessage callMessage) {
    if (callMessage.getOfferMessage().isPresent()) {
      offerMessage = callMessage.getOfferMessage().get();
    }

    if (callMessage.getAnswerMessage().isPresent()) {
      answerMessage = callMessage.getAnswerMessage().get();
    }

    if (callMessage.getBusyMessage().isPresent()) {
      busyMessage = callMessage.getBusyMessage().get();
    }

    if (callMessage.getHangupMessage().isPresent()) {
      hangupMessage = callMessage.getHangupMessage().get();
    }

    if (callMessage.getIceUpdateMessages().isPresent()) {
      iceUpdateMessages = callMessage.getIceUpdateMessages().get();
    }

    if (callMessage.getDestinationDeviceId().isPresent()) {
      destinationDeviceId = callMessage.getDestinationDeviceId().get();
    }

    isMultiRing = callMessage.isMultiRing();
  }
}
