package no.mesan.fag.patterns.timesheet.strategy;

/** Interface for hvordan tid skal vises i ferdig xlsx. */
public interface TimeRepresentationStrategy {

    /**
     * Konverterer antall minutter til riktig visning.
     * @param minutes minutter registrert
     * @return visningsverdi for xlsx
     */
    double convert(int minutes);
}
