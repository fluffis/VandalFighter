package gui;

import java.util.Hashtable;
import java.util.ResourceBundle;

import data.Configuration;

public class ListIndicator {

  private static Hashtable indicators = new Hashtable();
  
  public static ListIndicator NONE = new ListIndicator();
  
  public static final int WATCH = 0x01;
  public static final int TWATCH = 0x02;
  public static final int BLACK = 0x04;
  public static final int TBLACK = 0x08;
  public static final int RBLACK = 0x10;
  
  private int value;
  private String toString;
  private String tooltip;
  
  private ListIndicator() {
    this(0, new Integer(0));
  }
  
  private ListIndicator(int value, Integer valueS) {
    this.value = value;
    StringBuffer sb = new StringBuffer();
    StringBuffer tb = new StringBuffer();
    ResourceBundle bundle = Configuration.getConfigurationObject().bundle;
    if ((value & WATCH) != 0) {
      sb.append("W");
      tb.append(bundle.getString("list.watch"));
    }
    if ((value & TWATCH) != 0) {
      sb.append("w");
      if (tb.length()!=0)
        tb.append(",\n");
      tb.append(bundle.getString("list.twatch"));
    }
    if ((value & BLACK) != 0) {
      sb.append("B");
      if (tb.length()!=0)
        tb.append(",\n");
      tb.append(bundle.getString("list.black"));
    }
    if ((value & TBLACK) != 0) {
      sb.append("b");
      if (tb.length()!=0)
        tb.append(",\n");
      tb.append(bundle.getString("list.tblack"));
    }
    if ((value & RBLACK) != 0) {
      sb.append("R");
      if (tb.length()!=0)
        tb.append(",\n");
      tb.append(bundle.getString("list.rblack"));
    }
    toString = sb.toString();
    tooltip = (tb.length() == 0)?null:tb.toString();
    
    indicators.put(valueS, this); 
    
    
  }

  public final boolean isInList(int list) {
    return (value & list) != 0;
  }
  
  public ListIndicator addList(int list) {
    int newValue = value | list;
    if (newValue == value)
      return this;
    Integer newValueI = new Integer(newValue);
    ListIndicator result = (ListIndicator)indicators.get(newValueI);
    if (result == null) {
      result = new ListIndicator(newValue, newValueI);
      indicators.put(newValueI, result);
    }
    return result;
  }
  
  public ListIndicator removeList(int list) {
    int newValue = value & (~list);
    if (newValue == value)
      return this;
    Integer newValueI = new Integer(newValue);
    ListIndicator result = (ListIndicator)indicators.get(newValueI);
    if (result == null) {
      result = new ListIndicator(newValue, newValueI);
      indicators.put(newValueI, result);
    }
    return result;
  }
  
  public static ListIndicator create(int value) {
    Integer valueI = new Integer(value);
    ListIndicator result = (ListIndicator)indicators.get(valueI);
    if (result == null) {
      result = new ListIndicator(value, valueI);
      indicators.put(valueI, result);
    }
    return result;
  }
  
  public final String toString() {
    return toString;
  }
  
  public final String getTooltip() {
    return tooltip;
  }
}
