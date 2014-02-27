package no.mesan.fag.patterns.timesheet.external.decorators;

import no.mesan.fag.patterns.timesheet.external.TimeDataService;

/**
 * For de spesielt interesserte, så kan man f.eks. sette opp Spring-bønner for dekoratører slik:
 * <pre>
 * {@code
 *
 *   <bean id="timeDataService" class="no.mesan.fag.patterns.timesheet.external.TimeDataServer"/>
 *
 *   <bean id="cachingDecoratedTimeDataService"
 *         class="no.mesan.fag.patterns.timesheet.external.decorators.TimeDataServiceCachingDecorator">
 *         <constructor-arg name="timeDataService" ref="timeDataService"/>
 *   </bean>
 *
 *   <bean id="doubleDecoratedTimeDataService"
 *         class="no.mesan.fag.patterns.timesheet.external.decorators.TimeDataServiceLoggingDecorator">
 *         <constructor-arg name="timeDataService" ref="cachingDecoratedTimeDataService"/>
 *   </bean>
 *
 * }
 * </pre>
 *
 * Osv. For ekstra fancy config kan du til og med styre hvilke bønner som injectes via properties etc.,
 * men dette er langt utenfor scope :)
 */
public abstract class TimeDataServiceDecorator implements TimeDataService {

    protected final TimeDataService timeDataService;

    public TimeDataServiceDecorator(final TimeDataService timeDataService) {
        this.timeDataService = timeDataService;
    }
}
