package net.zmods.daedalus;

import net.hollowcube.luau.LuaState;

public class Module {
    private final ModuleProperties properties;
    private final byte[] bytecode;
    private final short internalIdentifier;
    private boolean halted = false;
    private LuaState state;

    public Module(ModuleProperties properties, byte[] bytecode, short internalIdentifier) {
        this.properties = properties;
        this.bytecode = bytecode;
        this.internalIdentifier = internalIdentifier;
    }
}
