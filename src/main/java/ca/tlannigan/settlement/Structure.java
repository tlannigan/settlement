package ca.tlannigan.settlement;

public interface Structure {
    abstract boolean canUpdate();
    abstract void updateStructure();
    abstract void updatePlayer();
}
