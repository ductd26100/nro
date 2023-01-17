package nro.map;

import nro.item.Item;

public class ItemMap {
    public Item item;
    public int playerId;
    public int itemMapID;
    public short itemTemplateID;
    public short x;
    public short y;
    public short rO;
    public long removedelay = 30000L + System.currentTimeMillis();
    public long timeDrop = 0;
}
