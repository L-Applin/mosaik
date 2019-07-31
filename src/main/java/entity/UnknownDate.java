package entity;

import app.AppConfig;
import app.Bundles;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ResourceBundle;

public class UnknownDate implements ChronoLocalDate {

    private ResourceBundle bundle;

    public UnknownDate() {
        bundle = Bundles.getResources(Bundles.STRINGS);
    }


    @Override
    public String format(DateTimeFormatter formatter) {
        return bundle.getString("no_grad_date");
    }


    /* Required interface method implementation */
    public Chronology getChronology() { return Chronology.ofLocale(AppConfig.getInstance().getLocale()); }
    public int lengthOfMonth() { return -1; }
    public long until(Temporal endExclusive, TemporalUnit unit) { return -1; }
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) { return endDateExclusive.until(endDateExclusive); }
    public long getLong(TemporalField field) { return -1; }


}
