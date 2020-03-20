package utils;

import java.util.Map;

/**
 * Simple container for scenario state objects allowing state to be persisted between cucumber steps
 * within a scenario.  An instance of this class is injected at initialisation of each scenario preventing
 * state leaking between scenarios
 *
 */
public class StateContainer {

    private Map state;

    public StateContainer() {
        state = new java.util.HashMap<String, Object>();
    }

    public void add(String key, Object value) {
        state.put(key, value);
    }

    public int getInt(String key) {
        return (Integer)state.get(key);
    }

    public boolean hasKey(String key) {
        return state.containsKey(key);
    }

    public boolean getBoolean(String key) {
        return (Boolean)state.get(key);
    }

    public String getString(String key) {
        return (String)state.get(key);
    }

    public Object getObject(String key) {
        return state.get(key);
    }
}
