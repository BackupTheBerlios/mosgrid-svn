package hu.sztaki.lpds.statistics.jobState;

/**
 * Describes different categories of states
 * Mapping exists in JobState {See StatAggregator Project}
 * FAIL Failed terminal state
 * PORTAL Time spent doing processing on the portal
 * RUN  TIme spent running that did not lead to a successful terminal state
 * TERMINAL Successful terminal state
 * OTHER No idea what these do
 * SUCCESSRUN Time spent running that lead to a successful terminal state
 * Used to group states rather then display data abou tall 20+ different job states. Change StatAggregator.jobState.JobState to change the mapping
 * @author smoniz
 */
public enum StateType {

    FAIL("FAIL"),PORTAL("PORTAL"), QUEUE("QUEUE"), FAILEDRUN("FAILEDRUN"), TERMINAL("TERMINAL"), OTHER("OTHER"), SUCCESSRUN("SUCCESSRUN");

    StateType(String desc) {
        this.desc = desc;
    }
    private final String desc;
    /**
     *
     * @return desc
     */
    @Override
    public String toString() {
        return desc;
    }

}
