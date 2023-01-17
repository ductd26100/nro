package nro.task;

import java.io.IOException;
import java.util.TimerTask;
import java.util.Timer;

import nro.constant.Constant;
import nro.io.Message;
import nro.player.Player;
import nro.player.Boss;
import nro.player.Detu;
import nro.player.PlayerManger;
import nro.map.Mob;
import nro.skill.Skill;
import java.util.ArrayList;
import nro.main.Util;
import nro.item.Item;
import nro.item.ItemSell;
import nro.item.ItemTemplate;
import nro.map.ItemMap;
import nro.main.Service;
import nro.main.Server;

public class DelayUseBomTask extends TimerTask {
    public ArrayList<Mob> mobs = new ArrayList<>();
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Boss> bosses = new ArrayList<>();
    public ArrayList<Detu> petss = new ArrayList<>();
    public ArrayList<ItemMap> itemsMap = new ArrayList<>();
    public Player player;
    public Skill skill;

    public DelayUseBomTask(ArrayList<Player> listPlayer, ArrayList<Mob> listMob, ArrayList<ItemMap> iItemMap, Player iplayer, Skill iskill, ArrayList<Boss> listBoss, ArrayList<Detu> listDetu) {
        this.players = listPlayer;
        this.mobs = listMob;
        this.player = iplayer;
        this.skill = iskill;
        this.itemsMap = iItemMap;
        this.bosses = listBoss;
        this.petss = listDetu;
    }

    @Override
    public void run() {
        if(this.player.isdie) {
            this.cancel();
        } else {
            Message m = null;
//            Util.log("% skill: " + Util.getPercentDouble((int)skill.damage));
//            Util.log("hp xd: " + player.hp);
            int dameBoom = (int)(player.hp * Util.getPercentDouble((int)skill.damage));
//            Util.log("dame xd: " + dameBoom);
            if (player.isMonkey) {
                dameBoom = (int) (dameBoom / 3);
            } else {
                dameBoom = (int) (dameBoom / 2);
            }
//            Util.log("dame xd: " + dameBoom);

            player.mp -= (int)(player.mp*0.5);

            try {
                m = new Message(-45);
                m.writer().writeByte(7);
                m.writer().writeInt(player.id); // id player use    
                m.writer().writeShort(skill.skillId); // b91 gui cho co
                m.writer().writeShort(0); //    seconds
                m.writer().flush();
                for(Player p: players) {
                    p.session.sendMessage(m);
                }

                Timer timerDie = new Timer();
                TimerTask tt = new TimerTask() {
                    public void run()
                    {
                        player.hp = 0;
                        player.isdie = true;

                        for(Player _pll: players) {
                            if(_pll.id == player.id) {
                                if(player.isMonkey) {
                                    Service.gI().loadCaiTrangTemp(player);
                                    player.isMonkey = false;
                                    //NOI TAI TANG DAME KHI HOA KHI
                                    if(player.upDameAfterKhi && player.noiTai.id != 0 && player.noiTai.idSkill == (byte)13) {
                                        player.upDameAfterKhi = false;
                                    }
                                    //NOI TAI TANG DAME KHI HOA KHI
                                    Service.gI().loadPoint(player.session, player);
                                }
                                player.zone.sendDieToMe(player);
                            } else {
                                _pll.sendDefaultTransformToPlayer(player);
                                player.zone.sendDieToAnotherPlayer(_pll, player);
                            }
                        }
                        //NEU DEO NGOC RONG SAO DEN THI ROT RA DAT
                        Service.gI().dropDragonBall(player);
                    };
                };
                timerDie.schedule(tt, 500);


                for(Mob mob: mobs) {
                    if (Math.abs(player.x - mob.pointX) < skill.dx && Math.abs(player.y - mob.pointY) < skill.dy && !mob.isDie) {
                            mob.updateHP(-dameBoom);

                            if(mob.isDie) {
                                ArrayList<ItemMap> itemDrop = new ArrayList<>();
                                //CHECK INIT BOSS MAP KHI GAS
                                Service.gI().initLychee(player);
                                if(mob.template.tempId != 0) {
//                                    int percentDrop = Util.nextInt(0, 10);
//                                    if(percentDrop < 3) {
//                                        int id = Util.nextInt(0, Constant.ITEM_DROP.length - 2);
//
//                                        ItemMap itemM = player.zone.cNewItemMap(Constant.ITEM_DROP[id], player.id, mob.pointX, mob.pointY);
//                                        if(itemM != null) {
//                                            player.zone.addItemToMap(itemM, player.id, mob.pointX, mob.pointY);
//                                            //CHECK BUA THU HUT
//                                            if(player.getBuaThuHut()) {
//                                                try {
//                                                    player.zone.PickItemDrop(player, (short)itemM.itemMapID);
//                                                } catch (IOException e) {
//                                                    throw new RuntimeException(e);
//                                                }
//                                            }
//                                        }

                                        //ITEM DROP RA MAP
                                        m = new Message(-12);
                                        m.writer().writeByte(mob.tempId);
                                        m.writer().writeInt(mob.hp);
                                        m.writer().writeBoolean(false);
                                        m.writer().writeByte(0);
//                                        m.writer().writeByte(1);
//                                        m.writer().writeShort(item.itemMapID);
//                                        m.writer().writeShort(item.item.template.id);
//                                        m.writer().writeShort(mob.pointX);
//                                        m.writer().writeShort(mob.pointY);
//                                        m.writer().writeInt(player.id);
                                        m.writer().flush();
                                        for(Player pll: players) {
                                            pll.session.sendMessage(m);
                                        }
                                        m.cleanup();
//                                    } else {
                                        //ITEM DROP RA MAP
//                                        m = new Message(-12);
//                                        m.writer().writeByte(mob.tempId);
//                                        m.writer().writeInt(mob.hp);
//                                        m.writer().writeBoolean(false);
//                                        m.writer().writeByte(0); //so luong item
//                                        m.writer().flush();
//                                        for(Player pll: players) {
//                                            pll.session.sendMessage(m);
//                                        }
//                                        m.cleanup();
//                                    }
                                    //check TRUNG MABU
                                    if(mob.template.tempId == 70 && mob.typeHiru == (byte)2) {
                                        int rdMabu = Util.nextInt(0, 5);
                                        if(rdMabu < 1) {
                                            player.hasTrungMabu = true;
                                            player.sendAddchatYellow("Bạn vừa nhận được đệ tử Mabư, quay về nhà gặp Ông Già để thao tác");
                                        }
                                    }
                                } else {
                                    //ITEM DROP RA MAP
                                    m = new Message(-12);
                                    m.writer().writeByte(mob.tempId);
                                    m.writer().writeInt(mob.hp);
                                    m.writer().writeBoolean(false);
                                    m.writer().writeByte(0); //so luong item
                                    m.writer().flush();
                                    for(Player pll: players) {
                                        pll.session.sendMessage(m);
                                    }
                                    m.cleanup();
                                }
                            }
                            else {
    //                             try {
                                    m = new Message(-9);
                                    m.writer().writeByte(mob.tempId);
                                    m.writer().writeInt(mob.hp);
                                    m.writer().writeInt(dameBoom);
                                    m.writer().writeBoolean(false);//flag
                                    //eff boss
                                    //5 khói
                                    m.writer().writeByte(-1);
                                    m.writer().flush();
                                    for(Player pll: players) {
                                        pll.session.sendMessage(m);
                                    }
                                    m.cleanup();
    //                            } catch (Exception e) {
    //                                e.printStackTrace();
    //                            }
                            }

                    }
                }

                if(bosses.size() > 0) {
                    for (Boss boss : bosses) {
                        if (Math.abs(player.x - boss.x) < skill.dx && Math.abs(player.y - boss.y) < skill.dy && !boss.isdie && Service.gI().checkCanAttackBoss(boss)) {

                            boss.hp -= dameBoom;
                            if (boss.hp <= 0) {
                                boss.isdie = true;
                                boss.isTTNL = false;
                                boss.hp = 0;
                            }
                            if (boss.isdie && boss.typePk == (byte) 5) {
                                //SET LAI TYPE PK CUA BOSS KHI BOSS CHET
//                                bosses.get(i).typePk = 1;
                                //send dame
                                player.zone.dameChar(boss.id, boss.hp, dameBoom, false);
                                //REMOVE ALL KHONG CHE KHI BOSS CHET
                                boss.removePlayerKhongChe();

                                if (boss._typeBoss != 1 && boss._typeBoss != 2) { //Broly khong roi do
                                    ArrayList<ItemMap> itemDrops = new ArrayList<>();
                                    if (boss._typeBoss == 3 || boss._typeBoss == 5 || (boss._typeBoss >= (byte) 7 && boss._typeBoss <= (byte) 30 && boss._typeBoss != (byte) 29)) {
                                        if (boss._typeBoss == (byte) 7) {
                                            Server.gI().mapKUKU = 0;
                                            Server.gI().khuKUKU = 0;
                                        } else if (boss._typeBoss == (byte) 8) {
                                            Server.gI().mapMDD = 0;
                                            Server.gI().khuMDD = 0;
                                        } else if (boss._typeBoss == (byte) 9) {
                                            Server.gI().mapRAMBO = 0;
                                            Server.gI().khuRAMBO = 0;
                                        } else if (boss._typeBoss == (byte) 14) {
                                            Server.gI().mapTDST = 0;
                                            Server.gI().khuTDST = 0;
                                        }
                                        Service.gI().sendThongBaoServer(player.name + " vừa tiêu diệt " + boss.name + " mọi người đều ngưỡng mộ");
                                    } else if (boss._typeBoss == 4 || boss._typeBoss == 6) {
                                        Service.gI().sendThongBaoServer(player.name + " vừa tiêu diệt " + boss.name + " mọi người đều ngưỡng mộ");
                                    } else if (boss._typeBoss >= (byte) 31 && boss._typeBoss <= (byte) 35) {
                                        int itemDT = 17;
                                        if (Util.nextInt(0, 4) == 0) {
                                            itemDT = 16;
                                        }
                                        ItemMap itemM = player.zone.newItemMAP(itemDT, player.id, boss.x, boss.y);
                                        if (itemM != null) {
                                            player.zone.addItemToMap(itemM, player.id, boss.x, boss.y);
                                        }
                                    } else if(boss._typeBoss == (byte)51) {
                                        if(Util.nextInt(0, 100) == 75) {
                                            ItemMap itemM = player.zone.dropItemGodCereal(player, boss.x, boss.y);
                                            if(itemM != null) {
                                                player.zone.addItemToMap(itemM, player.id, boss.x, boss.y);
                                            }
                                        }
                                    } else if(boss._typeBoss >= (byte)52 && boss._typeBoss <= (byte)55) {
                                        if(Util.nextInt(0, 100) >= 97) {
                                            ItemMap itemM = player.zone.cNewItemMap(Util.nextInt(1201, 1203), player.id, boss.x, boss.y);
                                            if(itemM != null) {
                                                player.zone.addItemToMap(itemM, player.id, boss.x, boss.y);
                                            }
                                        }
                                    }


                                    //ALL BOSS ROT DO
                                    if (boss._typeBoss == (byte) 4 || boss._typeBoss == (byte) 6 || (boss._typeBoss >= (byte) 18 && boss._typeBoss <= (byte) 30 && boss._typeBoss != (byte) 29) || boss._typeBoss == (byte)48) {
                                        ItemMap itemROT = player.zone.dropItemGOD(player, boss.x, boss.y, boss._typeBoss);
                                        if (itemROT != null) {
                                            player.zone.addItemToMap(itemROT, player.id, boss.x, boss.y);
                                        }
                                    }
                                }

                                if (boss._typeBoss == 2 && (player.havePet == 0 || (player.havePet == 1 && !player.detu.isMabu))) { //boss die la super broly
                                    player.detu = boss.detu;
                                    player.zone.leaveDEEEEE(boss.detu);
                                    if (player.havePet == 0) {
                                        player.isNewPet = true;
                                    }
                                    player.havePet = 1;
                                    player.detu.id = (-100000 - player.id);
                                    player.detu.gender = player.gender;
                                    player.statusPet = 0;
                                    player.petfucus = 1;
                                    player.zone.pets.add(player.detu);
                                    for (Player _plz : players) {
                                        player.zone.loadInfoDeTu(_plz.session, player.detu);
                                    }
                                } else {
                                    player.zone.leaveDEEEEE(boss.detu);
                                }
                                //boss chet
                                if (boss._typeBoss < (byte) 44 || boss._typeBoss > 47) {
                                    for (Player _pp : players) {
                                        player.zone.sendDieToAnotherPlayer(_pp, boss);
                                    }
                                }
                                //CHECK NHIEM VU SAN BOSS
                                Service.gI().updateTaskKillBoss(player, boss);

//                                player.zone.timerDeleteBoss(boss);
                            } else {
                                player.zone.dameChar(boss.id, boss.hp, dameBoom, false);
                            }
                        }
                    }
                }

                for(Player _p: players) {
                    if(Service.gI().checkCanAttackChar(player, _p) && !_p.isdie) {
                         if (Math.abs(player.x - _p.x) < skill.dx && Math.abs(player.y - _p.y) < skill.dy) {

                             _p.hasInjured(dameBoom);
                            if (_p.isdie) {
                                //NEU DEO NGOC RONG SAO DEN THI ROT RA DAT
                                Service.gI().dropDragonBall(_p);
                                
                                //CHECK NEU DANG CON DE TRUNG THI REMOVE DE TRUNG
                                if(_p.chimFollow == (byte)1 && _p.dameChim > 0) {
                                    _p.zone.useDeTrung(_p, (byte)7);
                                    _p.chimFollow = (byte)0;
                                    _p.dameChim = 0;
                                    _p.timerDeTrung.cancel();
                                    _p.timerDeTrung = null;
                                }
                                for(Player _pll: players) {
                                    if(_pll.id == _p.id) {
                                        if(_p.isMonkey) {
                                            Service.gI().loadCaiTrangTemp(_p);
                                            _p.isMonkey = false;
                                            //NOI TAI TANG DAME KHI HOA KHI
                                            if(_p.upDameAfterKhi && _p.noiTai.id != 0 && _p.noiTai.idSkill == (byte)13) {
                                                _p.upDameAfterKhi = false;
                                            }
                                            //NOI TAI TANG DAME KHI HOA KHI
                                            Service.gI().loadPoint(_p.session, _p);
                                        }
                                        player.zone.sendDieToMe(_p);
                                    } else {
                                        _pll.sendDefaultTransformToPlayer(_p);
                                        player.zone.sendDieToAnotherPlayer(_pll, _p);
                                    }
                                }
                            } else {
                                player.zone.dameChar(_p.id, _p.hp, dameBoom, false);
                            }
                            //ve dap kenh khi cho cac char khac
    //                        player.zone.attachedChar(player.id, _p.id, templateSkillUse.skillId);
                        }
                    }
                }

                for(Detu _pet: petss) {
                    if(((_pet.cPk != 0 && _pet.cPk != player.cPk && player.cPk != 0) || (_pet.cPk == 8 && player.cPk != 0) || (_pet.cPk != 0 && player.cPk == 8)) && _pet.id != player.id && !_pet.isdie) {
                        if (Math.abs(player.x - _pet.x) < skill.dx && Math.abs(player.y - _pet.y) < skill.dy) {

                            _pet.hp -= dameBoom;
                            if(_pet.hp <= 0) {
                                _pet.isdie = true;
                                _pet.isTTNL = false;
                                _pet.hp = 0;
                            }
                            if (_pet.isdie) {
                                //SEND TASK HOI SINH DE TU NEU DANH CHET
                                Timer hoiSinhDetu = new Timer();
                                TimerTask hsDetu = new TimerTask() {
                                    public void run()
                                    {
                                        if(_pet.isdie) {
                                            player.timerHSDe = null;
                                            hoiSinhDetu.cancel();
                                            Player suPhu = PlayerManger.gI().getPlayerByDetuID(_pet.id);
                                            _pet.x = suPhu.x;
                                            _pet.y = suPhu.y;
                                            Service.gI().petLiveFromDead(suPhu);
                                            if(suPhu.statusPet == (byte)1 || suPhu.statusPet == (byte)2) {
                                                suPhu.zone.PetAttack(suPhu, _pet, suPhu.statusPet);
                                            }
                                        } else {
                                            hoiSinhDetu.cancel();
                                        }
                                    };
                                };
                                hoiSinhDetu.schedule(hsDetu, 60000);
                                player.timerHSDe = hoiSinhDetu;
                                for(Player _pll: players) {

                                    _pll.sendDefaultTransformToPlayer(_pet);
                                    player.zone.sendDieToAnotherPlayer(_pll, _pet);
                                }
                            } else {
                                player.zone.dameChar(_pet.id, _pet.hp, dameBoom, false);
                            }
                            //ve dap kenh khi cho cac char khac
    //                        player.zone.attachedChar(player.id, _p.id, templateSkillUse.skillId);
                        }
                    }
                }

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
}
