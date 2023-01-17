package nro.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import nro.io.Message;
import nro.io.Session;
import org.jetbrains.annotations.NotNull;

public class PlayerManger {

    private static PlayerManger instance;

    private ArrayList<Player> players;
    private ArrayList<Player> players2;
    private Timer timer;

    public static  HashMap<String, Long> timeWaitLogin = new HashMap<>();
    public final ArrayList<Session> conns;
//    public final HashMap<Integer, Session> conns_id;
    public HashMap<Integer, Session> list_userId;
//    public final HashMap<Integer, Player> players_id = new HashMap<Integer, Player>();
//    public final HashMap<String, Player> players_uname = new HashMap<String, Player>();
    public PlayerManger() {
        this.players = new ArrayList<>();
        this.players2 = new ArrayList<>();
        this.timer = new Timer();
        this.conns = new ArrayList<>();
//        this.conns_id = new HashMap<>();
        this.list_userId = new HashMap<>();
    }
    
    public void kick(@NotNull Session s){
        //clear all message
        s.clearMessage();
//        if (conns_id.containsKey(s.userId)) {
//            this.conns_id.remove(s.userId);
//        }
        if (conns.contains(s)) {
            this.conns.remove(s);
        }
        if (list_userId.containsKey(s.userId)) {
            this.list_userId.remove(s.userId);
        }
//        if (s.player != null) {
//            if (players_id.containsKey(s.player.id)) {
//                this.players_id.remove(s.player.id);
//            }
            //
//            if (players_uname.containsKey(s.player.name)) {
//                this.players_uname.remove(s.player.name);
//            }
            //
            s.disconnect();
//        }
        s = null;
    }
    
    public static PlayerManger gI(){
        if (instance == null){
            instance = new PlayerManger();
        }
        return instance;
    }

    public synchronized boolean checkUserLogin(@NotNull Session session) {
        if (list_userId.containsKey(session.userId)){
            return true;
        }
        list_userId.put(session.userId, session);
        return false;
    }

    public void disconnectUserLogin(int userId) {
        list_userId.get(userId).disconnect();
        list_userId.remove(userId);
    }

    public Player getPlayerByUserID(int _userID) {
        for (Player player : players) {
            if (player.session.userId == _userID){
                return player;
            }
        }
        return null;
    }
    public Player getPlayerByDetuID(int _detuID) {
        for (Player player : players) {
            if (player.havePet == (byte)1 && player.detu.id == _detuID){
                return player;
            }
        }
        return null;
    }
    public Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.name.equals(name) && player.session != null){
                return player;
            }
        }
        return null;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public ArrayList<Player> getPlayers2() {
        return players2;
    }
    
    public int size(){
        return players.size();
    }
    public void SendMessageServer(Message m) {
        synchronized (conns) {
            for (int i = conns.size()-1; i >= 0; i--)
                if (conns.get(i).player != null)
                    conns.get(i).sendMessage(m);
        }
    }
    public void put(Session conn) {
//        if (!conns_id.containsValue(conn)){
//            conns_id.put(conn.userId, conn);
//        }
        if (!conns.contains(conn)){
            conns.add(conn);
        }
    }
//    public void put(Player p) {
//        if (!players_id.containsKey(p.id)){
//            players_id.put(p.id, p);
//        }
//    }

}
