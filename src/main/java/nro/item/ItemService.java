package nro.item;

import nro.constant.Constant;
import nro.giftcode.GiftCode;
import nro.io.Message;
import nro.main.Service;
import nro.main.Util;
import nro.map.ItemMap;
import nro.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ItemService {
    private static ItemService instance;

    public static ItemService gI() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }

    public void createItemAngel(Player p, Message m, byte size, byte index) {
        try {
            byte index3 = -1;
            byte index4 = -1;
            if (index == (byte)(-1) || size < (byte)2 || size > (byte)4) {
                Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
            }
            Item item = p.ItemBag[index];
            if (item == null || !isFormulaByGender(item.template.id, p.gender)) {
                Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
            }
            if (item.quantity < 1) {
                Service.gI().serverMessage(p.session, "Cần 1 công thức"); return;
            }
            Item item3 = null;
            Item item4 = null;

            byte index2 = m.reader().readByte();
            if (index2 == (byte)(-1)) {
                Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
            }
            Item item2 = p.ItemBag[index2];
            if (item2 == null || !isPieceItemAngle(item2.template.id)) {
                Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
            }
            if (item2.quantity < 999) {
                Service.gI().serverMessage(p.session, "Cần 999 mảnh trang bị"); return;
            }

            if (size >= (byte)3) {
                index3 = m.reader().readByte();
                if (index3 == (byte)(-1)) {
                    Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
                }
                item3 = p.ItemBag[index3];
                if (item3 == null || !isStoneUpgradeAngle(item3.template.id)) {
                    Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
                }
            }
            if (size == (byte)4) {
                index4 = m.reader().readByte();
                if (index4 == (byte)(-1)) {
                    Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
                }
                item4 = p.ItemBag[index4];
                if (item4 == null || !isStoneLuckyAngle(item4.template.id)) {
                    Service.gI().serverMessage(p.session, "Không nghịch ngu"); return;
                }
            }
//            Util.log("SIZE: " + size + ", " + index + ", " + index2 + ", " + index3 + ", " + index4);
            String info = nameItemAngleTarget(item2.template.id, p.gender);
            info += "\b|2|Mảnh ghép " + item2.quantity + "/999";
            int percentSuccess = 35;
            if (item3 != null) {
                percentSuccess += (item3.template.id - 1073)*10;
                info += "\b|2|Đá nâng cấp cấp " + (item3.template.id - 1073) + " (+" + (item3.template.id - 1073) + "0% tỉ lệ thành công)";
            }
            if (item4 != null) {
                info += "\b|2|Đá may mắn cấp " + (item4.template.id - 1078) + " (+" + (item4.template.id - 1078) + "0% tỉ lệ tối đa các chỉ số)";
            }
            info += "\b|2|Tỉ lệ thành công: " + percentSuccess + "%";
            info += "\b|2|Phí nâng cấp: 200 triệu vàng";

            //save item update
            p._itemUpStar = item;
            p._itemUseEpStar = item2;
            p._itemUseEpStar2 = item3;
            p._itemDaBaoVe = item4;

            p._indexUpStar = index;
            p._indexEpStar = index2;
            p._indexEpStar2 = index3;
            p._indexDaBaoVe = index4;

            m = new Message(32);
            m.writer().writeShort((short)56);
            m.writer().writeUTF(info);
            if(p.vang >= 200000000L) {
                m.writer().writeByte(2);
                m.writer().writeUTF("Nâng cấp");
                m.writer().writeUTF("Từ chối");
            } else {
                m.writer().writeByte(1);
                m.writer().writeUTF("Cần 200 Tr\nvàng");
            }

            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void confirmCreateItemAngel(@NotNull Player p) {
        if (p.getBagNull() == 0) {
            p.sendAddchatYellow("Hành trang không đủ chỗ trống!");
            return;
        }
        Item formulaAngle = p._itemUpStar;
        Item pieceAngle = p._itemUseEpStar;
        Item stoneUpgrade = p._itemUseEpStar2;
        Item stoneLucky = p._itemDaBaoVe;

        if(formulaAngle == null || pieceAngle == null || !isFormulaByGender(formulaAngle.template.id, p.gender) || !isPieceItemAngle(pieceAngle.template.id)) {
            return;
        }
        if((System.currentTimeMillis() - p._timeDapDo) >= 1000 && p._checkDapDo) {
            p._timeDapDo = System.currentTimeMillis();
            p._checkDapDo = false;
            int perSuccess = 35;
            int perLucky = 20;
            if (stoneUpgrade != null && p._indexEpStar2 != -1 && isStoneUpgradeAngle(stoneUpgrade.template.id)) {
                perSuccess += (stoneUpgrade.template.id - 1073)*10;
            }
            if (stoneLucky != null && p._indexDaBaoVe != -1 && isStoneLuckyAngle(stoneLucky.template.id)) {
                perLucky += perLucky*(stoneLucky.template.id - 1078)*10/100;
            }
            if(p.ItemBag[p._indexUpStar].quantity >= 1 && p.ItemBag[p._indexEpStar].quantity >= 999 && p.vang >= 200000000L) {
                int idAngle = getIdItemAngleCreate(pieceAngle.template.id, p.gender);
                p.vang -= 200000000L;
                //tru so luong cong thuc
                p.ItemBag[p._indexUpStar].quantity -= 1;
                if (p.ItemBag[p._indexUpStar].quantity <= 0) {
                    p.ItemBag[p._indexUpStar] = null;
                }
                //tru so luong manh trang bi
                p.ItemBag[p._indexEpStar].quantity -= 999;
                if (p.ItemBag[p._indexEpStar].quantity <= 0) {
                    p.ItemBag[p._indexEpStar] = null;
                }
                //tru so luong da nang cap neu co
                if (stoneUpgrade != null && p.ItemBag[p._indexEpStar2].quantity >= 1) {
                    p.ItemBag[p._indexEpStar2].quantity -= 1;
                    if (p.ItemBag[p._indexEpStar2].quantity <= 0) {
                        p.ItemBag[p._indexEpStar2] = null;
                    }
                } else {
                    return;
                }
                //tru so luong da may man neu co
                if (stoneLucky != null && p.ItemBag[p._indexDaBaoVe].quantity >= 1) {
                    p.ItemBag[p._indexDaBaoVe].quantity -= 1;
                    if (p.ItemBag[p._indexDaBaoVe].quantity <= 0) {
                        p.ItemBag[p._indexDaBaoVe] = null;
                    }
                } else {
                    return;
                }
                Service.gI().updateVangNgoc(p);

                if(Util.nextInt(0, 100) < perSuccess) {
                    Item itemAngleTemp = ItemSell.getItemNotSell(idAngle);
                    if (itemAngleTemp == null) {
                        return;
                    }
                    perSuccess = Util.nextInt(0, 50); //luc nay la percent bonus 0 -> 20%
                    if (perSuccess == 49) { perSuccess = 20; }
                    else if(perSuccess == 48 || perSuccess == 47) { perSuccess = 19; }
                    else if(perSuccess == 46 || perSuccess == 45) { perSuccess = 18; }
                    else if(perSuccess == 44 || perSuccess == 43) { perSuccess = 17; }
                    else if(perSuccess == 42 || perSuccess == 41) { perSuccess = 16; }
                    else if(perSuccess == 40 || perSuccess == 39) { perSuccess = 15; }
                    else if(perSuccess == 38 || perSuccess == 37) { perSuccess = 14; }
                    else if(perSuccess == 36 || perSuccess == 35) { perSuccess = 13; }
                    else if(perSuccess == 34 || perSuccess == 33) { perSuccess = 12; }
                    else if(perSuccess == 32 || perSuccess == 31) { perSuccess = 11; }
                    else if(perSuccess == 30 || perSuccess == 29) { perSuccess = 10; }
                    else if(perSuccess <= 28 && perSuccess >= 26) { perSuccess = 9; }
                    else if(perSuccess <= 25 && perSuccess >= 23) { perSuccess = 8; }
                    else if(perSuccess <= 22 && perSuccess >= 20) { perSuccess = 7; }
                    else if(perSuccess <= 19 && perSuccess >= 17) { perSuccess = 6; }
                    else if(perSuccess <= 16 && perSuccess >= 14) { perSuccess = 5; }
                    else if(perSuccess <= 13 && perSuccess >= 11) { perSuccess = 4; }
                    else if(perSuccess <= 10 && perSuccess >= 8) { perSuccess = 3; }
                    else if(perSuccess <= 7 && perSuccess >= 5) { perSuccess = 2; }
                    else if(perSuccess <= 4 && perSuccess >= 2) { perSuccess = 1; }
                    else if(perSuccess <= 1) { perSuccess = 0; }
                    Item itemAngle = new Item(itemAngleTemp);
                    perSuccess += 10;
//                    Util.log("PERSUCCESS: " + perSuccess);
                    if(perSuccess > 0) {
                        for(byte i = 0; i < itemAngle.itemOptions.size(); i++) {
                            if(itemAngle.itemOptions.get(i).id != 21 && itemAngle.itemOptions.get(i).id != 30) {
                                itemAngle.itemOptions.get(i).param += (itemAngle.itemOptions.get(i).param*perSuccess/100);
                            }
                        }
                    }
                    //option bonus
                    perSuccess = Util.nextInt(0, 100);
//                    Util.log("perLucky: " + perLucky);
                    if (perSuccess <= perLucky) {
                        if (perSuccess >= (perLucky - 3)) {
                            perLucky = 3;
                        } else if (perSuccess <= (perLucky - 4) && perSuccess >= (perLucky - 10)) {
                            perLucky = 2;
                        } else { perLucky = 1; }
                        itemAngle.itemOptions.add(new ItemOption(41, perLucky));
                        ArrayList<Integer> listOptionBonus = new ArrayList<>();
                        listOptionBonus.add(42); listOptionBonus.add(43); listOptionBonus.add(44); listOptionBonus.add(45);
                        listOptionBonus.add(46); listOptionBonus.add(197); listOptionBonus.add(198); listOptionBonus.add(199);
                        listOptionBonus.add(200); listOptionBonus.add(201); listOptionBonus.add(202); listOptionBonus.add(203);
                        listOptionBonus.add(204);
                        for (int i = 0; i < perLucky; i++) {
                            perSuccess = Util.nextInt(0, listOptionBonus.size());
                            itemAngle.itemOptions.add(new ItemOption(listOptionBonus.get(perSuccess), Util.nextInt(1, 6)));
                            listOptionBonus.remove(perSuccess);
                        }
                    }
                    p.addItemToBag(itemAngle);

                    Service.gI().sendUpStarSuccess(p);
                } else {
                    Service.gI().sendUpStarError(p);
                }
                Service.gI().updateItemBag(p);
            }
            p._checkDapDo = true;
        }
        //RESET ALL
        Service.gI().resetItemDapDo(p);
    }

    private boolean isFormulaByGender(int id, byte gender) {
        return id == ((int)gender + 1071) || id == ((int)gender + 1084);
    }

    private boolean isPieceItemAngle(int id) {
        return id >= 1066 && id <= 1070;
    }

    private boolean isStoneUpgradeAngle(int id) {
        return id >= 1074 && id <= 1078;
    }

    private boolean isStoneLuckyAngle(int id) {
        return id >= 1079 && id <= 1083;
    }

    private String nameItemAngleTarget(int id, byte gender) {
        String info = "|1|";
        if (id == 1066) { info += "Chế tạo Áo Thiên Sứ"; }
        else if (id == 1067) { info += "Chế tạo Quần Thiên Sứ"; }
        else if (id == 1068) { info += "Chế tạo Giày Thiên Sứ"; }
        else if (id == 1069) { info += "Chế tạo Nhẫn Thiên Sứ"; }
        else if (id == 1070) { info += "Chế tạo Găng Tay Thiên Sứ"; }

        if (gender == (byte)0) { info += " Trái Đất"; }
        else if (gender == (byte)1) { info += " Namếc"; }
        else if (gender == (byte)2) { info += " Xayda"; }

        return info;
    }

    private int getIdItemAngleCreate(int idPiece, byte gender) {
        int idAngle = -1;
        if (idPiece == 1066)
        {
            idAngle = 1048 + (int)gender;
        }
        else if (idPiece == 1067)
        {
            idAngle = 1051 + (int)gender;
        }
        else if (idPiece == 1070)
        {
            idAngle = 1054 + (int)gender;
        }
        else if (idPiece == 1068)
        {
            idAngle = 1057 + (int)gender;
        }
        else if (idPiece == 1069)
        {
            idAngle = 1060 + (int)gender;
        }
        return idAngle;
    }

    //for giftcode
    public void addItemGiftCodeToPlayer(Player p, @NotNull GiftCode giftcode) {
        Set<Integer> keySet = giftcode.detail.keySet();
        StringBuilder textGift = new StringBuilder("Bạn vừa nhận được:\b");
        for (Integer key : keySet) {
            int idItem = key;
            int quantity = giftcode.detail.get(key);

            if(idItem == -1) {
                p.vang = Math.min(p.vang + (long)quantity, Constant.MAX_MONEY);
                textGift.append(quantity).append(" vàng\b");
            } else if(idItem == -2) {
                p.ngoc = Math.min(p.ngoc + quantity, Constant.MAX_GEM);
                textGift.append(quantity).append(" ngọc\b");
            } else if(idItem == -3) {
                p.ngocKhoa = Math.min(p.ngocKhoa + quantity, Constant.MAX_RUBY);
                textGift.append(quantity).append(" ngọc khóa\b");
            } else {
                Item itemGiftTemplate = ItemSell.getItemNotSell(idItem);
                if (itemGiftTemplate != null) {
                    Item itemGift = new Item(itemGiftTemplate);
                    if(itemGift.template.id == 457) {
                        itemGift.itemPay = true;
                    }
                    itemGift.quantity = quantity;
                    p.addItemToBag(itemGift);
                    textGift.append("x").append(quantity).append(" ").append(itemGift.template.name).append("\b");
                }
            }
        }
        Service.gI().updateItemBag(p);
        Service.chatNPC(p, (short)24, textGift.toString());
    }

    //effect chat
    public void sendEffectChat(@NotNull Player p) {
        Timer timeEffectChat = new Timer();
        TimerTask sendEffect = new TimerTask() {
            public void run()
            {
                boolean check = false;
//                System.out.println("ZONE SIZE: " + p.zone.players.size());
                for (Player playerNear: p.zone.players) {
                    if (playerNear.id == p.id) {
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    timeEffectChat.cancel();
                }
                for (Player playerNear: p.zone.players) {
//                    System.out.println("ZONE id: " + playerNear.id);
                    if (playerNear.id != p.id && Math.abs(p.x - playerNear.x) <= 200) {
                        playerNear.zone.chat(playerNear, "Em ấy xinh đẹp quá");
                    }
                }
            };
        };
        timeEffectChat.schedule(sendEffect, 0, 5000);
        p.timerEffectChat = timeEffectChat;
    }

    //TODO NANG CAP ITEM KICH HOAT
    public void upgradeItemActive(@NotNull Player p, byte index, byte index2) {
        Item _item = p.ItemBag[index]; //get item 1
        Item _item2 = p.ItemBag[index2]; //get item 2
        if(_item == null || _item2 == null) {
            return;
        }
        if((_item.isItemActive() && _item2.isItemGod()) || (_item2.isItemActive() && _item.isItemGod())) {
            if(_item.isItemActive() && getIdGodUpItemActive(_item.template.id) == _item2.template.id) {
                p._itemUpStar = _item;
                p._itemUseEpStar = _item2;
                p._indexUpStar = index;
                p._indexEpStar = index2;
            } else if (_item2.isItemActive() && getIdGodUpItemActive(_item2.template.id) == _item.template.id){
                p._itemUpStar = _item2;
                p._itemUseEpStar = _item;
                p._indexUpStar = index2;
                p._indexEpStar = index;
            } else {
                Service.gI().serverMessage(p.session, "Cần trang bị kích hoạt và trang bị thần linh tương ứng để nâng cấp");
                return;
            }

            if (p._itemUpStar.getParamItemByID(215) >= 10) {
                Service.gI().serverMessage(p.session, "Trang bị đã nâng cấp tối đa");
                return;
            }
        } else {
            Service.gI().serverMessage(p.session, "Cần trang bị kích hoạt và trang bị thần linh để nâng cấp");
            return;
        }

        String _info = p._itemUpStar.getInfoUpItemActive(p);
        if(!_info.equals("")) {
            Message m = null;
            try {
                m = new Message(32);
                m.writer().writeShort(21);
                m.writer().writeUTF(_info);
                if (p.vang >= Constant.MAX_MONEY) {
                    m.writer().writeByte(2);
                    m.writer().writeUTF("Nâng cấp\n2 Tỷ vàng");
                    m.writer().writeUTF("Từ chối");
                } else {
                    m.writer().writeByte(1);
                    m.writer().writeUTF("Cần\n2 Tỷ vàng");
                }

                m.writer().flush();
                p.session.sendMessage(m);
                m.cleanup();
            } catch (Exception var2) {
                var2.printStackTrace();
            } finally {
                if (m != null) {
                    m.cleanup();
                }
            }
        }
    }
    //TODO CONFIRM NANG CAP ITEM KICH HOAT
    public void confirmUpgradeItemActive(@NotNull Player p) {
        Item _itemUp = p._itemUpStar;
        Item _stone = p._itemUseEpStar;
        if(_itemUp == null || _stone == null) {
            Service.gI().resetItemDapDo(p);
            return;
        }
//        Util.log("% success: ");

        int levelNow = _itemUp.getParamItemByID(215);
        if(_itemUp.isItemActive() && _stone.isItemGod() && getIdGodUpItemActive(_itemUp.template.id) == _stone.template.id && levelNow < 10) {
            if((System.currentTimeMillis() - p._timeDapDo) >= 1000 && p._checkDapDo) {
                p._timeDapDo = System.currentTimeMillis();
                p._checkDapDo = false;
                if (p.vang >= Constant.MAX_MONEY) {
                    int rdUp = Util.nextInt(0, 120);
                    p.vang = 0;
//                    Util.log("% success: " + rdUp);
//                    Util.log("% success: " + _itemUp.getPercentUpActive(levelNow));
                    if (rdUp <= _itemUp.getPercentUpActive(levelNow)) {
                        //XOA ITEM GOD
                        p.ItemBag[p._indexEpStar] = null;

                        if (levelNow == 0) {
                            p.ItemBag[p._indexUpStar].itemOptions.add(new ItemOption(215, 1));
                        } else {
                            for(int i = 0; i < p.ItemBag[p._indexUpStar].itemOptions.size(); i++) {
                                if(p.ItemBag[p._indexUpStar].itemOptions.get(i).id == 215) {
                                    p.ItemBag[p._indexUpStar].itemOptions.get(i).param = Math.min(10, levelNow + 1);
                                }
                            }
                        }
                        Service.gI().sendUpStarSuccess(p);
                    } else {
                        Service.gI().sendUpStarError(p);
                    }
                    Service.gI().updateVangNgoc(p);
                    Service.gI().updateItemBag(p);
                } else {
                    Service.gI().serverMessage(p.session, "Cần 2 Tỷ vàng để nâng cấp");
                    Service.gI().resetItemDapDo(p);
                    return;
                }
                p._checkDapDo = true;
            }
        } else {
            Service.gI().serverMessage(p.session, "Cần trang bị kích hoạt và trang bị thần linh tương ứng để nâng cấp");
            Service.gI().resetItemDapDo(p);
            return;
        }

        Service.gI().resetItemDapDo(p);
    }
    //TODO MAP ID ITEM KICH HOAT DE LAY ID ITEM THAN LINH TUONG UNG
    public int getIdGodUpItemActive(int id) {
        switch (id) {
            case 0: {
                return 555;
            }
            case 6: {
                return 556;
            }
            case 21: {
                return 562;
            }
            case 27: {
                return 563;
            }
            case 12: {
                return 561;
            }
            case 1: {
                return 557;
            }
            case 7: {
                return 558;
            }
            case 22: {
                return 564;
            }
            case 28: {
                return 565;
            }
            case 2: {
                return 559;
            }
            case 8: {
                return 560;
            }
            case 23: {
                return 566;
            }
            case 29: {
                return 567;
            }
            default:{
                return -1;
            }
        }
    }
}
