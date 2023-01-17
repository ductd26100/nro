package nro.main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
//CUSTOM ITEM
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Objects;

import nro.item.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import nro.io.Message;
import nro.io.Session;

import nro.shop.Shop;
import nro.shop.TabItemShop;
import nro.map.Map;
import nro.map.MapData;
import nro.map.MapTemplate;
import nro.map.MobTemplate;
import nro.map.Npc;
import nro.map.WayPoint;
import nro.player.Player;
import nro.part.Part;

public class MainManager {

    public static int port;
    public static String host;
    public static String mysql_host;
    public static int mysql_port;
    public static String mysql_database;
    public static String mysql_user;
    public static String mysql_pass;
    byte vsData;
    byte vsMap;
    byte vsSkill;
    static byte vsItem;
    public static ArrayList<Part> parts;

    public MainManager() {

        this.loadConfigFile();
//        this.loadDataBase();
    }
 
    private void loadConfigFile() {

        String data = Objects.requireNonNull(GameScr.loadFile("nro.conf")).toString();
        HashMap<String, String> configMap = new HashMap<>();
        StringBuilder sbd = new StringBuilder();
        boolean bo = false;

        for (int i = 0; i <= data.length(); ++i) {
            char es;
            if (i != data.length() && (es = data.charAt(i)) != '\n') {
                if (es == '#') {
                    bo = true;
                }

                if (!bo) {
                    sbd.append(es);
                }
            } else {
                bo = false;
                String sbf = sbd.toString().trim();
                if (!sbf.equals("") && sbf.charAt(0) != '#') {
                    int j = sbf.indexOf(58);
                    if (j > 0) {
                        String key = sbf.substring(0, j).trim();
                        String value = sbf.substring(j + 1).trim();
                        configMap.put(key, value);
                        System.out.println("config: " + key + "-" + value);
                    }
                }
                sbd.setLength(0);
            }
        }

        host = configMap.getOrDefault("host", "localhost");

        if (configMap.containsKey("port")) {
            port = Integer.parseInt(configMap.get("port"));
        } else {
            port = 14445;
        }

        mysql_host = configMap.getOrDefault("mysql-host", "localhost");

        if (configMap.containsKey("mysql-port")) {
            mysql_port = Integer.parseInt(configMap.get("mysql-port"));
        } else {
            mysql_port = 3306;
        }

        mysql_user = configMap.getOrDefault("mysql-user", "root");

        mysql_pass = configMap.getOrDefault("mysql-password", "12345678");

        mysql_database = configMap.getOrDefault("mysql-database", "server2");

        if (configMap.containsKey("version-Data")) {
//            this.vsData = 66; //v222
            this.vsData = 3; //v225
        } else {
            this.vsData = 98;
        }

        if (configMap.containsKey("version-Map")) {
//            this.vsMap = 31; //v222
            this.vsMap = 85; //v225
        } else {
            this.vsMap = 23;
        }

        if (configMap.containsKey("version-Skill")) {
//            this.vsSkill = 5; //v222
            this.vsSkill = 36; //v225
        } else {
            this.vsSkill = 2;
        }

        if (configMap.containsKey("version-Item")) {
//            this.vsItem = 119; //vhalloween
            this.vsItem = 29; //225
        } else {
            this.vsItem = 6;
        }
    }

    //load item database
    public void loadDataBase() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;

        int i;
        try {
            conn = DataSource.getConnection();
            JSONArray Option;
            pstmt = conn.prepareStatement("SELECT * FROM `mob`;");
            res = pstmt.executeQuery();

            while (res.next()) {
                MobTemplate md = new MobTemplate();
                md.tempId = Integer.parseInt(res.getString("id"));
                md.name = res.getString("name");
                md.level = Byte.parseByte(res.getString("level"));
                md.Level = Integer.parseInt(res.getString("level"));
                md.maxHp = Integer.parseInt(res.getString("hp"));
                md.rangeMove = Byte.parseByte(res.getString("rangeMove"));
                md.speed = Byte.parseByte(res.getString("speed"));
                MobTemplate.entrys.add(md);
            }
            res.close();

            //load MAP
            i = 0;
            byte j;
            res = conn.prepareStatement("SELECT * FROM `map`;").executeQuery();
            if (res.last()) {
                MapTemplate.arrTemplate = new MapTemplate[res.getRow()];
                res.beforeFirst();
            }

            while (res.next()) {
                MapTemplate mapTemplate = new MapTemplate();
                mapTemplate.id = res.getInt("id");
                mapTemplate.name = res.getString("name");
                mapTemplate.type = res.getByte("type");
                mapTemplate.planetId = res.getByte("planet_id");
                mapTemplate.tileId = res.getByte("tile_id");
                mapTemplate.bgId = res.getByte("bg_id");
                mapTemplate.bgType = res.getByte("bg_type");
                mapTemplate.maxplayers = res.getByte("maxplayer");
                mapTemplate.numarea = res.getByte("numzone");
                mapTemplate.wayPoints = MapData.loadListWayPoint(mapTemplate.id).toArray(new WayPoint[0]);
                JSONArray jar2;
                Option = (JSONArray) JSONValue.parse(res.getString("Mob"));
                mapTemplate.arMobid = new short[Option.size()];
                mapTemplate.arrMoblevel = new int[Option.size()];
                mapTemplate.arrMaxhp = new int[Option.size()];
                mapTemplate.arrMobx = new short[Option.size()];
                mapTemplate.arrMoby = new short[Option.size()];
                short l;
                for (l = 0; l < Option.size(); ++l) {
                    jar2 = (JSONArray) Option.get(l);
                    mapTemplate.arMobid[l] = Short.parseShort(jar2.get(0).toString());
                    mapTemplate.arrMoblevel[l] = Integer.parseInt(jar2.get(1).toString());
                    mapTemplate.arrMaxhp[l] = Integer.parseInt(jar2.get(2).toString());
                    mapTemplate.arrMobx[l] = Short.parseShort(jar2.get(3).toString());
                    mapTemplate.arrMoby[l] = Short.parseShort(jar2.get(4).toString());
                }
                Option = (JSONArray) JSONValue.parse(res.getString("Npc"));
                mapTemplate.npcs = new Npc[Option.size()];
                for (j = 0; j < Option.size(); ++j) {
                    mapTemplate.npcs[j] = new Npc();
                    jar2 = (JSONArray) JSONValue.parse(Option.get(j).toString());
                    Npc npc = mapTemplate.npcs[j];
                    npc.status = Byte.parseByte(jar2.get(0).toString());
                    npc.cx = Short.parseShort(jar2.get(1).toString());
                    npc.cy = Short.parseShort(jar2.get(2).toString());
                    npc.tempId = Integer.parseInt(jar2.get(3).toString());
                    npc.avartar = Integer.parseInt(jar2.get(4).toString());
                }
                MapTemplate.arrTemplate[i] = mapTemplate;
                i++;
            }

            res.close();
            i = 0;
            JSONObject job;
            res = conn.prepareStatement("SELECT * FROM `item`;").executeQuery();
            while (res.next()) {
                ItemTemplate item = new ItemTemplate();
                item.id = Short.parseShort(res.getString("id"));
                item.type = Byte.parseByte(res.getString("type"));
                item.gender = Byte.parseByte(res.getString("gender"));
                item.name = res.getString("name");
                item.description = res.getString("description");
                item.level = Byte.parseByte(res.getString("level"));
                item.strRequire = Integer.parseInt(res.getString("strRequire"));
                item.iconID = Integer.parseInt(res.getString("IconID"));
                item.part = Short.parseShort(res.getString("part"));
                item.type = Byte.parseByte(res.getString("type"));
                item.isUpToUp = Boolean.parseBoolean(res.getString("isUpToUp"));
                Option = (JSONArray) JSONValue.parse(res.getString("ItemOption"));
                if (Option.size() > 0) {
                    for (Object o : Option) {
                        job = (JSONObject) o;
                        item.itemoption.add(new ItemOption(Integer.parseInt(job.get("id").toString()), Integer.parseInt(job.get("param").toString())));
                    }
                } else {
                    item.itemoption.add(new ItemOption(73, 0));
                }

                ItemTemplate.entrys.add(item);
            }

            res.close();

            //load itemShell
            i = 0;
            res = conn.prepareStatement("SELECT * FROM `ItemSell`;").executeQuery();
            while (res.next()) {
                if(res.getByte("pay") == (byte)1) {
                    ItemSell sell = new ItemSell();
                    sell.id = Integer.parseInt(res.getString("item_id"));
                    sell.buyCoin = Integer.parseInt(res.getString("buyCoin"));
                    sell.buyGold = Integer.parseInt(res.getString("buyGold"));
                    sell.buyType = Byte.parseByte(res.getString("buyType"));
                    sell.isNew = res.getBoolean("isNew");
                    Item item = new Item();
                    item.id = sell.id;
                    item.template = ItemTemplate.ItemTemplateID(item.id);
                    item.quantity = Integer.parseInt(res.getString("quantity"));
                    item.quantityTemp = Integer.parseInt(res.getString("quantity"));
                    item.isExpires = Boolean.parseBoolean(res.getString("isExpires"));
                    Option = (JSONArray) JSONValue.parse(res.getString("optionItem"));
                    if (Option.size() > 0) {
                        for (Object o : Option) {
                            JSONObject job2 = (JSONObject) o;
                            item.itemOptions.add(new ItemOption(Integer.parseInt(job2.get("id").toString()), Integer.parseInt(job2.get("param").toString())));
                        }
                    } else {
                        item.itemOptions.add(new ItemOption(73, 0));
                    }
                    sell.item = item;
                    ItemSell.items.add(item);
                    ItemSell.itemCanSell.add(sell);
                    i++;
                }
            }
            res.close();

            //load itemShell
            i = 0;
            res = conn.prepareStatement("SELECT * FROM `itemnotsell`;").executeQuery();
            while (res.next()) {
                Item _item = new Item();
                _item.id = Integer.parseInt(res.getString("item_id"));
                _item.template = ItemTemplate.ItemTemplateID(_item.id);
                _item.quantity = 1;
                _item.quantityTemp = 99;
                _item.isExpires = true;
                Option = (JSONArray) JSONValue.parse(res.getString("optionItem"));
                if (Option.size() > 0) {
                    for (Object o : Option) {
                        JSONObject job2 = (JSONObject) o;
                        _item.itemOptions.add(new ItemOption(Integer.parseInt(job2.get("id").toString()), Integer.parseInt(job2.get("param").toString())));
                    }
                }
                else
                {
                    _item.itemOptions.add(new ItemOption(73, 0));
                }

                ItemSell.itemsNotSell.add(_item);
                i++;
            }
            res.close();

            //load Shops
            i = 0;
            res = conn.prepareStatement("SELECT * FROM `shop`;").executeQuery();
            while (res.next()) {
                Shop shop = new Shop();
                shop.npcID = Integer.parseInt(res.getString("npcID"));
                shop.idTabShop = Integer.parseInt(res.getString("idTabShop"));
                JSONArray tabs = (JSONArray) JSONValue.parse(res.getString("itemSell"));
                for (Object tab : tabs) {
                    TabItemShop tabItemShop = new TabItemShop();
                    JSONObject tabItem = (JSONObject) JSONValue.parse(tab.toString());
                    tabItemShop.tabName = tabItem.get("tabName").toString();
                    JSONArray items = (JSONArray) JSONValue.parse(tabItem.get("items").toString());
                    for (Object item : items) {
                        int itemSellID = Integer.parseInt(item.toString());
                        tabItemShop.itemsSell.add(ItemSell.getItemSellByID(itemSellID));
                    }
                    shop.tabShops.add(tabItemShop);
                }
                Shop.shops.add(shop);
                i++;
            }
            res.close();
            i = 0;
            res = conn.prepareStatement("SELECT * FROM `itemtemp`;").executeQuery();
            while (res.next()) {
                Item item2 = new Item();
                item2.idTemp = res.getInt("id");
                item2.headTemp = res.getShort("head");
                item2.bodyTemp = res.getShort("body");
                item2.legTemp = res.getShort("leg");
                item2.entrys.add(item2);
                i++;
            }
            res.close();
            conn.close();
        } catch (Exception var14) {
            var14.printStackTrace();
            System.exit(0);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void sendDatav2(@NotNull Session session) {
        Message m = null;
        try {
            m = new Message(-87);
            m.writer().write(Server.cache[0].toByteArray());
            m.writer().flush();
            session.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendDatav3(@NotNull Session session) {
        Message m = null;
        try {
            m = new Message(-87);
            m.writer().writeByte((byte)3);
            byte[] cache = FileIO.readFile("res/cache/data/v225/NR_dart");
            m.writer().writeInt(cache.length);
            m.writer().write(cache);
            cache = FileIO.readFile("res/cache/data/v225/NR_arrow");
            m.writer().writeInt(cache.length);
            m.writer().write(cache);
            cache = FileIO.readFile("res/cache/data/v225/NR_effect");
            m.writer().writeInt(cache.length);
            m.writer().write(cache);
            cache = FileIO.readFile("res/cache/data/v225/NR_image");
            m.writer().writeInt(cache.length);
            m.writer().write(cache);
            cache = FileIO.readFile("res/cache/data/v225/NR_part_new");
            m.writer().writeInt(cache.length);
            m.writer().write(cache);
            cache = FileIO.readFile("res/cache/data/v225/NR_skill");
            m.writer().writeInt(cache.length);
            m.writer().write(cache);
            m.writer().flush();
            session.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }


    public static void sendMapv2(@NotNull Session session) {
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().writeByte((byte)6);
            m.writer().write(Server.cache[1].toByteArray());
            m.writer().flush();
            session.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendMapv3(@NotNull Session session) {
        ByteArrayInputStream is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/v225/NRmap")));
        DataInputStream dis = new DataInputStream(is);
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().writeByte((byte)6);
            m.writer().writeByte(dis.readByte());
            int lenghtmap = dis.readUnsignedByte();
            int imap = 168;
            byte b = (byte) imap;
            m.writer().write(Byte.toUnsignedInt(b));
            for (int i = 0; i < lenghtmap; i++) {
                m.writer().writeUTF(dis.readUTF());
            }
            m.writer().writeUTF("Hành tinh Cereal 1");
            m.writer().writeUTF("Hành tinh Cereal 2");

            byte npcLength = dis.readByte();
            m.writer().writeByte((byte)(npcLength + 1));
            byte menuLength = -1;
            byte menuJLength = -1;
            for(byte i = 0; i < npcLength; i++) {
                m.writer().writeUTF(dis.readUTF());
                m.writer().writeShort(dis.readShort());
                m.writer().writeShort(dis.readShort());
                m.writer().writeShort(dis.readShort());
                menuLength = dis.readByte();
                m.writer().writeByte(menuLength);
                for(byte j=0; j < menuLength; j++) {
                    menuJLength = dis.readByte();
                    m.writer().writeByte(menuJLength);
                    for(byte k=0; k < menuJLength; k++) {
                        m.writer().writeUTF(dis.readUTF());
                    }
                }
            }
            m.writer().writeUTF("Monaito");
            m.writer().writeShort((short)1194);
            m.writer().writeShort((short)1195);
            m.writer().writeShort((short)1196);
            m.writer().writeByte((byte)1);
            m.writer().writeByte((byte)1);
            m.writer().writeUTF("Nói chuyện");

            while (dis.available() > 0)
            {
                m.writer().writeByte(dis.readByte());
            }

            m.writer().flush();
            session.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendSkillv2(Session session) {
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().writeByte((byte)7);
            m.writer().write(Server.cache[2].toByteArray());
            m.writer().flush();
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendItemv2(Session session) {
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().writeByte((byte)8);

            m.writer().writeByte((byte)119); //TODO
            m.writer().writeByte((byte)0);
            m.writer().writeByte((byte)216);
            for(ItemOptionTemplate itop: ItemData.iOptionTemplates) {
                m.writer().writeUTF(itop.name);
                m.writer().writeByte((byte)itop.type);
            }
            session.sendMessage(m);
            m.cleanup();

            m = new Message(-28);
            m.writer().writeByte((byte)8);
            m.writer().write(Server.cache[4].toByteArray());
            session.sendMessage(m);
            m.cleanup();

            //TODO NEW
            m = new Message(-28);
            m.writer().writeByte((byte)8);
            m.writer().writeByte(MainManager.vsItem);
            m.writer().writeByte((byte)2);
            m.writer().writeShort((short)800); //start item
            m.writer().writeShort((short)ItemTemplate.entrys.size()); //end item
            for (ItemTemplate itemTemplate: ItemTemplate.entrys) {
                if(itemTemplate.id >= 800) {
                    m.writer().writeByte(itemTemplate.type); //type item
                    m.writer().writeByte(itemTemplate.gender); //gender item
                    m.writer().writeUTF(itemTemplate.name); //name item
                    m.writer().writeUTF(itemTemplate.description); //description item
                    m.writer().writeByte(itemTemplate.level); //level item
                    m.writer().writeInt(itemTemplate.strRequire); //strRequire item
                    m.writer().writeShort(itemTemplate.iconID); //iconID item
                    m.writer().writeShort(itemTemplate.part); //part item
                    m.writer().writeBoolean(itemTemplate.isUpToUp); //isUptoUp item
                }
            }
            session.sendMessage(m);
            m.cleanup();

//            m = new Message(-28);
//            m.writer().writeByte((byte)8);
////            m.writer().write(Server.cache[5].toByteArray());
//            //CUSTOM ITEM
//            ByteArrayInputStream is = new ByteArrayInputStream(FileIO.readFile("res/cache/vhalloween/NRitem2"));
//            DataInputStream dis = new DataInputStream(is);
//            m.writer().writeByte(dis.readByte()); //verItem
//            m.writer().writeByte(dis.readByte()); //type item: 2
//            m.writer().writeShort(dis.readShort()); //start item
//            short endItem = dis.readShort();
//            m.writer().writeShort((short)1145); //enditem v222
//
//            for(short i = (short)800; i < (short)1118; i++) {
//                m.writer().writeByte(dis.readByte()); //type item
//                m.writer().writeByte(dis.readByte()); //gender item
//                m.writer().writeUTF(dis.readUTF()); //name item
//                m.writer().writeUTF(dis.readUTF()); //description item
//                m.writer().writeByte(dis.readByte()); //level item
//                m.writer().writeInt(dis.readInt()); //strRequire item
//                m.writer().writeShort(dis.readShort()); //iconID item
//                m.writer().writeShort(dis.readShort()); //part item
//                m.writer().writeBoolean(dis.readBoolean()); //isUptoUp item
//            }
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Black Goku"); //name item
//            m.writer().writeUTF("Cải trang thành Black Goku"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)5141); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Hóa Đá"); //name item
//            m.writer().writeUTF("Cải trang thành Tượng Đá"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)4392); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Bill"); //name item
//            m.writer().writeUTF("Cải trang thành Thần Hủy Diệt Bill"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)4847); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Champa"); //name item
//            m.writer().writeUTF("Cải trang thành Thần Hủy Diệt Champa"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)4879); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Whis"); //name item
//            m.writer().writeUTF("Cải trang thành Thiên Sứ Whis"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)7679); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Cadic"); //name item
//            m.writer().writeUTF("Cải trang thành Cadic"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)6027); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)5); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Cải trang Nappa"); //name item
//            m.writer().writeUTF("Cải trang thành Nappa"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)15000000); //strRequire item
//            m.writer().writeShort((short)6058); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)27); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Rương cải trang may mắn NOAH"); //name item
//            m.writer().writeUTF("Giấu bên trong nhiều vật phẩm quý giá"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)5007); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            //SET HEART
//            m.writer().writeByte((byte)0); //type item
//            m.writer().writeByte((byte)0); //gender item
//            m.writer().writeUTF("Áo Heart Trái Đất"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6528); //iconID item
//            m.writer().writeShort((short)589); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)1); //type item
//            m.writer().writeByte((byte)0); //gender item
//            m.writer().writeUTF("Quần Heart Trái Đất"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6529); //iconID item
//            m.writer().writeShort((short)590); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)0); //type item
//            m.writer().writeByte((byte)1); //gender item
//            m.writer().writeUTF("Áo Heart Namếc"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6533); //iconID item
//            m.writer().writeShort((short)583); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)1); //type item
//            m.writer().writeByte((byte)1); //gender item
//            m.writer().writeUTF("Quần Heart Namếc"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6534); //iconID item
//            m.writer().writeShort((short)584); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)0); //type item
//            m.writer().writeByte((byte)2); //gender item
//            m.writer().writeUTF("Áo Heart Xayda"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6537); //iconID item
//            m.writer().writeShort((short)586); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)1); //type item
//            m.writer().writeByte((byte)2); //gender item
//            m.writer().writeUTF("Quần Heart Xayda"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6538); //iconID item
//            m.writer().writeShort((short)587); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)4); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Nhẫn Heart"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6532); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)2); //type item
//            m.writer().writeByte((byte)0); //gender item
//            m.writer().writeUTF("Găng Heart Trái Đất"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6530); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)3); //type item
//            m.writer().writeByte((byte)0); //gender item
//            m.writer().writeUTF("Giầy Heart Trái Đất"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6531); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)2); //type item
//            m.writer().writeByte((byte)1); //gender item
//            m.writer().writeUTF("Găng Heart Namếc"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6535); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)3); //type item
//            m.writer().writeByte((byte)1); //gender item
//            m.writer().writeUTF("Giầy Heart Namếc"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6536); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)2); //type item
//            m.writer().writeByte((byte)2); //gender item
//            m.writer().writeUTF("Găng Heart Xayda"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6539); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)3); //type item
//            m.writer().writeByte((byte)2); //gender item
//            m.writer().writeUTF("Giầy Heart Xayda"); //name item
//            m.writer().writeUTF("Trang bị Thần Heart"); //description item
//            m.writer().writeByte((byte)13); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6540); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            // ITEM TICKET
//            m.writer().writeByte((byte)27); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Mảnh Áo Thần"); //name item
//            m.writer().writeUTF("Mảnh ghép Áo Thần Heart"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6523); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)27); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Mảnh Quần Thần"); //name item
//            m.writer().writeUTF("Mảnh ghép Quần Thần Heart"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6524); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)27); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Mảnh Găng Thần"); //name item
//            m.writer().writeUTF("Mảnh ghép Găng Thần Heart"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)1500000); //strRequire item
//            m.writer().writeShort((short)6525); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)13); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Bùa Mabư Mạnh Mẽ"); //name item
//            m.writer().writeUTF("Tăng sức đánh cho đệ tử Mabư"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)0); //strRequire item
//            m.writer().writeShort((short)1404); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)13); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Bùa Mabư Trí Tuệ"); //name item
//            m.writer().writeUTF("Tăng tiềm năng cho đệ tử Mabư"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)0); //strRequire item
//            m.writer().writeShort((short)1403); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
//
//            m.writer().writeByte((byte)13); //type item
//            m.writer().writeByte((byte)3); //gender item
//            m.writer().writeUTF("Bùa Đệ Tử x3"); //name item
//            m.writer().writeUTF("Tăng tiềm năng cho đệ tử"); //description item
//            m.writer().writeByte((byte)1); //level item
//            m.writer().writeInt((int)0); //strRequire item
//            m.writer().writeShort((short)1403); //iconID item
//            m.writer().writeShort((short)(-1)); //part item
//            m.writer().writeBoolean(false); //isUptoUp item
            //END CUSTOM ITEM
//            session.sendMessage(m);
//            m.cleanup();
            
            m = new Message(-28);
            m.writer().writeByte((byte)8);
            m.writer().write(Server.cache[6].toByteArray());
            session.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    @Contract(pure = true)
    public static @Nullable Map getMapid(int id) {
        for (Map map : Server.maps) {
            if (map != null && map.template.id == id) {
                return map;
            }
        }
        return null;
    }

}
