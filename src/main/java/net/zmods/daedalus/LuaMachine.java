package net.zmods.daedalus;

import net.hollowcube.luau.LuaState;
import net.hollowcube.luau.compiler.LuauCompileException;
import net.hollowcube.luau.compiler.LuauCompiler;

public class LuaMachine {

    public void run(String name, String source) throws LuauCompileException {
        final byte[] bytecode = LuauCompiler.DEFAULT.compile(source);

        LuaState state = LuaState.newState();
        try {
            state.openLibs();
            state.sandbox();

            LuaState thread = state.newThread();
            thread.sandboxThread();
            thread.load(name, bytecode);
            thread.pcall(0, 0);

            state.pop(1);

        } finally {
            state.close();
        }
    }

    public class instance {

    }

}
