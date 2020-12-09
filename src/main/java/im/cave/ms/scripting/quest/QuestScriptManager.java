///*
//	This file is part of the OdinMS Maple Story Server
//    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
//		       Matthias Butz <matze@odinms.de>
//		       Jan Christian Meyer <vimes@odinms.de>
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as
//    published by the Free Software Foundation version 3 as published by
//    the Free Software Foundation. You may not use, modify or distribute
//    this program under any other version of the GNU Affero General Public
//    License.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package im.cave.ms.scripting.quest;
//
//
//import im.cave.ms.client.MapleClient;
//import im.cave.ms.client.quest.Quest;
//import im.cave.ms.constants.GameConstants;
//import im.cave.ms.provider.data.QuestData;
//import im.cave.ms.scripting.AbstractScriptManager;
//import jdk.nashorn.api.scripting.NashornScriptEngine;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author RMZero213
// */
//public class QuestScriptManager extends AbstractScriptManager {
//
//    private static final QuestScriptManager instance = new QuestScriptManager();
//
//    public static QuestScriptManager getInstance() {
//        return instance;
//    }
//
//    private final Map<MapleClient, QuestActionManager> qms = new HashMap<>();
//    private final Map<MapleClient, NashornScriptEngine> scripts = new HashMap<>();
//
//    private NashornScriptEngine getQuestScriptEngine(MapleClient c, int questId) {
//        return getScriptEngine("quest/" + questId + ".js", c);
//    }
//
//    public void start(MapleClient c, int questId, int npc) {
//        Quest quest = QuestData.createQuestFromId(questId);
//        try {
//            QuestActionManager qm = new QuestActionManager(c, questId, npc, true);
//            if (qms.containsKey(c)) {
//                return;
//            }
//            if (c.canClickNPC()) {
//                qms.put(c, qm);
//
//                if (!quest.hasScriptRequirement(false)) {   // lack of scripted quest checks found thanks to Mali, Resinate
//                    qm.dispose();
//                    return;
//                }
//
//                NashornScriptEngine iv = getQuestScriptEngine(c, questId);
//                if (iv == null) {
//                    FilePrinter.printError(FilePrinter.QUEST_UNCODED, "START Quest " + questId + " is uncoded.");
//                    qm.dispose();
//                    return;
//                }
//
//                iv.put("qm", qm);
//                scripts.put(c, iv);
//                c.setClickedNPC();
//                iv.invokeFunction("start", (byte) 1, (byte) 0, 0);
//            }
//        } catch (final UndeclaredThrowableException ute) {
//            FilePrinter.printError(FilePrinter.QUEST + questId + ".txt", ute);
//            dispose(c);
//        } catch (final Throwable t) {
//            FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", t);
//            dispose(c);
//        }
//    }
//
//    public void start(MapleClient c, byte mode, byte type, int selection) {
//        NashornScriptEngine iv = scripts.get(c);
//        if (iv != null) {
//            try {
//                c.setClickedNPC();
//                iv.invokeFunction("start", mode, type, selection);
//            } catch (final Throwable ute) {
//                FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", ute);
//                dispose(c);
//            }
//        }
//    }
//
//    public void end(MapleClient c, int questId, int npc) {
//        MapleQuest quest = MapleQuest.getInstance(questId);
//        if (!c.getPlayer().getQuest(quest).getStatus().equals(MapleQuestStatus.Status.STARTED) || !c.getPlayer().getMap().containsNPC(npc)) {
//            dispose(c);
//            return;
//        }
//        try {
//            QuestActionManager qm = new QuestActionManager(c, questId, npc, false);
//            if (qms.containsKey(c)) {
//                return;
//            }
//            if (c.canClickNPC()) {
//                qms.put(c, qm);
//
//                if (!quest.hasScriptRequirement(true)) {
//                    qm.dispose();
//                    return;
//                }
//
//                NashornScriptEngine iv = getQuestScriptEngine(c, questId);
//                if (iv == null) {
//                    FilePrinter.printError(FilePrinter.QUEST_UNCODED, "END Quest " + questId + " is uncoded.");
//                    qm.dispose();
//                    return;
//                }
//
//                iv.put("qm", qm);
//                scripts.put(c, iv);
//                c.setClickedNPC();
//                iv.invokeFunction("end", (byte) 1, (byte) 0, 0);
//            }
//        } catch (final UndeclaredThrowableException ute) {
//            FilePrinter.printError(FilePrinter.QUEST + questId + ".txt", ute);
//            dispose(c);
//        } catch (final Throwable t) {
//            FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", t);
//            dispose(c);
//        }
//    }
//
//    public void end(MapleClient c, byte mode, byte type, int selection) {
//        NashornScriptEngine iv = scripts.get(c);
//        if (iv != null) {
//            try {
//                c.setClickedNPC();
//                iv.invokeFunction("end", mode, type, selection);
//            } catch (final Throwable ute) {
//                FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", ute);
//                dispose(c);
//            }
//        }
//    }
//
//    public void raiseOpen(MapleClient c, short questId, int npc) {
//        try {
//            QuestActionManager qm = new QuestActionManager(c, questId, npc, true);
//            if (qms.containsKey(c)) {
//                return;
//            }
//            if (c.canClickNPC()) {
//                qms.put(c, qm);
//
//                NashornScriptEngine iv = getQuestScriptEngine(c, questId);
//                if (iv == null) {
//                    //FilePrinter.printError(FilePrinter.QUEST_UNCODED, "RAISE Quest " + questId + " is uncoded.");
//                    qm.dispose();
//                    return;
//                }
//
//                iv.put("qm", qm);
//                scripts.put(c, iv);
//                c.setClickedNPC();
//                iv.invokeFunction("raiseOpen");
//            }
//        } catch (final UndeclaredThrowableException ute) {
//            FilePrinter.printError(FilePrinter.QUEST + questId + ".txt", ute);
//            dispose(c);
//        } catch (final Throwable t) {
//            FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", t);
//            dispose(c);
//        }
//    }
//
//    public void dispose(QuestActionManager qm, MapleClient c) {
//        qms.remove(c);
//        scripts.remove(c);
//        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
//        resetContext("quest/" + qm.getQuest() + ".js", c);
//        c.getPlayer().flushDelayedUpdateQuests();
//    }
//
//    public void dispose(MapleClient c) {
//        QuestActionManager qm = qms.get(c);
//        if (qm != null) {
//            dispose(qm, c);
//        }
//    }
//
//    public QuestActionManager getQM(MapleClient c) {
//        return qms.get(c);
//    }
//
//    public void reloadQuestScripts() {
//        scripts.clear();
//        qms.clear();
//    }
//}
