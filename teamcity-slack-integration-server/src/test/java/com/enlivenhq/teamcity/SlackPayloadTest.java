package com.enlivenhq.teamcity;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SlackPayloadTest {

    String project = "project";
    String build = "build";
    String branch = "";
    String statusText = "status";
    String statusColor = "color";
    String btId = "btId";
    long buildId = 0;
    String serverUrl = "localhost";
    String channel = "#channel";
    String username = "bot";
    SlackPayload slackPayload;

    @AfterMethod
    public void tearDown() throws Exception {
        slackPayload = null;
    }

    @Test
    public void testSlackPayloadDoesNotRequiresUserAndChannel() {
        slackPayload = new SlackPayload(project, build, branch, statusText, statusColor, btId, buildId, serverUrl);
        assertFalse(false);
    }

    @Test
    public void testSlackPayloadWithoutAttachment() {
        slackPayload = new SlackPayload(project, build, branch, statusText, statusColor, btId, buildId, serverUrl);
        slackPayload.setUseAttachments(false);
        assertFalse(slackPayload.hasAttachments());
    }

    @Test
    public void testSlackPayloadUsesAttachmentByDefault() {
        slackPayload = new SlackPayload(project, build, branch, statusText, statusColor, btId, buildId, serverUrl);
        assertTrue(slackPayload.hasAttachments());
    }

    @Test
    public void testSlackPayloadIsUpdatedWithUsername() {
        slackPayload = new SlackPayload(project, build, branch, statusText, statusColor, btId, buildId, serverUrl);
        slackPayload.setUseAttachments(false);
        slackPayload.setUsername(username);
        assertTrue(slackPayload.getUsername().equals(username));
    }

    @org.testng.annotations.Test
    public void testSlackPayloadIsUpdatedWithChannel() {
        slackPayload = new SlackPayload(project, build, branch, statusText, statusColor, btId, buildId, serverUrl);
        slackPayload.setUseAttachments(false);
        slackPayload.setChannel(channel);
        assertTrue(slackPayload.getChannel().equals(channel));
    }
}