package gui;

public class WarningIndicator {

  public static final WarningIndicator NONE = new WarningIndicator("", null);
  
  private String label;
  private String tooltip;
  
  public WarningIndicator(String label, String tooltip) {
    super();
    
    this.label = label;
    this.tooltip = tooltip;
  }
  
  public String getTooltip() {
    return tooltip;
  }
  
  public String toString() {
    return label;
  }

}
