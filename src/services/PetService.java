package services;

/*
 * @Author: NgojcDev
 */

import consts.ConstPlayer;
import player.NewPet;
import player.Pet;
import player.Player;
import utils.SkillUtil;
import utils.Util;

public class PetService {

    private static PetService instance;

    public static PetService gI() {
        if (instance == null) {
            instance = new PetService();
        }
        return instance;
    }

    public void createNormalPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createNormalPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createMabuPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false); // Trở lại random gender cho Mabư
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createMabuPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
            }
        }).start();
    }

    public void changeNormalPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, gender, limitPower);
    }

    public void changeNormalPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, limitPower);
    }

    public void changeMabuPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, limitPower);
    }

    public void changeMabuPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, gender, limitPower);
    }

    // Methods for new pet types
    public void createFidePet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, 5, player.gender); // Sử dụng gender của sư phụ
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Ta là Fide, đệ tử của ngươi!");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createKidBuPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, 6, player.gender); // Sử dụng gender của sư phụ
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Ta là Kid Bư, sức mạnh của ta vô tận!");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createXenPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, 7, player.gender); // Sử dụng gender của sư phụ
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Xên đã đến, hãy chuẩn bị chiến đấu!");
            } catch (Exception e) {
            }
        }).start();
    }

    public void createKidBeerusPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, 8, player.gender); // Sử dụng gender của sư phụ
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, "Ta là Kid Beerus, thần hủy diệt trẻ tuổi!");
            } catch (Exception e) {
            }
        }).start();
    }

    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryService.gI().isExistItemBag(player, 400)) {
                Service.gI().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.gI().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
                return;
            } else if (name.length() > 10) {
                Service.gI().sendThongBao(player, "Tên quá dài");
                return;
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.name = "$" + name.toLowerCase().trim();
            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 400), 1);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Service.gI().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã đặt cho con tên " + name);
                } catch (Exception e) {
                }
            }).start();
        } catch (Exception ex) {

        }
    }

    private int[] getDataPetNormal() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(20, 45); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetMabu() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(50, 120); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetPic() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 115) * 20; // hp
        petData[1] = Util.nextInt(40, 115) * 20; // mp
        petData[2] = Util.nextInt(70, 140); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetFide() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(50, 120) * 20; // hp
        petData[1] = Util.nextInt(50, 120) * 20; // mp
        petData[2] = Util.nextInt(80, 150); // dame
        petData[3] = Util.nextInt(15, 60); // def
        petData[4] = Util.nextInt(1, 3); // crit
        return petData;
    }

    private int[] getDataPetKidBu() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(60, 130) * 20; // hp
        petData[1] = Util.nextInt(60, 130) * 20; // mp
        petData[2] = Util.nextInt(90, 160); // dame
        petData[3] = Util.nextInt(20, 70); // def
        petData[4] = Util.nextInt(2, 4); // crit
        return petData;
    }

    private int[] getDataPetXen() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(70, 140) * 20; // hp
        petData[1] = Util.nextInt(70, 140) * 20; // mp
        petData[2] = Util.nextInt(100, 170); // dame
        petData[3] = Util.nextInt(25, 80); // def
        petData[4] = Util.nextInt(3, 5); // crit
        return petData;
    }

    private int[] getDataPetKidBeerus() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(80, 150) * 20; // hp
        petData[1] = Util.nextInt(80, 150) * 20; // mp
        petData[2] = Util.nextInt(110, 180); // dame
        petData[3] = Util.nextInt(30, 90); // def
        petData[4] = Util.nextInt(4, 6); // crit
        return petData;
    }

    private void createNewPet(Player player, int petType, byte... gender) {
        int[] data;
        String petName;
        long petPower;
        
        switch (petType) {
            case 0: // Normal Pet
                data = getDataPetNormal();
                petName = "Đệ tử";
                petPower = 2000;
                break;
            case 1: // Mabư
                data = getDataPetMabu();
                petName = "Mabư";
                petPower = 1500000;
                break;
            case 2: // Beerus
                data = getDataPetPic();
                petName = "Beerus";
                petPower = 1500000;
                break;
            case 3: // Pic
                data = getDataPetPic();
                petName = "Pic";
                petPower = 1500000;
                break;
            case 4: // Black
                data = getDataPetPic();
                petName = "Black";
                petPower = 1500000;
                break;
            case 5: // Fide
                data = getDataPetFide();
                petName = "Đệ tử Fide";
                petPower = 2000000;
                break;
            case 6: // Kid Bư
                data = getDataPetKidBu();
                petName = "Đệ tử Kid Bư";
                petPower = 2500000;
                break;
            case 7: // Xên
                data = getDataPetXen();
                petName = "Đệ tử Xên";
                petPower = 3000000;
                break;
            case 8: // Kid Beerus
                data = getDataPetKidBeerus();
                petName = "Đệ tử Kid Beerus";
                petPower = 3500000;
                break;
            default:
                data = getDataPetNormal();
                petName = "Đệ tử";
                petPower = 2000;
                petType = 0;
                break;
        }
        
        Pet pet = new Pet(player);
        pet.name = "$" + petName;
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = player.isPl() ? -player.id : -Math.abs(player.id) - 100000;
        pet.nPoint.power = petPower;
        pet.typePet = (byte) petType;
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int i = 0; i < 7; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int i = 0; i < 6; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }

    // Backward compatibility method
    private void createNewPet(Player player, boolean isMabu, boolean isBeerus, boolean isPic, boolean isBlack,
            byte... gender) {
        int petType = 0;
        if (isMabu) petType = 1;
        else if (isBeerus) petType = 2;
        else if (isPic) petType = 3;
        else if (isBlack) petType = 4;
        
        createNewPet(player, petType, gender);
    }

    public static void Pet2(Player pl, int h, int b, int l) {
        if (pl.newPet != null) {
            pl.newPet.dispose();
        }
        pl.newPet = new NewPet(pl, (short) h, (short) b, (short) l);
        pl.newPet.name = "$";
        pl.newPet.gender = pl.gender;
        pl.newPet.nPoint.tiemNang = 1;
        pl.newPet.nPoint.power = 1;
        pl.newPet.nPoint.limitPower = 1;
        pl.newPet.nPoint.hpg = 500000000;
        pl.newPet.nPoint.mpg = 500000000;
        pl.newPet.nPoint.hp = 500000000;
        pl.newPet.nPoint.mp = 500000000;
        pl.newPet.nPoint.dameg = 1;
        pl.newPet.nPoint.defg = 1;
        pl.newPet.nPoint.critg = 1;
        pl.newPet.nPoint.stamina = 1;
        pl.newPet.nPoint.setBasePoint();
        pl.newPet.nPoint.setFullHpMp();
    }
}
