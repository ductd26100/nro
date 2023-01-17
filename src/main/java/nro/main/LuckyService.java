package nro.main;

import nro.constant.Constant;
import nro.io.Message;
import nro.player.Player;
import nro.item.Item;
import nro.item.ItemOption;
import nro.item.ItemSell;

public class LuckyService {
    private static LuckyService instance;

    public static LuckyService gI() {
        if (instance == null) {
            instance = new LuckyService();
        }
        return instance;
    }

    public void loadUILucky(Player p) {
        Message m = null;
        try {
            m = new Message(-127);
            m.writer().writeByte((byte)0);
            m.writer().writeByte((byte)7);
            m.writer().writeShort((short)419);
            m.writer().writeShort((short)420);
            m.writer().writeShort((short)421);
            m.writer().writeShort((short)422);
            m.writer().writeShort((short)423);
            m.writer().writeShort((short)424);
            m.writer().writeShort((short)425);
            m.writer().writeByte((byte)1);
            m.writer().writeInt(4);
            m.writer().writeShort((short)821);
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }

    public void resultLucky(Player p, byte type, byte count) {
        if(type == (byte)2) {
            int id = -1;
            Message m = null;
            try {
                m = new Message(-127);
                m.writer().writeByte((byte)1);
                m.writer().writeByte(count);
                for(byte i = 0; i < count; i++) {
                    id = Util.nextInt(0, Constant.ITEM_LUCKY.length);
                    int iditem = Constant.ITEM_LUCKY[id];
                    if(Util.nextInt(0, 100) == 61) {
                        iditem = Constant.ITEM_BUFF[Util.nextInt(0, 3)];
                    }


                    Item _item = new Item(ItemSell.getItemNotSell(iditem));
                    //1001, 740, 865
                    if(iditem == 190) {
                        _item.quantity = Util.nextInt(1, 51)*1000;
                        _item.itemOptions.add(new ItemOption(171, _item.quantity/1000));
                    } else if(iditem == 1001 || iditem == 740 || iditem == 865) {
                        _item.itemOptions.clear();
                        _item.itemOptions.add(new ItemOption(50, Util.nextInt(6, 14)));

                        if(iditem == 1001) {
                            _item.itemOptions.add(new ItemOption(5, Util.nextInt(6, 14)));
                        } else if(iditem == 740) {
                            if(Util.nextInt(0, 100) < 95) {
                                _item.itemOptions.add(new ItemOption(77, Util.nextInt(6, 11)));
                            } else {
                                _item.itemOptions.add(new ItemOption(77, Util.nextInt(10, 14)));
                            }
                        } else {
                            _item.itemOptions.add(new ItemOption(103, Util.nextInt(6, 14)));
                        }

                        if(Util.nextInt(0, 3500) != 1234) {
                            int day = Util.nextInt(1, 3);
                            _item.itemOptions.add(new ItemOption(93, day));
                            _item.timeHSD = System.currentTimeMillis() + (long)day*86400000;
                        }

                    } else if(itemHasTime(Constant.ITEM_LUCKY[id])) {
                        if(Util.nextInt(0, 100) <= 98) {
                            int day = Util.nextInt(1, 5);
                            _item.itemOptions.add(new ItemOption(93, day));
                            _item.timeHSD = System.currentTimeMillis() + (long)day*86400000;
                        }
                    }
                    p.ItemQuay.add(_item);
                    m.writer().writeShort((short)(_item.template.iconID));
                }
                p.session.sendMessage(m);
                m.cleanup();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(m != null) {
                    m.cleanup();
                }
            }
        }
    }

    public boolean itemHasTime(int id) {
        int[] hasTime = {995,996,997,998,999,1000,1001,1007,1013,1021,1022,1023,1028, 954,955,
            1008,967,944,943,942,936,919,918,917,916,910,909,908,893,892};
        for (int j : hasTime) {
            if (j == id) {
                return true;
            }
        }
        return false;
    }

    public void openItemQuay(Player p) {
        Message m = null;
        try {
            m = new Message(-44);
            m.writer().writeByte((byte)4);
            m.writer().writeByte((byte)1);      
            for (int i = 0; i < 1; i++) {
//                TabItemShop tabItemShop = tabs[i];   
                m.writer().writeUTF("Vật phẩm");
                m.writer().writeByte((byte)(p.ItemQuay.size()));
                for (int j = 0; j < p.ItemQuay.size(); j++) {
                    m.writer().writeShort(p.ItemQuay.get(j).template.id);
                    m.writer().writeUTF(p.ItemQuay.get(j).template.name);

                    m.writer().writeByte((byte)(p.ItemQuay.get(j).itemOptions.size()));
                    
                    for (ItemOption itemOption : p.ItemQuay.get(j).itemOptions) {
                        m.writer().writeByte(itemOption.id);
                        m.writer().writeShort((short)itemOption.param);
                    }
                    //hiển thị new item
                    m.writer().writeByte((byte)0);
                    //xử lý preview cải trang
                    boolean isCT = (p.ItemQuay.get(j).template.type == 5) && p.ItemQuay.get(j).template.checkIsCaiTrang();
//                    m.writer().writeByte((isCT ? 1 : 0));
                    m.writer().writeByte((byte)0);
//                    if (isCT) {
//                         for(Item iad : itemSell.item.entrys){
//                            if(itemSell.item.id == iad.idTemp){
//                                m.writer().writeShort(iad.headTemp);
//                                m.writer().writeShort(iad.bodyTemp);
//                                m.writer().writeShort(iad.legTemp);
//                                m.writer().writeShort(-1);
//                            }
//                        }
//                    }
                }
                m.writer().flush();
                p.session.sendMessage(m);
                m.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
