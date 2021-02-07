const talk_how_tow_create_guild = Array(
    "家族……你可以把它理解成一个小的组织。是拥有相同理想的人为了同一个目的而聚集在一起成立的组织。 但是家族是经过家族总部的正式登记，是经过认可的组织。",
    "要想创建家族，至少必须达到100级。",
    "要想创建家族，你需要总共6人。这6个人应该在同一队伍，组队长应该来跟我说话。请注意，组队长将成为家族族长?",
    "创建家族还需要1500000金币的费用！",
    "创建家族，带6个人来~你不能没有6个人就组成一个...哦，当然，6个人不能是其他家族的成员！如果有人已经加入了其他家族，那就不行了！！"
);

function start() {
    let HashMap = Java.type('java.util.HashMap');
    let options = new HashMap();
    const player = cm.getPlayer();
    const guild = player.getGuild();
    if (guild == null) {
        let res = cm.sendNext("你……是因为对家族感兴趣，才会来找我的吗？");
        if (res === 1) {
            options.put(null, "你想要干什么呢？快告诉我吧。")
            options.put(1, "请告诉我家族是什么");
            options.put(2, "怎么才能创建家族呢？")
            options.put(3, "我想创建家族")
            options.put(4, "我想了解有关家族系统的详细说明")
            res = cm.sendAskMenu(options);
            switch (res) {
                case 1:
                    cm.sendNext("家族……你可以把它理解成一个小的组织。是拥有相同理想的人为了同一个目的而聚集在一起成立的组织。 但是家族是经过家族总部的正式登记，是经过认可的组织。")
                    break
                case 2:
                    let i = 0;
                    while (res !== -1 && i < talk_how_tow_create_guild.length) {
                        res = cm.sendNext(talk_how_tow_create_guild[i])
                        if (res === 1) {
                            i++
                        } else {
                            i--
                        }
                    }
                    break
                case 3:
                    res = cm.sendAskYesNo("哦！你是来创建家族的吗……要想创建家族，需要500万金币。我相信你一定已经准备好了。好的～你想创建家族吗？");
                    if (res === 1) {
                        if (player.getParty() === null) {
                            cm.sendNext("我不在乎你觉得自己有多强…为了创建一个家族，你需要参加一个6人的组队。如果你真的很想成立一个家族的话，请创建一个6人的组队，然后把你所有的队员带回来。")
                            break
                        } else {
                            if (cm.sendNext(".输入您家族的名称，您的公会将被创建。") === 1) {
                                cm.inputGuildName();
                            }
                        }
                    }
                    break
                case 4:
                    break
            }
        }
    } else if (guild.getLeaderId() === player.getId()) {
        options.put(null, "我能帮你什么吗？");
        options.put(1, "我想增加家族人数");
        options.put(2, "我想解散家族");
    }
}