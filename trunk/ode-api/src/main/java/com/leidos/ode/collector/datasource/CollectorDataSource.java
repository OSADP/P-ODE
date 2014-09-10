package com.leidos.ode.collector.datasource;

public interface CollectorDataSource {

    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) throws DataSourceException;

    public void stopDataSource();

    public interface CollectorDataSourceListener {
        public void dataReceived(byte[] receivedData);
    }

    public class DataSourceException extends Exception {

        public DataSourceException(String string) {
            super(string);
        }

        public DataSourceException(String string, Throwable e) {
            super(string, e);
        }
    }
}
