package nro.main;

import java.io.File;
import java.sql.*;
import java.util.Scanner;

import nro.constant.Constant;
import nro.item.ItemTemplate;
import nro.map.WayPoint;
import nro.player.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import nro.io.Message;
import org.json.simple.JSONValue;

public class HelperDAO {
    public static @NotNull String getTopPower() {
        StringBuilder sb = new StringBuilder("");

        String SELECT_TOP_POWER = "SELECT name, power FROM player ORDER BY power DESC LIMIT " + Constant.MAX_TOP_POWER;
        PreparedStatement ps;
        ResultSet rs;
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            ps = conn.prepareStatement(SELECT_TOP_POWER);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
            while(rs.next()) {
                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("power")).append("\b");
                i++;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static @NotNull String getTopCard() {
        StringBuilder sb = new StringBuilder("");

        String SELECT_TOP_CARD = "SELECT player.name, account.tongnap FROM player INNER JOIN account ON player.account_id = account.id WHERE account.tongnap > 0 ORDER BY account.tongnap DESC LIMIT " + Constant.MAX_TOP_CARD;
        PreparedStatement ps;
        ResultSet rs;
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            ps = conn.prepareStatement(SELECT_TOP_CARD);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
            while(rs.next()) {
                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("tongnap")).append("\b");
                i++;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static boolean payGoldDB(Player p, int gold) {
        String SELECT_GOLD = "SELECT gold FROM account WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps;
        ResultSet rs;
        boolean check = true;
        try {
            conn = DataSource.getConnection();
            ps = conn.prepareStatement(SELECT_GOLD);
            ps.setInt(1, p.id);
            conn.setAutoCommit(false);
            rs = ps.executeQuery();
            if (rs.next()) {
                int goldReal = rs.getInt("gold");
                if(goldReal >= gold) {
                    String UPDATE_GOLD = "UPDATE account SET gold=? WHERE id=?";
                    ps = conn.prepareStatement(UPDATE_GOLD);
                    ps.setInt(1, goldReal - gold);
                    ps.setInt(2, p.id);
                    ps.executeUpdate();
                    conn.commit();
                    ps.close();

                    p.goldStone = goldReal - gold;
                    check = true;
                } else {
                    check = false;
                }
            }
            conn.close();
        } catch (Exception e) {
            check = false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return check;
    }

    public static boolean updateEvent(Player p) {
//        Util.log("A");
        String UPDATE_GIFT = "UPDATE account SET isOn = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps;
        boolean check = true;
        try {
            conn = DataSource.getConnection();
            ps = conn.prepareStatement(UPDATE_GIFT);
            ps.setInt(1, p.id);
//            conn.setAutoCommit(false);
            ps.executeUpdate();
            conn.commit();
            ps.close();
            conn.close();
        } catch (Exception e) {
            check = false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return check;
    }

    public static void updateTransactionGold(Player p, Player partner, int goldIn, int goldOut) {
        Connection conn = null;
        String UPDATE_GIFT = "INSERT INTO transaction (account_id, name, gold_in, gold_out, partner_id, partner_name) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps;
        try {
            conn = DataSource.getConnection();
//            conn.setAutoCommit(false);
            ps = conn.prepareStatement(UPDATE_GIFT);
            ps.setInt(1, p.id);
            ps.setString(2, p.name);
            ps.setInt(3, goldIn);
            ps.setInt(4, goldOut);
            ps.setInt(5, partner.id);
            ps.setString(6, partner.name);

            ps.executeUpdate();

            ps = conn.prepareStatement(UPDATE_GIFT);
            ps.setInt(1, partner.id);
            ps.setString(2, partner.name);
            ps.setInt(3, goldOut);
            ps.setInt(4, goldIn);
            ps.setInt(5, p.id);
            ps.setString(6, p.name);

            ps.executeUpdate();
            conn.commit();
            ps.close();
            conn.close();
        } catch (Exception ignored) {
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cancelTrace(@NotNull Player player) {
        if (player._friendGiaoDich != null && player._friendGiaoDich._friendGiaoDich != null) { //HUY GIAO DICH
            Message m = new Message();
            //SEND TAT UI GIAO DICH
            //ADD ITEM TO ME
//            for (int i = 0; i < player._indexGiaoDich.size(); i++) {
//                byte indexME = player.getIndexBagNotItem();
////                                Item _item = new Item(item);
////                                _item.quantity += (item.quantity - 1);
//                if (indexME != (byte) (-1)) {
//                    player.ItemBag[indexME] = player._itemGiaoDich.get(i);
//                }
//            }
            //ADD ITEM TO FRIEND
//            for (int i = 0; i < player._friendGiaoDich._indexGiaoDich.size(); i++) {
//                byte indexFRIEND = player._friendGiaoDich.getIndexBagNotItem();
////                                Item _item = new Item(item);
////                                _item.quantity += (item.quantity - 1);
//                if (indexFRIEND != (byte) (-1)) {
//                    player._friendGiaoDich.ItemBag[indexFRIEND] = player._friendGiaoDich._itemGiaoDich.get(i);
//                }
//            }
//            if (player._goldGiaoDich > 0) {
//                player.vang = Math.min((player.vang + (long) player._goldGiaoDich), 2000000000L);
//            }
//            if (player._friendGiaoDich._goldGiaoDich > 0) {
//                player._friendGiaoDich.vang = Math.min((player._friendGiaoDich.vang + (long) player._friendGiaoDich._goldGiaoDich), 2000000000L);
//            }
            //SEND HUY GIAO DICH
            //SEND TAT UI GIAO DICH
            try {
                m = new Message(-86);
                m.writer().writeByte(7);
                m.writer().flush();
                player.session.sendMessage(m);
                player._friendGiaoDich.session.sendMessage(m);
                m.cleanup();
            } catch (Exception var2) {
                var2.printStackTrace();
            } finally {
                m.cleanup();
            }
            //UPDATE BAG CHO TOI
//            Service.gI().updateItemBag(player);
//            Service.gI().buyDone(player);
            //UPDATE BAG CHO FRIEND
//            Service.gI().updateItemBag(player._friendGiaoDich);
//            Service.gI().buyDone(player._friendGiaoDich);
            try {
                m = new Message(-86);
                m.writer().writeByte(7);
                m.writer().flush();
                player.session.sendMessage(m);
                player._friendGiaoDich.session.sendMessage(m);
                m.cleanup();
            } catch (Exception var2) {
                var2.printStackTrace();
            } finally {
                m.cleanup();
            }

            player.sendAddchatYellow("Giao dịch đã bị hủy");
            player._friendGiaoDich.sendAddchatYellow("Giao dịch đã bị hủy");

            player._isGiaoDich = false;
            player._friendGiaoDich._isGiaoDich = false;
            //CLEAR VARIABLE SAU KHI GIAO DICH XONG CHO FRIEND
            player._friendGiaoDich._indexGiaoDich.clear();
            player._friendGiaoDich._itemGiaoDich.clear();
            player._friendGiaoDich._friendGiaoDich = null;
            player._friendGiaoDich._confirmGiaoDich = false;
            player._friendGiaoDich._goldGiaoDich = 0;
            player._friendGiaoDich._isGiaoDich = false;
            //CLEAR VARIABLE SAU KHI GIAO DICH XONG CHO TOI
            player._indexGiaoDich.clear();
            player._itemGiaoDich.clear();
            player._friendGiaoDich = null;
            player._confirmGiaoDich = false;
            player._goldGiaoDich = 0;
            player._isGiaoDich = false;
        }
    }

    public static boolean isCmdLockTran(byte cmd) {
        return cmd == (byte)(-81) || cmd == (byte)(-46) || cmd == (byte)(-43) || cmd == (byte)(-40) || cmd == (byte)(-20)
                || cmd == (byte)(6) || cmd == (byte)(7) || cmd == (byte)(32) || cmd == (byte)(33) || cmd == (byte)(-34)
                || cmd == (byte)(112);
    }

    public static boolean isItemLungLock(int id) {
        return  (id >= 467 && id <= 471) || id == 741 || id == 745 || (id >= 800 && id <= 805) || (id >= 814 && id <= 817)
                || id == 822 || id == 823 || id == 852 || id == 954 || id == 955 || id == 966 || id == 982 || id == 983
                || (id >= 994 && id <= 1000) || id == 1007 || id == 1013 || (id >= 1021 && id <= 1028) || id == 1030 || id == 1031
                || id == 1047 || id == 1100 || (id >= 1108 && id <= 1112);
    }

    public static void countVang() {

        new Thread(new Runnable() {
            @Override
            public void run() {countGold(5500, 5520);}
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                countGold(5521, 5540);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                countGold(5541, 5560);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                countGold(5561, 5580);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                countGold(5581, 5600);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {countGold(5700, 5720);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5721, 5740);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5741, 5760);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5761, 5780);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5781, 5800);}
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {countGold(5900, 5920);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5921, 5940);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5941, 5960);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5961, 5980);}
        }).start();new Thread(new Runnable() {
            @Override
            public void run() {countGold(5981, 6000);}
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6100, 6120);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6121, 6140);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6141, 6160);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6161, 6180);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6181, 6200);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6300, 6320);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6321, 6340);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6341, 6360);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6361, 6380);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6381, 6400);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6500, 6520);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6521, 6540);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6541, 6560);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6561, 6580);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6581, 6600);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6700, 6720);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6721, 6740);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6741, 6760);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6761, 6780);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6781, 6800);}
//        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6900, 6920);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6921, 6940);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6941, 6960);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6961, 6980);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(6981, 7000);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7100, 7120);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7121, 7140);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7141, 7160);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7161, 7180);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7181, 7200);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7300, 7320);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7321, 7340);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7341, 7360);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7361, 7380);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7381, 7400);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7500, 7520);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7521, 7540);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7541, 7560);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7561, 7580);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7581, 7600);}
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7700, 7720);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7721, 7740);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7741, 7760);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7761, 7780);}
//        }).start();new Thread(new Runnable() {
//            @Override
//            public void run() {countGold(7781, 7800);}
//        }).start();
    }
    public static void countGold(int start, int end) {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player WHERE id > ? AND id <= ?");
            ps.setInt(1, start);
            ps.setInt(2, end);
            ResultSet rs = ps.executeQuery();
            Util.log("start: " + start + ", end : " + end);
            while (rs.next()) {
                int count = 0;
                int account_id = rs.getInt("account_id");
                JSONObject job2 = null;
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("itembox"));
                int j, id, quantity;
                if (jar != null) {
                    for (j = 0; j < jar.size(); ++j) {
                        job2 = (JSONObject) jar.get(j);
                        id = Integer.parseInt(job2.get("id").toString());
                        quantity = Integer.parseInt(job2.get("quantity").toString());
                        if(id == 457) {
                            count += quantity;
                        }
                        job2.clear();
                    }
                }

                jar = (JSONArray) JSONValue.parse(rs.getString("itembag"));
                if (jar != null) {
                    for (j = 0; j < jar.size(); ++j) {
                        job2 = (JSONObject) jar.get(j);
                        id = Integer.parseInt(job2.get("id").toString());
                        quantity = Integer.parseInt(job2.get("quantity").toString());
                        if(id == 457) {
                            count += quantity;
                        }
                        job2.clear();
                    }
                }

                ps = conn.prepareStatement("UPDATE player SET count_gold = ? WHERE account_id = ?");
                ps.setInt(1, count);
                ps.setInt(2, account_id);
                ps.executeUpdate();
                conn.commit();
            }
            Util.log("done: " + start + ", end : " + end);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
