package im.cave.ms.client.party;

import im.cave.ms.client.character.MapleCharacter;
import im.cave.ms.enums.PartyType;
import im.cave.ms.network.netty.OutPacket;

/**
 * Created on 3/19/2018.
 */
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

    public void encode(OutPacket outPacket) {
        outPacket.write(type.getVal());
        switch (type) {
            case PartyRes_LoadParty_Done:
                outPacket.writeInt(party.getId());
                party.encode(outPacket);
                break;
            case PartyRes_CreateNewParty_Done:
                outPacket.writeInt(party.getId());
                party.getTownPortal().encode(outPacket);
                PartyMember leader = party.getPartyLeader();
                outPacket.writeBool(leader.isOnline());
                outPacket.writeBool(party.isAppliable());
                outPacket.write(0);
                outPacket.writeMapleAsciiString(party.getName());
                break;
            case PartyReq_InviteIntrusion:
                outPacket.writeInt(party.getId());
                outPacket.writeMapleAsciiString(chr.getName());
                outPacket.writeInt(chr.getLevel());
                outPacket.writeInt(chr.getJob().getJobId());
                outPacket.writeInt(0);
                break;
            case PartyReq_ApplyParty:
                outPacket.writeInt(party.getId());
                outPacket.writeMapleAsciiString(member.getCharName());
                outPacket.writeInt(member.getLevel());
                outPacket.writeInt(member.getJob());
                outPacket.writeInt(member.getSubSob());
                break;
            case PartyRes_ChangePartyBoss_Done:
                outPacket.writeInt(member.getCharId());
                outPacket.write(arg1); // nReason
                break;
            case PartyRes_WithdrawParty_Done:
                outPacket.writeInt(party.getId());
                outPacket.writeInt(member.getCharId());
                outPacket.writeBool(bool); // bPartyExists
                if (bool) {
                    outPacket.writeBool(bool2); // bExpelled
                    outPacket.writeMapleAsciiString(member.getCharName());
//                    party.encode(outPacket);
                }
                break;
            case PartyRes_JoinParty_Done:
                outPacket.writeInt(party.getId());
                outPacket.writeMapleAsciiString(str); // sJoinerName
                outPacket.write(0);// unknown
                outPacket.writeInt(0);// unknown
//                party.encode(outPacket);
                break;
            case PartyRes_UserMigration:
                outPacket.writeInt(party.getId());
//                party.encode(outPacket);
                break;
            case PartyRes_ChangeLevelOrJob:
                outPacket.writeInt(chr.getId());
                outPacket.writeInt(chr.getLevel());
                outPacket.writeInt(chr.getJob().getJobId());
                break;
            case PartyRes_UpdateShutdownStatus:
                outPacket.writeInt(chr.getId());
                outPacket.writeBool(chr.isOnline());
                break;
        }
    }
}
