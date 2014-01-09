package gui;

import java.util.ResourceBundle;

import data.Configuration;

public class TypeIndicator {

  public static final TypeIndicator NONE = new TypeIndicator(false, false);
  public static final TypeIndicator MINOR = new TypeIndicator(true, false);
  public static final TypeIndicator NEWPAGE = new TypeIndicator(false, true);
  public static final TypeIndicator BOTH = new TypeIndicator(false, true);
  
  private boolean minor;
  private boolean newpage;
  String tooltip;
  
  private TypeIndicator(boolean minor, boolean newpage) {
    this.minor = minor;
    this.newpage = newpage;
    this.tooltip = createTooltip();
  }
  
  public final boolean isMinor() {
    return minor;
  }
  
  public final boolean isNewpage() {
    return newpage;
  }
  
  public static TypeIndicator create(boolean minor, boolean newpage) {
    TypeIndicator result;
    
    if (minor)
      result = newpage?BOTH:MINOR;
    else
      result = newpage?NEWPAGE:NONE;
    return result;
  }
  
  public String toString() {
    return minor ? (newpage ? "mN" : "m")
        : (newpage ? "N" : "");
  }
  
  public final String getTooltip() {
    return tooltip;
  }
  
  private String createTooltip() {
    ResourceBundle bundle = Configuration.getConfigurationObject().bundle;
    String result = null;
    if (minor || newpage) {
      StringBuffer sb = new StringBuffer();
      if (minor)
        sb.append(bundle.getString("type.minor"));
      if (newpage) {
        if (sb.length() != 0)
          sb.append(", ");
        sb.append(bundle.getString("type.newpage"));
      }

      result = sb.toString();
    }
    return result;
  }
}
