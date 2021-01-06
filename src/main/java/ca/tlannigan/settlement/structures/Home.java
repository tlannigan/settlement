package ca.tlannigan.settlement.structures;

import org.bukkit.entity.Player;

public class Home extends Structure {

    public Home(Player player) {
        super(player);
    }

    public boolean canUpdate(int structLevel) {
        switch (structLevel) {
            case 1:
                return playerHasAdvancement("story/smelt_iron");
            case 2:
                break;
            case 3:
                break;
        }

        return false;
    }

    public void updateStructure() {

    }

    public void updatePlayer() {

    }
}
