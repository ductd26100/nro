package nro.item;

import org.json.simple.JSONObject;

public class Amulet {
    public int id;
    public long timeEnd;

    public Amulet() {
        this.id = -1;
        this.timeEnd = 0;
    }

    public JSONObject ObjectAmulet() {
        JSONObject put = new JSONObject();
        put.put("id", this.id);
        put.put("point", this.timeEnd);
        return put;
    }
}