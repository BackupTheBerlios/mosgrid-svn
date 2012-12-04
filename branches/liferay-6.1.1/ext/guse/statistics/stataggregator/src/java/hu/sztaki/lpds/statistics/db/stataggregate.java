package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author smoniz
 */
public class stataggregate implements Runnable {

    /**
     * run runAgg() forever
     * Polling is fun!
     */
    public void run() {
        int sleep;
        try {
            sleep = Integer.valueOf(PropertyLoader.getInstance().getProperty("sleeptime"));
        }
        catch (Exception e) {
            sleep = 100;
        }
        while (run) {
            runAgg();
            //TODO: Possible Performance Enhancement
            //Somesort of back off algorithm: if pull.getAggregateJobs().size()<properties.aggregatejoblimit
            // Meaning you know as of the time of the last query you've emptied the database
            //IF the above is true, I've emptied the database of things I care about , therefore wait longer?
            try {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * What I do:
     * Open DB connection
     * Pulls set of aggregateJobs
     * Calculates statistics for them
     * Marks the jobs consumed
     * stores the statistics
     * clean job instance table
     * clean stat running table
     * close db connection
     *
     */
    private void runAgg() {
        Puller pull = null;
        try {
            pull = new Puller(new DBBase());
            Collection<StataggregateJob> jobs = pull.getAggregateJobs();
            Sorter sort = new Sorter();
            sort.sort(jobs, pull.conFactory);
            sort.put(pull.getCon());
            pull.markEntered(jobs);
            //TODO: Possible performance enhancement: Do I need to run the db cleaning so frequently? Should the DB Cleaning be a seperate thread running less frequently?

            pull.cleanJobInstance();
            pull.cleanStatRunning();
            //Can be even less frequently
            pull.cleanOldJobInstance();
            pull.cleanOldStatRunning();
            pull.close();

        }
        catch (IOException e) {

            e.printStackTrace();
        }
        catch (SQLException e) {

            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {


            e.printStackTrace();
        }
        finally {
            try {
                if (pull != null) {
                    pull.close();
                }
            }
            catch (SQLException e) {

                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {


                e.printStackTrace();
            }
        }
    }
    static boolean started = false;
    static volatile boolean run = false;

    /**
     *
     * @return
     */
    public String aggregateTop1000() {
        long start = java.lang.System.nanoTime();


        //Starts the Thread
        runAgg();

        String log =
               String.valueOf((double) (java.lang.System.nanoTime() - start) / 1000000000.0);
        return log;
    }

    /**
     * Toggles the StatAggregator service thread. Will return STARTING if is starting or STOPPING if it is ending the thread
     * @return
     */
    public String toggle() {
        if (run) {
            return stopLoop();
        }
        return startLoop();
    }

    /**
     *
     * @return
     */
    public String stopLoop() {
        run = false;
        started = false;
        return "STOPPING";
    }

    /**
     *
     * @return
     */
    public String startLoop() {
        if (!started) {
            started = true;
            run = true;
            new Thread(this).start();
        }
        return "STARTING";
    }
}
