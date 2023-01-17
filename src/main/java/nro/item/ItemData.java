package nro.item;

import nro.main.DataSource;
import nro.part.Part;
import nro.part.PartImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.sql.*;
import java.util.ArrayList;
import nro.main.FileIO;
import nro.main.Util;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ItemData {

    public static ItemOptionTemplate[] iOptionTemplates;
    public static Part[] part;
    public int id;

    public static void loadDataItem() {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/v225/NRitem0")));
            DataInputStream dis = new DataInputStream(is);
            dis.readByte();
            dis.readByte();

//            dis.readByte();
            dis.readUnsignedByte();
            iOptionTemplates = new ItemOptionTemplate[216];
//            int ii = 0;
//            while (dis.available() > 0) {
//                Util.log("ITEM OPTION ID: " + ii + ", NAME: " + dis.readUTF() + ", TYPE: " + dis.readByte());
//                ii++;
//            }
//            for (int i = 0; i < 213; i++) {
//                iOptionTemplates[i] = new ItemOptionTemplate();
//                iOptionTemplates[i].id = i;
//                iOptionTemplates[i].name = dis.readUTF();
//                iOptionTemplates[i].type = (int) dis.readByte();
//                Util.log("ITEM OPTION ID: " + i + ", NAME: " + iOptionTemplates[i].name + ", TYPE: " + iOptionTemplates[i].type);
//            }
            PreparedStatement ps;
            ResultSet rs;
            Connection conn = null;
            try {
                conn = DataSource.getConnection();
                ps = conn.prepareStatement("SELECT * FROM itemoption");
                conn.setAutoCommit(false);
                int ix = 0;
                rs = ps.executeQuery();

                while(rs.next()) {
//                    if(ix >= 213) {
                        iOptionTemplates[ix] = new ItemOptionTemplate();
                        iOptionTemplates[ix].id = rs.getInt("id");
                        iOptionTemplates[ix].name = rs.getString("name");
                        iOptionTemplates[ix].type = rs.getInt("type");
//                    }
                    ix++;
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
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
//
//            Util.log("start: ");
//            is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/v225/NRmap")));
//            dis = new DataInputStream(is);
//            Util.log("ver: " + dis.readByte());
//            int lenghtmap = dis.readUnsignedByte();
//            Util.log("length: " + lenghtmap);
//            for (int i = 0; i < lenghtmap; i++) {
//                Util.log("NAME: " + dis.readUTF());
//            }
//            byte npclenght = dis.readByte();
//            for (byte i = 0; i< npclenght; i++) {
//                Util.log("npc: " + dis.readUTF());
//                Util.log("NAME: " + dis.readShort());
//                Util.log("NAME: " + dis.readShort());
//                Util.log("NAME: " + dis.readShort());
//
//            }

            is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/NRitem1")));
            dis = new DataInputStream(is);
            dis.readByte();
            dis.readByte();
//            dis.readByte();
            int num = (int) dis.readShort();
            for (int j = 0; j < num; j++) {
                ItemTemplate it = new ItemTemplate();
                it.id = j;
                it.type = dis.readByte();
                it.gender = dis.readByte();
                it.name = dis.readUTF();
                it.description = dis.readUTF();
                it.level = dis.readByte();
                it.strRequire = dis.readInt();
                it.iconID = dis.readShort();
                it.part = dis.readShort();
                it.isUpToUp = dis.readBoolean();
//                ItemTemplate.entrys.add(it);
//                Util.log("ItemTemplate: " + j + ", TYPE: " + it.type + ", gender: " + it.gender + ", name: " + it.name + ", des: " + it.description + ", level: " + it.level
//+ ", strRequire: " + it.strRequire + ", iconID: " + it.iconID + ", part: " + it.part + ", isUpToUp: " + it.isUpToUp );
            }
            is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/NRitem100")));
            dis = new DataInputStream(is);
            dis.readByte();
            dis.readByte();
//            dis.readByte();
            int num5 = dis.readShort();
            int[][] array;
            array = new int[num5][];
            
            for (int i = 0; i < array.length; i++) {
                int num6 = dis.readByte();
                array[i] = new int[num6];
                for (int j = 0; j < num6; j++) {
                    array[i][j] = dis.readShort();
//                    Util.log("id " + j + " part " + array[i][j]);
                }
            }

            is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/data/v225/NR_part")));
            dis = new DataInputStream(is);
//            dis.readByte();
//            dis.readByte();
            int number = dis.readShort();
            part = new Part[number];
            for (int i = 0; i < part.length; i++) {
                byte type = dis.readByte();
                part[i] = new Part(type);
                for (int j = 0; j < part[i].pi.length; j++) {
                    part[i].pi[j] = new PartImage();
                    part[i].pi[j].id = dis.readShort();
                    part[i].pi[j].dx = dis.readByte();
                    part[i].pi[j].dy = dis.readByte();
//                    Util.log("PART " +  i + ": ID: " + part[i].pi[j].id + ", dx: " + part[i].pi[j].dx + ", dy: " + part[i].pi[j].dy);
                }
//                if(i >= 1161) {
//                    ItemDAO.insertPartDB(i + 1, type, part[i]);
//                }
            }
            
//            is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/vhalloween/NRitem2")));
            is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/v225/NRitem2")));
            dis = new DataInputStream(is);
            dis.readByte();
            dis.readByte();
//            dis.readByte(); //VER 219 load 2 byte
            int num2 = (int) dis.readShort();
            int num3 = (int) dis.readShort();
//            int k = 1145;
            for (int j = num2; num2 < num3; j++) {
                ItemTemplate it2 = new ItemTemplate();
                it2.id = j;
                it2.type = dis.readByte();
                it2.gender = dis.readByte();
                it2.name = dis.readUTF();
                it2.description = dis.readUTF();
                it2.level = dis.readByte();
                it2.strRequire = dis.readInt();
                it2.iconID = dis.readShort();
                it2.part = dis.readShort();
                it2.isUpToUp = dis.readBoolean();
//                ItemTemplate.entrys.add(it2);
//Util.log("ItemTemplate2: " + j + ", TYPE: " + it2.type + ", gender: " + it2.gender + ", name: " + it2.name + ", des: " + it2.description + ", level: " + it2.level
//+ ", strRequire: " + it2.strRequire + ", iconID: " + it2.iconID + ", part: " + it2.part + ", isUpToUp: " + it2.isUpToUp );
//                if(j >= 1118) {
//                    ItemDAO.insertItemDB(k, it2.type, it2.gender, it2.name, it2.description, it2.level, it2.strRequire, it2.iconID, it2.part);
//                    k++;
//                }
            }
//            Util.log("finish load Part ");
//            is = new ByteArrayInputStream(FileIO.readFile("res/cache/data/NR_part"));
//            dis = new DataInputStream(is);
////            dis.readByte();
////            dis.readByte();
//            int number = dis.readShort();
//            Util.log("finish load Part " + number);
//            part = new Part[number];
//            Util.log("finish load Part " + number);
//            for (int i = 0; i < part.length; i++) {
//                Util.log("finish load Part " + number);
//                byte type = dis.readByte();
//                part[i] = new Part(type);
//                for (int j = 0; j < part[i].pi.length; j++) {
//                    part[i].pi[j] = new PartImage();
//                    part[i].pi[j].id = dis.readShort();
//                    part[i].pi[j].dx = dis.readByte();
//                    part[i].pi[j].dy = dis.readByte();
//                    Util.log("PART " +  i + ": ID: " + part[i].pi[j].id + ", dx: " + part[i].pi[j].dx + ", dy: " + part[i].pi[j].dy);
//                }
//
//            }
//            Util.log("finish load Part");
        } catch (Exception ignored) {
        }
    }

    public static void reWriteCacheData() {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(Objects.requireNonNull(FileIO.readFile("res/cache/v225/NRdata")));
            DataInputStream dis = new DataInputStream(is);

            byte vcData = dis.readByte();
    //        List<Byte> al = new ArrayList<Integer>();
            ArrayList<Byte> bytes = new ArrayList<>();
            bytes.add(vcData);
    //        byte bytes[] = {vcData};
            //TODO NR_dart
            int vcInt = dis.readInt();
            Util.log("INT DART: " + vcInt);
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(vcInt);
            byte[] result = b.array();
            for(int i = 0; i < result.length; i++) {
                bytes.add(result[i]);
            }
            for(int i = 0; i < vcInt; i++) {
                bytes.add(dis.readByte());
            }
            //TODO NR_arrow
            vcInt = dis.readInt();
            Util.log("INT Arrow: " + vcInt);
            b = ByteBuffer.allocate(4);
            b.putInt(vcInt);
            result = b.array();
            for(int i = 0; i < result.length; i++) {
                bytes.add(result[i]);
            }
            for(int i = 0; i < vcInt; i++) {
                bytes.add(dis.readByte());
            }
            //TODO NR_effect
            vcInt = dis.readInt();
            Util.log("INT eff: " + vcInt);
            b = ByteBuffer.allocate(4);
            b.putInt(vcInt);
            result = b.array();
            for(int i = 0; i < result.length; i++) {
                bytes.add(result[i]);
            }
            for(int i = 0; i < vcInt; i++) {
                bytes.add(dis.readByte());
            }
            //TODO NR_image
            vcInt = dis.readInt();
            Util.log("INT image: " + vcInt);
            b = ByteBuffer.allocate(4);
            b.putInt(vcInt);
            result = b.array();
            for(int i = 0; i < result.length; i++) {
                bytes.add(result[i]);
            }
            for(int i = 0; i < vcInt; i++) {
                bytes.add(dis.readByte());
            }
            //TODO NR_part
            vcInt = dis.readInt();
            Util.log("INT part: " + vcInt);
            b = ByteBuffer.allocate(4);
            b.putInt(vcInt);
            result = b.array();
            for(int i = 0; i < result.length; i++) {
                bytes.add(result[i]);
            }
//            Util.log("NUMBER byte item: " + vcInt);
            short numPart = dis.readShort();
            b = ByteBuffer.allocate(2);
            b.putShort(numPart);
            result = b.array();
            for(int i = 0; i < result.length; i++) {
                bytes.add(result[i]);
            }
//            Util.log("NUMBER PART REAL: " + numPart);
            int num1part = 0;
            for (int i = 0; i < numPart; i++) {
                byte type = dis.readByte();

                bytes.add(type);
                if(i == 589 || i == 590 || i == 583 || i == 584 || i == 586 || i == 587) {
                    String list = "";
                    if(i == 589) {
                        list = "[{\"dx\":-13,\"dy\":0,\"id\":5498},{\"dx\":-4,\"dy\":-2,\"id\":5497},{\"dx\":-15,\"dy\":-2,\"id\":5499},{\"dx\":-11,\"dy\":-1,\"id\":5500},{\"dx\":-13,\"dy\":-1,\"id\":5501},{\"dx\":-9,\"dy\":0,\"id\":5502},{\"dx\":-11,\"dy\":0,\"id\":5503},{\"dx\":-7,\"dy\":0,\"id\":5504},{\"dx\":-9,\"dy\":1,\"id\":5505},{\"dx\":-11,\"dy\":0,\"id\":5506},{\"dx\":-9,\"dy\":0,\"id\":5507},{\"dx\":-7,\"dy\":0,\"id\":5508},{\"dx\":-5,\"dy\":-1,\"id\":5509},{\"dx\":-10,\"dy\":0,\"id\":5510},{\"dx\":0,\"dy\":0,\"id\":5511},{\"dx\":-12,\"dy\":0,\"id\":5512},{\"dx\":0,\"dy\":0,\"id\":3001}]";
                    } else if(i == 590) {
                        list = "[{\"dx\":1,\"dy\":0,\"id\":5525},{\"dx\":-1,\"dy\":1,\"id\":5513},{\"dx\":0,\"dy\":0,\"id\":5514},{\"dx\":0,\"dy\":0,\"id\":5515},{\"dx\":0,\"dy\":0,\"id\":5516},{\"dx\":0,\"dy\":0,\"id\":5517},{\"dx\":0,\"dy\":0,\"id\":5524},{\"dx\":0,\"dy\":0,\"id\":5521},{\"dx\":-2,\"dy\":2,\"id\":5522},{\"dx\":0,\"dy\":0,\"id\":5523},{\"dx\":1,\"dy\":-1,\"id\":5519},{\"dx\":0,\"dy\":0,\"id\":5520},{\"dx\":0,\"dy\":0,\"id\":5518},{\"dx\":0,\"dy\":0,\"id\":3002}]";
                    } else if(i == 583) {
//                        list = "[{\"dx\":-13,\"dy\":0,\"id\":4614},{\"dx\":-5,\"dy\":-2,\"id\":4613},{\"dx\":-15,\"dy\":-4,\"id\":4615},{\"dx\":-9,\"dy\":-2,\"id\":4616},{\"dx\":-12,\"dy\":-3,\"id\":4617},{\"dx\":-9,\"dy\":-2,\"id\":4618},{\"dx\":-11,\"dy\":-1,\"id\":4619},{\"dx\":-5,\"dy\":2,\"id\":4620},{\"dx\":-10,\"dy\":1,\"id\":4621},{\"dx\":-11,\"dy\":0,\"id\":4622},{\"dx\":-9,\"dy\":0,\"id\":4623},{\"dx\":-7,\"dy\":0,\"id\":4624},{\"dx\":-6,\"dy\":1,\"id\":4625},{\"dx\":-8,\"dy\":0,\"id\":4626},{\"dx\":0,\"dy\":0,\"id\":4627},{\"dx\":-12,\"dy\":0,\"id\":4628},{\"dx\":0,\"dy\":0,\"id\":3001}]";
                        list = "[{\"dx\":-13,\"dy\":0,\"id\":5435},{\"dx\":-5,\"dy\":-2,\"id\":5434},{\"dx\":-15,\"dy\":-4,\"id\":5436},{\"dx\":-9,\"dy\":-2,\"id\":5437},{\"dx\":-12,\"dy\":-3,\"id\":5438},{\"dx\":-9,\"dy\":-2,\"id\":5439},{\"dx\":-11,\"dy\":-1,\"id\":5440},{\"dx\":-5,\"dy\":2,\"id\":5441},{\"dx\":-10,\"dy\":1,\"id\":5442},{\"dx\":-11,\"dy\":0,\"id\":5443},{\"dx\":-9,\"dy\":0,\"id\":5444},{\"dx\":-7,\"dy\":0,\"id\":5445},{\"dx\":-6,\"dy\":1,\"id\":5446},{\"dx\":-8,\"dy\":0,\"id\":5447},{\"dx\":0,\"dy\":0,\"id\":5448},{\"dx\":-12,\"dy\":0,\"id\":5449},{\"dx\":0,\"dy\":0,\"id\":3001}]";
                    } else if(i == 584) {
                        list = "[{\"dx\":8,\"dy\":4,\"id\":5462},{\"dx\":0,\"dy\":1,\"id\":5450},{\"dx\":0,\"dy\":0,\"id\":5451},{\"dx\":0,\"dy\":0,\"id\":5452},{\"dx\":0,\"dy\":0,\"id\":5453},{\"dx\":0,\"dy\":0,\"id\":5454},{\"dx\":0,\"dy\":0,\"id\":5455},{\"dx\":2,\"dy\":1,\"id\":5459},{\"dx\":-1,\"dy\":1,\"id\":5460},{\"dx\":-2,\"dy\":0,\"id\":5461},{\"dx\":1,\"dy\":-1,\"id\":5457},{\"dx\":0,\"dy\":1,\"id\":5458},{\"dx\":-2,\"dy\":1,\"id\":5456},{\"dx\":0,\"dy\":0,\"id\":3002}]";
//                        list = "[{\"dx\":8,\"dy\":4,\"id\":4663},{\"dx\":0,\"dy\":1,\"id\":4629},{\"dx\":0,\"dy\":0,\"id\":4630},{\"dx\":0,\"dy\":0,\"id\":4631},{\"dx\":0,\"dy\":0,\"id\":4632},{\"dx\":0,\"dy\":0,\"id\":4633},{\"dx\":0,\"dy\":0,\"id\":4634},{\"dx\":2,\"dy\":1,\"id\":4638},{\"dx\":-1,\"dy\":1,\"id\":4639},{\"dx\":-2,\"dy\":0,\"id\":4640},{\"dx\":1,\"dy\":-1,\"id\":4636},{\"dx\":0,\"dy\":1,\"id\":4637},{\"dx\":-2,\"dy\":1,\"id\":4635},{\"dx\":0,\"dy\":0,\"id\":3002}]";
                    } else if(i == 586) {
                        list = "[{\"dx\":-13,\"dy\":0,\"id\":5467},{\"dx\":-6,\"dy\":-2,\"id\":5464},{\"dx\":-15,\"dy\":-2,\"id\":5468},{\"dx\":-10,\"dy\":-1,\"id\":5469},{\"dx\":-12,\"dy\":-1,\"id\":5470},{\"dx\":-8,\"dy\":0,\"id\":5471},{\"dx\":-10,\"dy\":-1,\"id\":5472},{\"dx\":-7,\"dy\":0,\"id\":5473},{\"dx\":-9,\"dy\":0,\"id\":5474},{\"dx\":-10,\"dy\":0,\"id\":5475},{\"dx\":-8,\"dy\":0,\"id\":5476},{\"dx\":-6,\"dy\":0,\"id\":5477},{\"dx\":-6,\"dy\":0,\"id\":5478},{\"dx\":-10,\"dy\":0,\"id\":5479},{\"dx\":0,\"dy\":0,\"id\":5480},{\"dx\":-12,\"dy\":0,\"id\":5481},{\"dx\":0,\"dy\":0,\"id\":3001}]";
                    } else if(i == 587) {
                        list = "[{\"dx\":1,\"dy\":-1,\"id\":5494},{\"dx\":0,\"dy\":1,\"id\":5482},{\"dx\":0,\"dy\":0,\"id\":5483},{\"dx\":0,\"dy\":0,\"id\":5484},{\"dx\":0,\"dy\":0,\"id\":5485},{\"dx\":0,\"dy\":0,\"id\":5486},{\"dx\":0,\"dy\":0,\"id\":5493},{\"dx\":0,\"dy\":0,\"id\":5490},{\"dx\":0,\"dy\":0,\"id\":5491},{\"dx\":0,\"dy\":0,\"id\":5492},{\"dx\":0,\"dy\":0,\"id\":5488},{\"dx\":0,\"dy\":1,\"id\":5489},{\"dx\":0,\"dy\":0,\"id\":5487},{\"dx\":0,\"dy\":0,\"id\":3002}]";
                    }
                    JSONArray jar = (JSONArray) JSONValue.parse(list);
                    JSONObject job2 = null;
                    if (jar != null) {
                        for (int m = 0; m < jar.size(); m++) {
                            dis.readShort();
                            dis.readByte();
                            dis.readByte();
                            job2 = (JSONObject) jar.get(m);
                            short idEdit = Short.parseShort(job2.get("id").toString());
                            byte dxEdit = Byte.parseByte(job2.get("dx").toString());
                            byte dyEdit = Byte.parseByte(job2.get("dy").toString());

                            b = ByteBuffer.allocate(2);
                            b.putShort(idEdit);
                            result = b.array();
                            for(int k = 0; k < result.length; k++) {
                                bytes.add(result[k]);
                            }
                            bytes.add(dxEdit);
                            bytes.add(dyEdit);
                            job2.clear();
                        }
                    }
                } else {
                    if (type == (byte)0) {
                        num1part = 3;
                    } else if (type == (byte)1) {
                        num1part = 17;
                    } else if (type == (byte)2) {
                        num1part = 14;
                    } else if (type == (byte)3) {
                        num1part = 2;
                    }
//                    for (int j = 0; j < num1part; j++) {
//                        Util.log("PART " +  i + ": ID: " + dis.readShort() + ", dx: " + dis.readByte() + ", dy: " + dis.readByte());
//                    }
                    for (int j = 0; j < num1part; j++) {
                        short num1partShort = dis.readShort();
                        b = ByteBuffer.allocate(2);
                        b.putShort(num1partShort);
                        result = b.array();
                        for(int k = 0; k < result.length; k++) {
                            bytes.add(result[k]);
                        }
                        bytes.add(dis.readByte());
                        bytes.add(dis.readByte());
                    }
                }
            }
            Util.log("DONE!!!0: " + bytes.size());
            while(dis.available() > 0) {
                bytes.add(dis.readByte());
            }
            byte[] arr = new byte[bytes.size()];
            for(int i = 0; i < bytes.size(); i++) {
                arr[i] = bytes.get(i);
            }
            Util.log("DONE bytes!!!1: " + bytes.size());
            Util.log("DONE arr!!!1: " + arr.length);
//            FileIO.writeFile("res/cache/NRdataOverWrite", arr);
            Util.log("DONE!!!");
        } catch (Exception e) {
        }
    }
}
