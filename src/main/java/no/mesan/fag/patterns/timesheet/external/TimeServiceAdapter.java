package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

/**
 * Adapter til den eksterne tjenesten.
 */
public interface TimeServiceAdapter {

    /**
     * Henter alle timene registrert for en gitt bruker.
     *
     * @param userID Brukeren vi vil hente timer for
     * @return Liste av matchende timeføringer
     */
    Iterable<TimesheetEntry> forEmployee(String userID);

    /**
     * Henter alle timene registrert for et gitt år.
     *
     * @param year Året vi vil hente for
     * @return Liste av matchende timeføringer
     */
    Iterable<TimesheetEntry> forYear(int year);
}
