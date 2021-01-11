package im.cave.ms.enums;

import java.util.Arrays;


public enum CashItemType {
    Req_WebShopOrderGetList(0),
    Req_LoadLocker(1),
    Req_LoadWish(2),
    Req_Buy(3),
    Req_Gift(4),
    Req_SetCart(5),
    Req_IncSlotCount(6),
    Req_IncTrunkCount(7),
    Req_IncCharSlotCount(8),
    Req_IncBuyCharCount(8),
    Req_EnableEquipSlotExt(9),
    Req_CancelPurchase(10),
    Req_ConfirmPurchase(11),
    Req_Destroy(12),
    Req_MoveLtoS(13),
    Req_MoveStoL(14),
    Req_Expire(15),
    Req_Use(16),
    Req_StatChange(17),
    Req_SkillChange(18),
    Req_SkillReset(19),
    Req_DestroyPetItem(20),
    Req_SetPetName(21),
    Req_SetPetLife(22),
    Req_SetPetSkill(23),
    Req_SetItemName(24),
    Req_SetAndroidName(25),
    Req_SendMemo(26),
    Req_GetAdditionalCashShopInfo(27),
    Req_GetMaplePoint(28),
    Req_UseMaplePointFromGameSvr(29),
    Req_Rebate(30),
    Req_UseCoupon(31),
    Req_GiftCoupon(32),
    Req_Couple(33),
    Req_BuyPackage(34),
    Req_GiftPackage(35),
    Req_BuyNormal(36),
    Req_ApplyWishListEvent(39),
    Req_MovePetStat(40),
    Req_FriendShip(41),
    Req_ShopScan(42),
    Req_ShopOptionScan(43),
    Req_ShopScanSell(44),
    Req_LoadPetExceptionList(45),
    Req_UpdatePetExceptionList(46),
    Req_DestroyScript(47),
    Req_CashItemCreate(48),
    Req_PurchaseRecord(49),
    Req_DeletePurchaseRecord(50),
    Req_TradeDone(51),
    Req_BuyDone(52),
    Req_TradeSave(53),
    Req_TradeLog(54),
    Req_CharacterSale(55),
    Req_SellCashItemBundleToShop(56),
    Req_Refund(57),
    Req_ConfirmRefund(58),
    Req_CancelRefund(59),
    Req_SetItemNonRefundable(60),
    Req_WebShopOrderBuyItems(61),
    Req_UseCashRandomItem(62),
    Req_UseMaplePointGiftToken(63),
    Req_BuyByToken(64),
    Req_Buy_ByMeso(65),
    Req_UpgradeValuePack(66),
    Req_BuyFarmGift(67),
    Req_CashItemGachapon(68),
    Req_GiftScript(69),
    Req_MoveToAuctionStore(70),
    Req_ClearCashOption(71),
    Req_MasterPiece(72),
    Req_DestroyCoupleRings(73),
    Req_DestroyFriendshipRings(74),
    Req_LockerTransfer(75),
    Req_TradeLogForAuction(76),
    Req_MoveToLockerFromAuction(77),
    Req_NexonStarCouponUse(78),


    Res_CharacterSaleSuccess(0),
    Res_CharacterSaleFail(1),
    Res_LimitGoodsCount_Changed(-1),
    Res_WebShopOrderGetList_Done(-1),
    Res_WebShopOrderGetList_Failed(-1),
    Res_WebShopReceive_Done(5),
    Res_LoadLocker_Done(7), // correct   7
    Res_LoadLocker_Failed(8), // correct 8
    Res_LoadGift_Done(9),
    Res_LoadGift_Failed(10),
    Res_LoadWish_Done(11),
    Res_LoadWish_Failed(12),// ^----- maybe incorrect
    Res_SetWish_Done(13),
    Res_SetWish_Failed(14),
    Res_Buy_Done(15),
    Res_Buy_Failed(16),
    Res_UseCoupon_Done(15),
    Res_NexonStarCouponUse_Done(16),
    Res_NexonStarCoupon_Failed(17),
    Res_UseCoupon_Done_NormalItem(18),
    Res_GiftCoupon_Done(19),
    Res_UseCoupon_Failed(20),
    Res_UseCoupon_CashItem_Failed(21),
    Res_Gift_Done(22),
    Res_Gift_Failed(23),
    Res_IncSlotCount_Done(24),
    Res_IncSlotCount_Failed(25),
    Res_IncTrunkCount_Done(26),
    Res_IncTrunkCount_Failed(27),
    Res_IncCharSlotCount_Done(28),
    Res_IncCharSlotCount_Failed(29),
    Res_IncBuyCharCount_Done(30),
    Res_IncBuyCharCount_Failed(31),
    Res_EnableEquipSlotExt_Done(32),
    Res_EnableEquipSlotExt_Failed(33),
    Res_MoveLtoS_Done(34),
    Res_MoveLtoS_Failed(35),
    Res_MoveStoL_Done(36),
    Res_MoveStoL_Failed(37),
    Res_Destroy_Done(38),
    Res_Destroy_Failed(39),
    Res_Expire_Done(44),
    Res_Expire_Failed(45),
    Res_Use_Done(46),
    Res_Use_Failed(47),
    Res_StatChange_Done(48),
    Res_StatChange_Failed(49),
    Res_SkillChange_Done(50),
    Res_SkillChange_Failed(51),
    Res_SkillReset_Done(52),
    Res_SkillReset_Failed(53),
    Res_DestroyPetItem_Done(54),
    Res_DestroyPetItem_Failed(55),
    Res_SetPetName_Done(56),
    Res_SetPetName_Failed(57),
    Res_SetPetLife_Done(58),
    Res_SetPetLife_Failed(59),
    Res_MovePetStat_Failed(60),
    Res_MovePetStat_Done(54),
    Res_SetPetSkill_Failed(55),
    Res_SetPetSkill_Done(56),
    Res_SendMemo_Done(57),
    Res_SendMemo_Warning(58),
    Res_SendMemo_Failed(59),
    Res_GetMaplePoint_Done(60),
    Res_GetMaplePoint_Failed(61),
    Res_UseMaplePointFromGameSvr_Done(62),
    Res_UseMaplePointFromGameSvr_Failed(63),
    Res_CashItemGachapon_Done(64),
    Res_CashItemGachapon_Failed(65),
    Res_Rebate_Done(67),
    Res_Rebate_Failed(68),
    Res_Couple_Done(69),
    Res_Couple_Failed(70),
    Res_BuyPackage_Done(71),
    Res_BuyPackage_Failed(72),
    Res_GiftPackage_Done(73),
    Res_GiftPackage_Failed(74),
    Res_BuyNormal_Done(75),
    Res_BuyNormal_Failed(76),
    Res_ApplyWishListEvent_Done(83),
    Res_ApplyWishListEvent_Failed(84),
    Res_Friendship_Done(85),
    Res_Friendship_Failed(86),
    Res_LoadExceptionList_Done(87),
    Res_LoadExceptionList_Failed(88),
    Res_UpdateExceptionList_Done(89),
    Res_UpdateExceptionList_Failed(90),
    Res_DestroyScript_Done(91),
    Res_DestroyScript_Failed(92),
    Res_CashItemCreate_Done(93),
    Res_CashItemCreate_Failed(94),
    Res_ClearOptionScript_Done(95),
    Res_ClearOptionScript_Failed(96),
    Res_Bridge_Failed(97),
    Res_PurchaseRecord_Done(98),
    Res_PurchaseRecord_Failed(99),
    Res_DeletePurchaseRecord_Done(100),
    Res_DeletePurchaseRecord_Failed(101),
    Res_Refund_OK(102),
    Res_Refund_Done(103),
    Res_Refund_Failed(104),
    Res_UseRandomCashItem_Done(105),
    Res_UseRandomCashItem_Failed(106),
    Res_SetAndroidName_Done(107),
    Res_SetAndroidName_Failed(108),
    Res_UseMaplePointGiftToken_Done(109),
    Res_UseMaplePointGiftToken_Failed(110),
    Res_BuyByToken_Done(111),
    Res_BuyByToken_Failed(112),
    Res_UpgradeValuePack_Done(113),
    Res_UpgradeValuePack_Failed(114),
    Res_EventCashItem_Buy_Result(115),
    Res_BuyFarmGift_Done(116),
    Res_BuyFarmGift_Failed(117),
    Res_GiftScript_Done(118),
    Res_GiftScript_Failed(119),
    Res_AvatarMegaphone_Queue_Full(120),
    Res_AvatarMegaphone_Level_Limit(121),
    Res_MovoCashItemToAuction_Done(122),
    Res_MovoCashItemToAuction_Failed(123),
    Res_MasterPiece_Done(124),
    Res_MasterPiece_Failed(125),
    Res_DestroyCoupleRings_Done(126),
    Res_DestroyCoupleRings_Failed(127),
    Res_DestroyFriendShipRings_Done(128),
    Res_DestroyFriendShipRings_Failed(129),
    Res_LockerTransfer_Done(130),
    Res_LockerTransfer_Failed(131),
    Res_MovoCashItemToLockerFromAuction_Done(132),
    Res_MovoCashItemToLockerFromAuction_Failed(133),


    FailReason_Unknown(0), //未知原因 然后退出商城
    FailReason_Timeout(1),//操作超时 然后退出商城
    FailReason_CashDaemonDBError(2), //未知原因 然后退出商城
    FailReason_NoRemainCash(3), //冒险券不足
    FailReason_GiftUnderAge(4), //未满14岁不能赠送
    FailReason_GiftLimitOver(5), //超过赠送限额
    FailReason_GiftSameAccount(6), //无法向本人账号赠送
    FailReason_GiftUnknownRecipient(7), //角色名错误
    FailReason_GiftRecipientGenderMismatch(8), //接收者性别限制
    FailReason_GiftRecipientLockerFull(9), //接收者保管箱满了
    FailReason_BuyStoredProcFailed(10),//请确认是否超过可以保有的现金道具数量
    FailReason_GiftStoredProcFailed(11), //角色名是否出错或者该物品是否有性别限制
    FailReason_GiftNoReceiveCharacter(12), //未知
    FailReason_GiftNoSenderCharacter(13), // 未知
    FailReason_InvalidCoupon(14), //领奖券是否正确
    FailReason_ExpiredCoupon(15), //当前世界无法使用该券
    FailReason_UsedCoupon(16), //无法使用该券
    FailReason_CouponForCafeOnly(17), //该券已过期
    FailReason_CouponForCafeOnly_Used(18), //已经使用过
    FailReason_CouponForCafeOnly_Expired(19),//乱码1
    FailReason_NotAvailableCoupon(20),//乱码2
    FailReason_GenderMisMatch(21),//乱码3
    FailReason_GiftNormalItem(22),//这是nexon cash coupon号码
    FailReason_GiftMaplePoint(23), //性别不适合
    FailReason_NoEmptyPos(24), //这种领奖卡是专用道具 不能赠送给别人
    FailReason_ForPremiumUserOnly(25), ////这种领奖卡是专用道具 不能赠送给别人
    FailReason_BuyCoupleStoredProcFailed(26), //确认背包空间不足
    FailReason_BuyFriendshipStoredProcFailed(27), //   这种道具只能在优秀网吧买得到
    FailReason_NotAvailableTime(28), //暂时无法购买网吧道具
    FailReason_NoStock(29), //成对道具只可以送给同世界的异性角色
    FailReason_PurchaseLimitOver(30), //请准确输入收礼人角色名
    FailReason_NoRemainMeso(31), //现在不是销售时间
    FailReason_IncorrectSSN2(32), //卖完了
    FailReason_IncorrectSPW(33),//超过点券购买限额
    FailReason_ForNoPurchaseExpUsersOnly(34),//金币不足
    FailReason_AlreadyApplied(35),//确认身份证
    FailReason_WebShopUnknown(36),//确认二次密码
    FailReason_WebShopInventoryCount(37), //此会员卡只限于新购买现金道具用户使用
    FailReason_WebShopBuyStoredProcFailed(38), //已经报名
    FailReason_WebShopInvalidOrder(39), //未知原因不能进入商城
    FailReason_GachaponLimitOver(40), //未知原因不能进入商城
    FailReason_NoUser(41), //未知原因不能进入商城
    FailReason_WrongCommoditySN(42), //未知原因不能进入商城
    FailReason_CouponLimitError(43), //每日购买上线
    FailReason_CouponLimitError_Hour(44), //未知原因不能进入商城
    FailReason_CouponLimitError_Day(45), //未知原因不能进入商城
    FailReason_CouponLimitError_Week(46),//优惠卷超出最大使用次数
    FailReason_BridgeNotConnected(47), //优惠卷超出最大使用次数 小时
    FailReason_TooYoungToBuy(48),//优惠卷超出最大使用次数 一天
    FailReason_GiftTooYoungToRecv(49), //优惠卷超出最大使用次数 一周
    FailReason_LimitOverTheItem(50), //未知原因不能进入商城
    FailReason_CashLock(51),  //0月中无法购买 请到1月购买
    FailReason_FindSlotPos(52), //设置为不能使用冒险券
    FailReason_GetItem(53), //未知原因不能进入商城
    FailReason_DestroyCashItem(54), //未知原因不能进入商城
    FailReason_NotSaleTerm(55), //未知原因不能进入商城
    FailReason_InvalidCashItem(56),//目前不在出售
    FailReason_InvalidRandomCashItem(57), //未知原因不能进入商城
    FailReason_ReceiveItem(58), //未知原因不能进入商城
    FailReason_UseRandomCashItem(59), //未知原因不能进入商城
    FailReason_NotGameSvr(60), //未知原因不能进入商城
    FailReason_NotShopSvr(61), //未知原因不能进入商城
    FailReason_ItemLockerIsFull(62), //未知原因不能进入商城
    FailReason_NoAndroid(63),//持有的现金道具太多 特殊蓝空出一个未知
    FailReason_DBQueryFailed(64), //未知原因不能进入商城
    FailReason_UserSaveFailed(65), //未知原因不能进入商城
    FailReason_CannotBuyMonthlyOnceItem(66), //未知原因不能进入商城
    //67 直接38
    FailReason_OnlyCashItem(68), //包含只能使用冒险券的道具
    FailReason_NotEnoughMaplePoint(69), //未知原因不能进入商城
    FailReason_TooMuchMaplePointAlready(69),//已经拥有很多抵用券
    FailReason_GiveMaplePointUnknown(70), //未知原因无法增加抵用券
    FailReason_OnWorld(71), //在该世界无法出售
    FailReason_NoRemainToken(72), //冒险岛金元不足
    FailReason_GiftToken(73), //未知原因不能进入商城
    FailReason_LimitOverCharacter(74),//超出该道具的购买限度
    FailReason_CurrentValuePack(75),//还有之前购买的超值礼包，无法购买
    FailReason_NoRemainCashMileage(76),//积分余额不足
    FailReason_NotEquipItem(77), //未知原因不能进入商城
    FailReason_DoNotReceiveCashItemInvFull(78),
    FailReason_DoNotCheckQuest(79), //无法发放奖励道具,请清理现金保管箱,然后重新移动现金道具
    FailReason_SpecialServerUnable(80),//限制购买的道具
    FailReason_BuyWSLimit(81), //在当前世界无法进行
    FailReason_Max_Time_Limit(82), //超过购买限度
    FailReason_RefundExpired(83), //未知
    FailReason_NoRefundItem(84),//目前布莱之机会已中断,请在下次机会中购买
    FailReason_NoRefundPackage(85),//该道具无法赠送
    FailReason_PurchaseItemLimitOver(86),//未知
    FailReason_OTPStateError(87),//超出当月优惠券使用额度
    FailReason_WrongPassword(88),//无法移动该道具
    FailReason_CountOver(89),//未知
    FailReason_Reissuing(90),//当前无法使用
    FailReason_NotExist(91),//未知
    FailReason_NotAvailableLockerTransfer(92),//未知
    FailReason_DormancyAccount(93);//目前无法取消订单
    //94 超过七天的道具无法取消购买
    //95 无法取消订单的道具
    //96 礼包中的部分道具已经领取, 无法取消订单
    //97 超过每月购买限度,无法购买
    //98 OTP状态错误
    //99 一次性密码 U-OTP 不一致
    //100 一次性密码输入错误超出上限
    //101 U-OPT处于发送状态
    //102 U-OPT
    //103 该道具无法移动
    //104 设定了个人信息有效时间制的账号 无法使用冒险券购买
    //105 为保护玩家的冒险券消费,送礼功能已被限制
    //106 为保护玩家的冒险券消费,购买功能已被限制
    //107 当前拥有的冒险券不足0元,无法购买
    //108 未知
    //109 无法取消购买
    //110 收到的礼物道具,无法取消购买
    //111 未知
    //112 当前无法使用礼券
    //113 根据先后顺序,该礼券不可用
    //114 未知原因,礼券使用失败
    //115 不是冒险岛礼券
    //116 请确认礼券有效期
    //117 超出购买上线,无法使用礼券
    //118 已超出结算上线
    //119
    //120 未知
    //122 30级以上才能购买
    //123 70级
    //124 50级
    //125 100级
    //126 100级
    //127 无法使用抵用券购买
    //128 同上
    //129 未知
    //130 未知
    //131 无法使用抵用券购买
    //132 70级以上不能购买
    //135 今天不能继续删除商城道具了
    //136 没有优惠券
    //137 优惠价出错
    //138 现在无法使用优惠券
    //139请再确认领奖卡号码是否正确
    //140 无法领取性别不同
    //141 你手慢了,该道具已经被其他玩家买走
    //142 无法刷新魔法马车


    private int val;

    CashItemType(int val) {
        this.val = val;
    }

    public static CashItemType getRequestTypeByVal(byte type) {
        return Arrays.stream(values()).filter(cit -> cit.toString().startsWith("Req") && cit.getVal() == type).findAny().orElse(null);
    }

    public static CashItemType getResultTypeByVal(byte type) {
        return Arrays.stream(values()).filter(cit -> cit.toString().startsWith("Res") && cit.getVal() == type).findAny().orElse(null);
    }

    public int getVal() {
        return val;
    }
}
