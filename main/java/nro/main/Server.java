package nro.main;

import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import nro.giftcode.GiftCodeManager;
import nro.io.Session;
import nro.clan.ClanManager;
import nro.task.TaskManager;
import nro.card.RadaCardManager;
import nro.item.ItemData;
import nro.map.Map;
import nro.map.MapTemplate;
import nro.player.Boss;
import nro.skill.SkillData;
import nro.skill.NoiTaiTemplate;

import java.util.Timer;
import java.util.TimerTask;
import nro.daihoi.DaiHoiService;


public class Server {
    private static Server instance;
    public static Object LOCK_MYSQL = new Object();
    public static boolean isDebug = false;
    public static MainManager manager;
    public static Menu menu;
    public static Map[] maps;
    public static ByteArrayOutputStream[] cache = new ByteArrayOutputStream[7];
    public static SaveData runTime = new SaveData();
    public int[] idMapBroly = {5, 6, 10, 13, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38};
    public long zoneTimeEndNRSD = 0;
    public boolean openNRSD = false;
    public boolean openHiru = false;
    public boolean openMabu = false;
    //NGOC RONG NAMEC
    public int[] mapNrNamec = {-1,-1,-1,-1,-1,-1,-1};
    public String[] nameNrNamec = {"","","","","","",""};
    public byte[] zoneNrNamec = {-1,-1,-1,-1,-1,-1,-1};
    public String[] pNrNamec = {"","","","","","",""};
    public int[] idpNrNamec = {-1,-1,-1,-1,-1,-1,-1};
    public long timeNrNamec = 0;
    public boolean firstNrNamec = true;
    public long tOpenNrNamec = 0;
    //
    public short cDoanhTrai = 0;
    public short cKhiGas = 0;
    public short maxDoanhTrai = 200;
    public short maxKhiGas = 200;
    public boolean isPassDHVT = true;
    public boolean isCTG = true;
    public int idBossCall = 1000;
    public int timeBaotri = 195;
    //
    public byte isServer = (byte)1;
;

    public int mapKUKU = 0;
    public int khuKUKU = 0;
    public int mapMDD = 0;
    public int khuMDD = 0;
    public int mapRAMBO = 0;
    public int khuRAMBO = 0;

    public int mapTDST = 0;
    public int khuTDST = 0;

    public int khuFide = 0;

    public int map1920 = 0;
    public int khu1920 = 0;

    public int khu151413 = 0;

    public boolean supportNV = false;

    public void init() {
        manager = new MainManager();
        ItemData.loadDataItem();
        manager.loadDataBase();
        menu = new Menu();

        ClanManager.gI().init();
        SkillData.createSkill();
        TaskManager.gI().init();
        RadaCardManager.gI().init();
        GiftCodeManager.gI().init();

        cache[0] = GameScr.loadFile("res/cache/v225/NRdata_new");
        cache[1] = GameScr.loadFile("res/cache/v225/NRmap");
        cache[2] = GameScr.loadFile("res/cache/v225/NRskill");
        cache[3] = GameScr.loadFile("res/cache/v225/NRitem0");
        cache[4] = GameScr.loadFile("res/cache/v225/NRitem1");
        cache[5] = GameScr.loadFile("res/cache/v225/NRitem2");
        cache[6] = GameScr.loadFile("res/cache/v225/NRitem100");
        maps = new Map[MapTemplate.arrTemplate.length];
        short i;

        for (i = 0; i < maps.length; ++i) {
            maps[i] = new Map(MapTemplate.arrTemplate[i]);
            maps[i].start();
        }
        
        NoiTaiTemplate.initNoiTai();
        Util.log("GET LIST NOI TAI XONG");
        //START AUTO SAVE
        runTime.start();

        //TODO BUILD LAI CACHE
//        ItemData.buildNrPart();
//        ItemData.buildCacheCountImage();
//        ItemData.insertVersionImage();
//        ItemData.buildNewNRdata();
    }

    public static Server gI() {
        if (instance == null) {
            instance = new Server();
            instance.init();
        }
        return instance;
    }
    
    public static void main(String[] args) {
        Server.gI().run();
    }

    public void run() {
        ServerSocket listenSocket = null;
        try {
            Util.log("Start server...");
            listenSocket = new ServerSocket(MainManager.port);
            int idBROLY = 1;
            for (int k : idMapBroly) {
                int xBroly = Util.getToaDoXBROLY(k);
                int yBroly = Util.getToaDoYBROLY(k);
                for (int j = 0; j < 2; j++) {
                    Boss _sBroly = new Boss(idBROLY, (byte) 1, (short) xBroly, (short) yBroly);
                    idBROLY++;
                    int _rdZone = maps[k].getIndexMapNoBroly();
                    maps[k].area[_rdZone].bossMap.add(_sBroly);
                    maps[k].area[_rdZone].loadBROLY(_sBroly);
//                    Util.log("MAP NAME: " + maps[idMapBroly[i]].template.name + ", khu: " + _rdZone);
                }
            }
            Util.log("INIT BROLY XONG");
            int _rdZoneCooler = Util.nextInt(0, 5);
            Boss _cooler = new Boss(101, (byte)3, (short)243, (short)168);
            maps[107].area[_rdZoneCooler].bossMap.add(_cooler);
            maps[107].area[_rdZoneCooler].loadBossNoPet(_cooler);
            Util.log("INIT COOLER XONG " + maps[107].template.name + " KHU " + _rdZoneCooler);

            Service.gI().initGranolal();
            Timer timerBLACKXuatHien = new Timer();
            TimerTask ttBLACKXuatHien = new TimerTask() {
                public void run()
                {
                    int idMap = Util.nextInt(91, 93); //index 91 la map 92, index 92 la map 93
                    int IDZONE = Util.nextInt(0, maps[idMap].area.length);
                    short xBlack = 228;
                    if(idMap == 91) {
                        xBlack = 1296;
                    }
                    Boss _rBlack = new Boss(102, (byte)5, xBlack, (short)360);
                    maps[idMap].area[IDZONE].bossMap.add(_rBlack);
                    maps[idMap].area[IDZONE].loadBossNoPet(_rBlack);
                    Util.log("INIT BLACK XONG KHU " + IDZONE);
                    timerBLACKXuatHien.cancel();
                };
            };
            timerBLACKXuatHien.schedule(ttBLACKXuatHien, 30000);

            Timer timerKUKUX = new Timer();
            TimerTask ttKUKU = new TimerTask() {
                public void run()
                {
                    Service.gI().initKuKu();

                    Service.gI().initMAPDAUDINH();

                    Service.gI().initRAMBO();

                    timerKUKUX.cancel();
                };
            };
            timerKUKUX.schedule(ttKUKU, 60000);

            Timer timerTDST = new Timer();
            TimerTask ttTDST = new TimerTask() {
                public void run()
                {
                    Service.gI().initTDST();
                };
            };
            timerTDST.schedule(ttTDST, 10000);

            Timer timerFIDE = new Timer();
            TimerTask ttFIDE = new TimerTask() {
                public void run()
                {
                    int IDZONE = Util.nextInt(1, maps[79].area.length);
                    Boss _rFide = new Boss(130, (byte)15, (short)224, (short)192);
                    maps[79].area[IDZONE].bossMap.add(_rFide);
                    maps[79].area[IDZONE].loadBossNoPet(_rFide);
                    khuFide = IDZONE;
                    Util.log("INIT _rFide XONG MAP " + maps[79].template.name + ", " + IDZONE);
                    //INIT ANDROID 1920
                    Service.gI().initAndroid1920();
                    //INIT ANDROID 151413
                    Service.gI().initAndroid15();
                    //INIT PICPOC
                    Service.gI().initPicPoc();
                    Service.gI().initXenGinder();
                    Service.gI().initXenVoDai();
                    //INIT CHILLED
                    Service.gI().initChilled();
                    Service.gI().initZamasu();
//                    Service.gI().initBillWhis();
                    timerFIDE.cancel();
                };
            };
            timerFIDE.schedule(ttFIDE, 15000);

            Service.gI().initNgocRongSaoDen();
            Service.gI().initMabu12h();
            Service.gI().initHirudegarn();
            DaiHoiService.gI().initDaiHoiVoThuat();
            Service.gI().initNgocRongNamec((byte)0);
            Service.gI().initMapYardrat();
            //INIT SUPPORT NHIEM VU
            Service.gI().supportTDST();

            Util.log("INIT MOB AUTO");
            for(Map _map: maps) {
                if(_map.area[0].mobs.size() > 0 && _map.id != 0 && _map.id != 7 && _map.id != 14 && (_map.id < 53 || _map.id > 62)) {
                    for(int i = 0; i < _map.area.length; i++) {
                        _map.area[i].updateMobAuto();
                    }
                }
            }
            Util.log("INIT MOB AUTO XONG");

            while (true) {
                Socket sc = listenSocket.accept();
//                Util.log("Session connect: " + sc.getPort());
                new Session(sc);
                Thread.sleep(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
