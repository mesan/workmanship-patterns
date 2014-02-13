package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.TimesheetEntry;

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
}
