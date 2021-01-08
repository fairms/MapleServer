package im.cave.ms.client.social.party;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.PartyType;
import im.cave.ms.network.netty.OutPacket;


public class PartyResult {

    private final PartyType type;
    private Party party;
    private PartyMember member;
    private MapleCharacter chr;
    private int arg1;
    private boolean bool, bool2;
    private String str;

    public PartyResult(PartyType type) {
        this.type = type;
    }

    public static PartyResult createNewParty(Party party) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_CreateNewParty_Done);
        pr.party = party;
        return pr;
    }

    public static PartyResult loadParty(Party party) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_LoadParty_Done);
        pr.party = party;
        return pr;
    }

    public static PartyResult inviteIntrusion(Party party, MapleCharacter chr) {
        PartyResult pr = new PartyResult(PartyType.PartyReq_InviteIntrusion);
        pr.party = party;
        pr.chr = chr;
        return pr;
    }

    public static PartyResult applyParty(Party party, PartyMember member) {
        PartyResult pr = new PartyResult(PartyType.PartyReq_ApplyParty);
        pr.party = party;
        pr.member = member;
        return pr;
    }

    public static PartyResult changePartyBoss(Party party, int reason) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_ChangePartyBoss_Done);
        pr.party = party;
        pr.arg1 = reason;
        return pr;
    }

    public static PartyResult withdrawParty(Party party, PartyMember kickedMember, boolean partyStillExists, boolean expelled) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_WithdrawParty_Done);
        pr.party = party;
        pr.member = kickedMember;
        pr.bool = partyStillExists;
        pr.bool2 = expelled;
        return pr;
    }

    public static PartyResult joinParty(Party party, String joinerName) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_JoinParty_Done);
        pr.party = party;
        pr.str = joinerName;
        return pr;
    }

    public static PartyResult updateShutdownStatus(MapleCharacter chr) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_UpdateShutdownStatus);
        pr.chr = chr;
        return pr;
    }

    public static PartyResult changeLevelOrJob(MapleCharacter chr) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_ChangeLevelOrJob);
        pr.chr = chr;
        return pr;
    }

    public static PartyResult msg(PartyType type) {
        return new PartyResult(type);
    }

    public static PartyResult userMigration(Party party) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_UserMigration);
        pr.party = party;
        return pr;
    }

    public static PartyResult partySetting(boolean appliable, String name) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_PartySettingDone);
        pr.bool = appliable;
        pr.str = name;
        return pr;
    }

    public static PartyResult inviteSent(String name) {
        PartyResult pr = new PartyResult(PartyType.PartyRes_InviteParty_Sent);
        pr.str = name;
        return pr;
    }

    public void encode(OutPacket out) {
        out.write(type.getVal());
        switch (type) {
            case PartyRes_InviteParty_Sent:
                out.writeMapleAsciiString(str);
                break;
            case PartyRes_LoadParty_Done:
                out.writeInt(party.getId());
                party.encode(out);
                break;
            case PartyRes_CreateNewParty_Done:
                out.writeInt(party.getId());
                party.getTownPortal().encode(out);
                PartyMember leader = party.getPartyLeader();
                out.writeBool(leader.isOnline());
                out.writeBool(party.isAppliable());
                out.write(0);
                out.writeMapleAsciiString(party.getName());
                break;
            case PartyReq_InviteIntrusion:
                out.writeInt(party.getId());
                out.writeMapleAsciiString(chr.getName());
                out.writeInt(chr.getLevel());
                out.writeInt(chr.getJob());
                out.writeInt(0);
                out.writeShort(0);
                break;
            case PartyReq_ApplyParty:
                out.writeInt(party.getId());
                out.writeMapleAsciiString(member.getCharName());
                out.writeInt(member.getLevel());
                out.writeInt(member.getJob());
                out.writeInt(member.getSubSob());
                break;
            case PartyRes_ChangePartyBoss_Done:
                out.writeInt(member.getCharId());
                out.write(arg1); // nReason
                break;
            case PartyRes_WithdrawParty_Done:
                out.writeInt(party.getId());
                out.writeInt(member.getCharId());
                out.writeBool(bool); // bPartyExists
                if (bool) {
                    out.writeBool(bool2); // bExpelled
                    out.writeMapleAsciiString(member.getCharName());
                    party.encode(out);
                }
                out.writeInt(member.getCharId());
                break;
            case PartyRes_JoinParty_Done:
                out.writeInt(party.getId());
                out.writeMapleAsciiString(str); // sJoinerName
                out.write(0);// unknown
                out.writeInt(0);// unknown
                party.encode(out);
                break;
            case PartyRes_UserMigration:
                out.writeInt(party.getId());
//                party.encode(out);
                break;
            case PartyRes_ChangeLevelOrJob:
                out.writeInt(chr.getId());
                out.writeInt(chr.getLevel());
                out.writeInt(chr.getJob());
                break;
            case PartyRes_UpdateShutdownStatus:
                out.writeInt(chr.getId());
                out.writeBool(chr.isOnline());
                break;
            case PartyRes_PartySettingDone:
                out.writeBool(bool);
                out.writeMapleAsciiString(str);
                out.writeLong(0);
                break;
        }
    }
}
