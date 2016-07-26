package com.leidos.ode.agent.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/13/14
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DateParser extends ODEDataParser {

    protected abstract SimpleDateFormat buildSimpleDateFormat();

    protected final Date parseDate(String date) {
        if (date != null && !date.isEmpty()) {
            if (buildSimpleDateFormat() != null) {
                try {
                    return buildSimpleDateFormat().parse(date);
                } catch (ParseException e) {
                    getLogger().error(e.getLocalizedMessage());
                }
            } else {
                getLogger().debug("SimpleDateFormat was null. Unable to parse date.");
            }
        } else {
            getLogger().debug("Cannot parse empty or null date.");
        }
        return null;
    }
}
