package com.fetoxdevelopments.wormboard.status;

public class ResponseTime
{
  private long numRequests = 0;
  private double dbMillis = 0.0;
  private double aggMillis = 0.0;

  public double getDbMillis()
  {
    return dbMillis;
  }

  public double getAggMillis()
  {
    return aggMillis;
  }

  public void addNewRequest(double db, double agg)
  {
    numRequests = numRequests % 1000 + 1;
    dbMillis = ((numRequests - 1) * dbMillis + db) / numRequests;
    aggMillis = ((numRequests - 1) * aggMillis + agg) / numRequests;
  }
}
