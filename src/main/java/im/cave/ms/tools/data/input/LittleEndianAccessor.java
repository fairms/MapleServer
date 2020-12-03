/*
	This file is part of the OdinMS Maple Story AbstractServer
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package im.cave.ms.tools.data.input;

import im.cave.ms.tools.Position;

import java.awt.*;

/**
 * @author Frz
 */
public interface LittleEndianAccessor {
    byte readByte();

    char readChar();

    short readShort();

    int readInt();

    Position readPos();

    long readLong();

    void skip(int num);

    byte[] read(int num);

    float readFloat();

    double readDouble();

    String readAsciiString(int n);

    String readNullTerminatedAsciiString();

    String readMapleAsciiString();

    long getBytesRead();

    long available();
}
