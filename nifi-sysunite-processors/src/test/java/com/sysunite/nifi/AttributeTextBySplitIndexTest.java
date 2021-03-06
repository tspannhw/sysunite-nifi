package com.sysunite.nifi;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AttributeTextBySplitIndexTest {

  private TestRunner testRunner;

  @Before
  public void init() throws URISyntaxException {
    testRunner = TestRunners.newTestRunner(AttributeTextBySplitIndex.class);
  }

  @Test
  public void testCorrectAttributes() {
    ProcessSession session = testRunner.getProcessSessionFactory().createSession();
    
    // Create flowFile with content
    FlowFile flowFile = session.create();
    flowFile = session.importFrom(new ByteArrayInputStream("A B C D\nE F G H".getBytes()), flowFile);
    
    // Set properties
    testRunner.setProperty(RouteTextBySplitIndex.SKIP_LINES, "1");
    testRunner.setProperty(RouteTextBySplitIndex.PATTERN, " ");
    testRunner.setProperty("first",  "0");
    testRunner.setProperty("second", "1");
    testRunner.setProperty("third",  "2");
    testRunner.setProperty("fourth", "3");

    // Add the flowfile to the runner
    testRunner.enqueue(flowFile);

    // Run the enqueued content, it also takes an int = number of contents queued
    testRunner.run();

    // Get result
    List<MockFlowFile> resultsFirst = testRunner.getFlowFilesForRelationship(AttributeTextBySplitIndex.ORIGINAL);
    assertEquals(resultsFirst.size(), 1);
    assertEquals(resultsFirst.get(0).getAttribute("first"),  "E");
    assertEquals(resultsFirst.get(0).getAttribute("second"), "F");
    assertEquals(resultsFirst.get(0).getAttribute("third"),  "G");
    assertEquals(resultsFirst.get(0).getAttribute("fourth"), "H");
  }
  

  @Test(expected = AssertionError.class)
  public void testSkipLinesFailure() {
    ProcessSession session = testRunner.getProcessSessionFactory().createSession();

    // Create flowFile with content
    FlowFile flowFile = session.create();
    flowFile = session.importFrom(new ByteArrayInputStream("A B C D".getBytes()), flowFile);

    // Set properties
    testRunner.setProperty(RouteTextBySplitIndex.SKIP_LINES, "1");
    testRunner.setProperty(RouteTextBySplitIndex.PATTERN, " ");
    
    // Add the flowfile to the runner
    testRunner.enqueue(flowFile);
    
    // Run the queued content, it also takes an int = number of contents queued
    testRunner.run();
  }
}