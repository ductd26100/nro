package nro.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import nro.main.HelperDAO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ItemTemplate {

    public int id;

    public byte type;

    public byte gender;

    public String name;

    public String description;
    
    public byte level;
    
    public long expires = 0;
    
    public int iconID;

    public short part;

    public boolean isUpToUp;

    public int strRequire;
    public ArrayList<ItemOption> itemoption = new ArrayList<>();
    public static ArrayList<ItemTemplate> entrys = new ArrayList<>();

    private static final HashMap<Integer, ItemOptionTemplate> options = new HashMap<>();

    public static void put(int id, ItemOptionTemplate option) {
        ItemTemplate.options.put(id, option);
    }
    @Contract(pure = true)
    public static @NotNull Collection<ItemOptionTemplate> getOptions() {
        return ItemTemplate.options.values();
    }

    public static @NotNull JSONObject ObjectItem(@NotNull Item item, int index) {
        JSONObject put = new JSONObject();
        put.put((Object)"index", (Object)index);
        put.put((Object)"id", (Object)item.id);
        put.put((Object)"quantity", (Object)item.quantity);
        put.put((Object)"itempay", (Object)(item.itemPay ? 1 : 0));
        if(item.timeHSD > 0) {
            put.put((Object)"expire", (Object)item.timeHSD);
        }
        JSONArray option = new JSONArray();
        for (ItemOption Option : item.itemOptions) {
            JSONObject pa = new JSONObject();
            pa.put((Object)"id", (Object)Option.id);
            if(Option.id == 93) {
                pa.put((Object)"param", (Object)(int)((item.timeHSD - System.currentTimeMillis())/86400000));
            } else {
                pa.put((Object)"param", (Object)Option.param);
            }
            option.add((Object)pa);
        }
        put.put((Object)"option", (Object)option);
        return put;
    }
    
    public static @Nullable Item parseItem(String str) {
        Item item = new Item();
        JSONObject job = (JSONObject) JSONValue.parse(str);
        item.id = Short.parseShort(job.get((Object)"id").toString());
        item.quantity = Short.parseShort(job.get((Object)"quantity").toString());
        if(job.get((Object)"itempay") != null) {
            item.itemPay = Integer.parseInt(job.get((Object)"itempay").toString()) == 1;
        }
        if((item.id == 457 && !item.itemPay) || (HelperDAO.isItemLungLock(item.id))) {
            return null;
        }
        if(job.get((Object)"expire") != null) {
            item.timeHSD = Long.parseLong(job.get((Object)"expire").toString());
            if(item.timeHSD < System.currentTimeMillis() + 1800000) {
                return null;
            }
        }
        item.template = ItemTemplate.ItemTemplateID(item.id);
        JSONArray Option = (JSONArray)JSONValue.parse(job.get((Object)"option").toString());
        for (Object Option2 : Option) {
            JSONObject job2 = (JSONObject)Option2;
            if(Integer.parseInt(job2.get((Object)"id").toString()) < 0 || Integer.parseInt(job2.get((Object)"id").toString()) > 215) {
                return null;
            }
            ItemOption option = new ItemOption(Integer.parseInt(job2.get((Object)"id").toString()), Integer.parseInt(job2.get((Object)"param").toString()));
            if(Integer.parseInt(job2.get((Object)"id").toString()) == 9 && item.template.type == (byte)32) {
                int param9 = Integer.parseInt(job2.get((Object)"param").toString());
                item.timeGLT = (long)(param9* 60000L);
                if(((item.id == 531 || item.id == 536) && param9 >= 10000) || ((item.id == 530 || item.id == 535) && param9 >= 1000) || ((item.id == 529 || item.id == 534) && param9 >= 100)) {
                    item.maxTimeGLT = true;
                }
            }
            if(Integer.parseInt(job2.get((Object)"id").toString()) == 93 && item.timeHSD > System.currentTimeMillis()) {
                option.param = (int)((item.timeHSD - System.currentTimeMillis())/86400000);
                item.itemOptions.add(option);
            } else {
                item.itemOptions.add(option);
            }
        }
        return item;
    }
    @Contract(pure = true)
    public static @Nullable ItemTemplate ItemTemplateID(int id) {
        for (ItemTemplate entry : entrys) {
            if(entry.id == id)
            {
                return entry;
            }
        }
        return null;
    }
  
    public boolean checkIsCaiTrang() {
        return this.type != (byte) 5 || (this.level != (byte) 0 && this.level != (byte) 15 && (this.level != (byte) 1 || this.part == (short) (-1)));
    }
}
