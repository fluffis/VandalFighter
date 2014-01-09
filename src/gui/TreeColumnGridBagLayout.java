package gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public class TreeColumnGridBagLayout extends GridBagLayout {

  private static final long serialVersionUID = -2204356614461007392L;

  protected Component component1;
  protected Component component2;
  protected Component component3;
  
  public TreeColumnGridBagLayout(Component c1, Component c2, Component c3) {
    super();
    
    component1 = c1;
    component2 = c2;
    component3 = c3;
  }
  
  protected void ArrangeGrid(Container parent) {
    Component components[] = parent.getComponents();
    int minWidth = 0;
    int prefWidth = 0;
    
    for (int i = 0; i < components.length; i++) {
      Component comp = components[i];
      minWidth = Math.max(minWidth, comp.getMinimumSize().width);
      prefWidth = Math.max(prefWidth, comp.getPreferredSize().width);
    } 
    // set equal size of all three columns
    component1.setPreferredSize(new Dimension(prefWidth, component1.getPreferredSize().height));
    component1.setMinimumSize(new Dimension(minWidth, component1.getPreferredSize().height));
    component2.setPreferredSize(new Dimension(prefWidth, component2.getPreferredSize().height));
    component2.setMinimumSize(new Dimension(minWidth, component2.getMinimumSize().height));
    component3.setPreferredSize(new Dimension(prefWidth, component3.getPreferredSize().height));
    component3.setMinimumSize(new Dimension(minWidth, component3.getMinimumSize().height));
    super.ArrangeGrid(parent);
  }

}
