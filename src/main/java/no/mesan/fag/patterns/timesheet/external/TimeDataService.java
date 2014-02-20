package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

import java.util.List;

/**
 * Gnensesnittet til den eksterne tjenesten.
 */
public interface TimeDataService {

    /**
     * Henter alle timene registrert for en gitt bruker.
     * Hensikten var at denne skulle filtrere bort alle fakturererte timer, slik at den ikke vokser uhemmet over tid,
     * men det virker visst ikke...
     *
     * @param userID Brukeren vi vil hente timer for
     * @return Liste av matchende timeføringer
     */
    List<TimesheetEntry> forEmployee(String userID);

    /**
     * Henter alle timene registrert for et gitt år.
     *
     * @param year Året vi vil hente for
     * @return Liste av matchende timeføringer
     */
    List<TimesheetEntry> forYear(int year);
}
