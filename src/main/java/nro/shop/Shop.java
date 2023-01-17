package nro.shop;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Shop {
    public int npcID;
    public int idTabShop;
    public ArrayList<TabItemShop> tabShops = new ArrayList<>();
    public static ArrayList<Shop> shops = new ArrayList<>();
    
    @Contract(pure = true)
    public static @Nullable ArrayList<TabItemShop> getTabShop(int npcID, int tabShopID)
    {
        for (Shop shop : shops) {
            if(shop.npcID == npcID && shop.idTabShop == tabShopID)
            {
                return shop.tabShops;
            }
        }
        return null;
    }
    
}
