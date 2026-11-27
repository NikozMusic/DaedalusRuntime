package net.zmods.daedalus.api;

import net.hollowcube.luau.LuaState;
import net.zmods.daedalus.Module;

public class MinecraftAPI {
    private void open(LuaState state) {
        state.newTable();
        state.setGlobal("rawr");
        state.setGlobal("minecraft");
    }

}
