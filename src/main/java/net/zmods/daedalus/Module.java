package net.zmods.daedalus;

import net.hollowcube.luau.LuaState;

public class Module {
    public final ModuleProperties properties;
    private final byte[] bytecode;
    private boolean halted = false;
    private LuaState state;

    public Module(ModuleProperties properties, byte[] bytecode) {
        this.properties = properties;
        this.bytecode = bytecode;
    }
}
