package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

import java.util.List;

/**
 * Gnensesnittet til den eksterne tjenesten.
 */
public interface TimeDataService {

    /** Antall som hentes av gangen. */
    public static int BATCH_SIZE= 25;

    /**
     * Henter alle timene registrert for en gitt bruker.
     * Hensikten var at denne skulle filtrere bort alle fakturererte timer, slik at den ikke vokser uhemmet over tid,
     * men det virker visst ikke...
     *
     * @param from 0 første gang, deretter antall hentet tidligere
     * @param userID Brukeren vi vil hente timer for
     * @return Liste av matchende timeføringer; dersom den returnerer færre en BATCH_SIZE forekomster er alle hentet
     */
    List<TimesheetEntry> forEmployee(String userID, int from);

    /**
     * Henter alle timene registrert for et gitt år.
     *
     * @param from 0 første gang, deretter antall hentet tidligere
     * @param year Året vi vil hente for
     * @return Liste av matchende timeføringer; dersom den returnerer færre en BATCH_SIZE forekomster er alle hentet
     */
    List<TimesheetEntry> forYear(int year, int from);
}
