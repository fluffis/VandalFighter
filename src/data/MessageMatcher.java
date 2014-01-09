package data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageMatcher {

  // hex digits
  private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
      '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  /**
   * Map class where:
   * - key is parameter number in original message
   *   ($2 has key: new Integer(2))
   * - value is order number of corresponding matching group 
   *   (if (.*) is the first group, value is: new Integer(1)) 
   */
  private Map orderMap = new HashMap();

  private Pattern pattern = null;
  
  private Pattern pattern2 = null;

  private boolean isLogMessage;
  
  int lastParam = -1;
  
  private String name;

  public String getName() {
    return name;
  }

  private String message;

  public String getMessage() {
    return message;
  }

  public MessageMatcher(String name, String message, boolean isLogMessage) {
    this.name = name;
    this.message = message;
    this.isLogMessage = isLogMessage;

    createPatterns();
  }

  private void createPatterns() {
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < message.length(); i++) {
      char c = message.charAt(i);
      if ((c == '$') && (i + 1 < message.length())) {
        char c2 = message.charAt(i + 1);
        if (Character.getType(c2) == Character.DECIMAL_DIGIT_NUMBER && c2 != '0') {
          int v = Character.getNumericValue(c2);
          Integer intV = new Integer(v);
          Integer intOrder = (Integer) orderMap.get(intV);
          if (name.endsWith("Blocklogentry") && c2 == '3')
            sb.append("\\(");
          if (intOrder != null) {
            sb.append("\\").append(intOrder);
            i++;
          } else {
            sb.append("(.*)");
            i++;
            orderMap.put(intV, new Integer(orderMap.size() + 1));
            if (isLogMessage && i+1 == message.length()) 
              lastParam = v;
          }
          if (name.endsWith("Blocklogentry") && c2 == '3')
            sb.append("\\)");
          continue;
        }
      } 
      if (!Character.isLetterOrDigit(c)) {
        sb.append('\\');
        sb.append('u');
        sb.append(toHex((c >> 12) & 0xF));
        sb.append(toHex((c >> 8) & 0xF));
        sb.append(toHex((c >> 4) & 0xF));
        sb.append(toHex(c & 0xF));
      } else {
        sb.append(c);
      }
    }
    
    pattern = Pattern.compile("^"+sb.toString()+"$");
    if (isLogMessage) {
      pattern2 = Pattern.compile("^"+sb.toString()+": .*$");
    }
  }

  private static char toHex(int nibble) {
    return hexDigit[(nibble & 0xF)];
  }

  // TODO Beren optimalize to not create so many objects
  public synchronized Properties match(String s) {
    Properties result = null;

    if (isLogMessage) {
      Matcher matcher = pattern2.matcher(s);
      if (matcher.find()) {
        result = new Properties();
        for (Iterator iter = orderMap.entrySet().iterator(); iter.hasNext();) {
          Map.Entry entry = (Map.Entry) iter.next();
          Integer order = (Integer) entry.getValue();
          result.put(entry.getKey().toString(), matcher.group(order.intValue()));
        }
      }
    }

    if (result == null) {
      Matcher matcher = pattern.matcher(s);
      if (matcher.find()) {
        result = new Properties();
        for (Iterator iter = orderMap.entrySet().iterator(); iter.hasNext();) {
          Map.Entry entry = (Map.Entry) iter.next();
          Integer order = (Integer) entry.getValue();
          result.put(entry.getKey().toString(), matcher.group(order.intValue()));
        }
//
//        if (isLogMessage && lastParam != -1) {
//          String lastParamStr = Integer.toString(lastParam);
//          String lastParamResult = result.getProperty(lastParamStr);
//          if (lastParamResult != null) {
//            int index = lastParamResult.indexOf(": ");
//            if (index != -1)
//              result.setProperty(lastParamStr, lastParamResult.substring(0,
//                  index));
//          }
//        }
      }
    }

    return result;
  }
  
  // only for testing purposes
  public static void main(String[] args) {
    MessageMatcher m = new MessageMatcher("hehe", "Jajteles \"[[$2]]\" bumteles $1",true);
    Properties result = m.match("Jajteles \"[[zeleny]]\" bumteles \"[[modry]]\": lhjkhjdkhks");
    for (Iterator iter = result.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      System.out.println(entry.getKey()+": "+entry.getValue());
    }
  }
}
