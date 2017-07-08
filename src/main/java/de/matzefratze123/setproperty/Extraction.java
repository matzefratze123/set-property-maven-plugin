package de.matzefratze123.setproperty;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extraction {
  private String property;
  private String text;
  private String regex;
  private String defaultValue;
  private boolean caseSensitive = true;
  private boolean toLowerCase;

  public void process(MavenProject project, Log log) {
    final Properties properties = project.getProperties();

    if (property == null) {
      log.warn("'property' element of 'extraction' element is required. skipped");
    }
    if (text == null) {
      log.warn("'value' element of 'extraction' element is required. skipped");
    }
    if (regex == null) {
      log.warn("'regex' element of 'extraction' element is required. skipped");
    }

    Pattern pattern = caseSensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      int groupCount = matcher.groupCount();
      String v = null;
      for (int i = 1; i <= groupCount && (v == null || v.length() == 0); i++) {
        v = matcher.group(i);
      }
      if (v == null || v.length() == 0) {
        if (defaultValue != null) {
          properties.setProperty(property, defaultValue);
          log.info("Setting property '" + property + "' to default value '" + defaultValue + "'");
        } else {
          log.info("No match was found for property '" + property);
        }
      } else {
        properties.setProperty(property, toLowerCase ? v.toLowerCase() : v);
        log.info("Setting property '" + property + "' to '" + v + "'");
      }
    } else {
      log.info("No match was found for property '" + property);
    }
  }

  public static void main(String[] args) {
    final Matcher matcher = Pattern.compile(".*\\[DEPLOY\\s+(VERSION|SNAPSHOT|SKIP)+].*|.*\\[(DEPLOY)+].*").matcher(
        "hello [DEPLOY VERSION] world");
    final boolean found = matcher.find();
    System.out.println(found);
    if (found) {
      final int groupCount = matcher.groupCount();
      System.out.println(groupCount);
      System.out.println(matcher.group());
      for (int i = 1; i <= groupCount; i++) {
        System.out.println(matcher.group(i));
      }
    }
  }
}
