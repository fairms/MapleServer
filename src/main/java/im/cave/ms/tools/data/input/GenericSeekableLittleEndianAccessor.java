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

import java.io.IOException;


public class GenericSeekableLittleEndianAccessor extends GenericLittleEndianAccessor implements SeekableLittleEndianAccessor {
    private SeekableInputStreamBytestream bs;

    /**
     * Class constructor
     * Provide a seekable input stream to wrap this object around.
     *
     * @param bs The byte stream to wrap this around.
     */
    public GenericSeekableLittleEndianAccessor(SeekableInputStreamBytestream bs) {
        super(bs);
        this.bs = bs;
    }


    @Override
    public void seek(long offset) {
        try {
            bs.seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Seek failed " + e);
        }
    }


    @Override
    public long getPosition() {
        try {
            return bs.getPosition();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("getPosition failed" + e);
            return -1;
        }
    }


    @Override
    public void skip(int num) {
        seek(getPosition() + num);
    }
}
