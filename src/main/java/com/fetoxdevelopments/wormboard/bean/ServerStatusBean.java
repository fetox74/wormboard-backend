package com.fetoxdevelopments.wormboard.bean;

import java.util.List;

public class ServerStatusBean
{
  private List<String> allMonth;
  private String statusMessage;

  public ServerStatusBean(List<String> allMonth, String statusMessage)
  {
    this.allMonth = allMonth;
    this.statusMessage = statusMessage;
  }

  public List<String> getAllMonth()
  {
    return allMonth;
  }

  public void setAllMonth(List<String> allMonth)
  {
    this.allMonth = allMonth;
  }

  public String getStatusMessage()
  {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage)
  {
    this.statusMessage = statusMessage;
  }
}
