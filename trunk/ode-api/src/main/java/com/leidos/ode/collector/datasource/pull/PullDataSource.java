package com.leidos.ode.collector.datasource.pull;

import com.leidos.ode.collector.datasource.DataSource;

/**
 * This collector will go out and retrieve the data from the source.
 * This collector must be configured with all necessary connection information.
 * <p/>
 * Sources following this data should extend this class.
 *
 * @author cassadyja
 */
public abstract class PullDataSource extends DataSource {
}
