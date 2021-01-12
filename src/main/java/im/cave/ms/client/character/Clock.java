package im.cave.ms.client.character;

import im.cave.ms.client.field.MapleMap;
import im.cave.ms.connection.netty.OutPacket;
import im.cave.ms.connection.packet.opcode.SendOpcode;
import im.cave.ms.connection.server.service.EventManager;
import im.cave.ms.enums.ClockType;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Clock {
    private ClockType clockType;
    private MapleCharacter chr;
    private MapleMap map;
    private int seconds;
    private long timeInMillis;
    private ScheduledFuture clockRemovalTimer;

    public Clock(ClockType clockType, MapleMap map, int seconds) {
        this.clockType = clockType;
        this.map = map;
        this.seconds = seconds;
        this.timeInMillis = (seconds * 1000) + System.currentTimeMillis();
    }

    //开始一个时钟
    public static void startTimer(MapleCharacter chr, ClockType type, int seconds) {
        Clock clock = new Clock(type, chr.getMap(), seconds);
        switch (type) {
            case SecondsClock:
                chr.getMap().broadcastMessage(Clock.secondsClock(seconds));
                break;
            case StopWatch:
                chr.getMap().broadcastMessage(Clock.stopWatch(seconds));
                break;
        }
        clock.setChr(chr);
        clock.setClockRemovalTimer(EventManager.addEvent(clock::timeout, seconds, TimeUnit.SECONDS));
        chr.setClock(clock);
    }

    //封包
    private static OutPacket stopWatch(int seconds) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CLOCK.getValue());
        out.write(ClockType.StopWatch.getVal());
        out.writeInt(seconds * 1000);
        return out;
    }

    private static OutPacket secondsClock(int seconds) {
        OutPacket out = new OutPacket();
        out.writeShort(SendOpcode.CLOCK.getValue());
        out.write(ClockType.SecondsClock.getVal());
        out.writeInt(seconds);
        return out;
    }

    private static OutPacket cancelClock() {
        return secondsClock(-1);
    }

    public void showClock() {
        if (chr != null) {
            switch (clockType) {
                case SecondsClock:
                    chr.announce(Clock.secondsClock(getRemainingTime()));
                    break;
                case StopWatch:
                    chr.announce(Clock.stopWatch(getRemainingTime()));
                    break;
            }
        }
    }

    //手动停止时钟
    public void stopClock() {
        chr.announce(Clock.cancelClock());
        clockRemovalTimer.cancel(true);
        chr.setClock(null);
    }

    //获取剩余时间
    private int getRemainingTime() {
        return (int) ((timeInMillis - System.currentTimeMillis()) / 1000);
    }

    //时间到
    private void timeout() {
        if (chr != null) {
            chr.announce(Clock.cancelClock());
            chr.setClock(null);
            clockRemovalTimer.cancel(true);
            chr.changeMap(map.getReturnMap());
        }
    }

    // Getter Setter
    public ClockType getClockType() {
        return clockType;
    }

    public void setClockType(ClockType clockType) {
        this.clockType = clockType;
    }

    public MapleMap getMapleMap() {
        return map;
    }

    public void setMapleMap(MapleMap map) {
        this.map = map;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public MapleCharacter getChr() {
        return chr;
    }

    public void setChr(MapleCharacter chr) {
        this.chr = chr;
    }

    public ScheduledFuture getClockRemovalTimer() {
        return clockRemovalTimer;
    }

    public void setClockRemovalTimer(ScheduledFuture clockRemovalTimer) {
        this.clockRemovalTimer = clockRemovalTimer;
    }
}
