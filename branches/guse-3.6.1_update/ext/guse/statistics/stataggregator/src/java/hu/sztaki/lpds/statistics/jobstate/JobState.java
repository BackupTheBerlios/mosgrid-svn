    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.jobstate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * ENUM Describing all of the states that are valid for a job. 
 * 55 is a state that is created specifically by this project to descibe the last time a job enters state 5.
 * See StateType for the categorization criteria.
 * Categorization would be changed in this table
 * @author smoniz
 */
public enum JobState {

    INIT("INIT", "1", false, StateType.PORTAL),
    SUBMITTED("SUBMITTED", "2", false, StateType.QUEUE),
    WAITING("WAITING", "3", false, StateType.QUEUE),
    SCHEDULED("SCHEDULED", "4", false, StateType.QUEUE),
    RUNNING("FAILED RUN", "5", false, StateType.FAILEDRUN),
    FINISHED("FINISHED", "6", true, StateType.TERMINAL),
    ERROR("ERROR", "7", true, StateType.FAIL),
    NO_FREE_SERVICE("NO FREE SERVICE", "8", false, StateType.PORTAL),
    DONE("DONE", "9", true, StateType.TERMINAL),
    READY("READY", "10", false, StateType.QUEUE),
    CANCELLED("CANCELLED", "11", true, StateType.TERMINAL),
    CLEARED("CLEARED", "12", false, StateType.OTHER),
    PENDING("PENDING", "13", false, StateType.OTHER),
    ACTIVE("ACTIVE", "14", false, StateType.OTHER),
    SUSPENDED("SUSPENDED", "16", false, StateType.PORTAL),
    UNSUBMITTED("UNSUBMITTED", "17", true, StateType.TERMINAL),
    STAGE_IN("STAGE IN", "18", false, StateType.OTHER),
    STAGE_OUT("STAGE OUT", "19", false, StateType.OTHER),
    UNKNOWN_STATUS("UNKNOWN_STATUS", "20", false, StateType.OTHER),
    TERM_IS_FALSE("TERM IS FALSE", "21", true, StateType.FAIL),
    NO_INPUT("NO INPUT", "25", false, StateType.FAIL),
    CANNOT_BE_RUN("CANNOT BE RUN", "99", true, StateType.FAIL),
    SUCCESS_RUN("SUCCESSFUL RUN", "55", false, StateType.SUCCESSRUN);
    private final String id;
    private final boolean terminal;
    private final StateType type;
    private final String desc;

    JobState(String desc, String id, boolean terminal, StateType type) {
        this.id = id;
        this.terminal = terminal;
        this.type = type;
        this.desc = desc;
    }

    public String getID() {
        return id;
    }

    public boolean getTerminal() {
        return terminal;
    }

    public StateType getType() {
        return type;
    }
    /**
     * 
     * @param id
     * @return
     */
    public static JobState getByID(String id) {
        for (JobState state : JobState.values()) {
            if (state.getID().equals(id)) {
                return state;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }
    /**
     * Gets the ENUM from the string in the description
     * @param desc
     * @return
     */
    public static JobState getByDesc(String desc) {
        return JobState.valueOf(desc.replace(' ', '_').toUpperCase());
    }

    private static Map<StateType, Collection<JobState>> getByTypeMap = null;
    //Loaded as needed then memoized
    public static Collection<JobState> getByType(StateType t) {
        if (getByTypeMap == null) {
            getByTypeMap = new HashMap<StateType, Collection<JobState>>();
        }
        else {

            if (getByTypeMap.containsKey(t)) {
                return getByTypeMap.get(t);
            }
        }
        Collection<JobState> ts = new HashSet<JobState>();
        for (JobState state : JobState.values()) {
            if (state.getType() == t) {
                ts.add(state);
            }
        }
        getByTypeMap.put(t, ts);
        return ts;
    }

    @Override
    public String toString() {
        return id;
    }
}
