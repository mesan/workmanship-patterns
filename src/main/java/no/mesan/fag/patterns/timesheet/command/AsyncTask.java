package no.mesan.fag.patterns.timesheet.command;

/** Pakker en oppgave som kan kjøre asynkront. */
public interface AsyncTask {

    /** Presenter deg for verden. */
    String whoAmI();

    /**
     * Run, rabbit, run.
     * Dig that hole, forget the sun.
     * And when at last the work is done.
     * Don't sit down.
     * It's time to dig another one.
     */
    void executeTask();
}
