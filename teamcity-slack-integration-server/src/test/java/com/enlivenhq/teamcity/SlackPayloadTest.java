package com.enlivenhq.teamcity;

import com.enlivenhq.slack.BuildInfo;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Collections;

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
    BuildInfo buildInfo = new BuildInfo.Builder()
            .username(username)
            .fullname(username)
            .email(username)
            .project(project)
            .build(build)
            .branch(branch)
            .statusText(statusText)
            .statusColor(statusColor)
            .btId(btId)
            .buildId(buildId)
            .serverUrl(serverUrl)
            .changesetList(Collections.emptyList())
            .createBuildInfo();

    @AfterMethod
    public void tearDown() throws Exception {
        slackPayload = null;
    }

    @Test
    public void testSlackPayloadDoesNotRequiresUserAndChannel() {
        slackPayload = new SlackPayload(buildInfo);
        assertFalse(false);
    }

    @Test
    public void testSlackPayloadWithoutAttachment() {
        slackPayload = new SlackPayload(buildInfo);
        slackPayload.setUseAttachments(false);
        assertFalse(slackPayload.hasAttachments());
    }

    @Test
    public void testSlackPayloadUsesAttachmentByDefault() {
        slackPayload = new SlackPayload(buildInfo);
        assertTrue(slackPayload.hasAttachments());
    }

    @Test
    public void testSlackPayloadIsUpdatedWithUsername() {
        slackPayload = new SlackPayload(buildInfo);
        slackPayload.setUseAttachments(false);
        slackPayload.setUsername(username);
        assertTrue(slackPayload.getUsername().equals(username));
    }

    @org.testng.annotations.Test
    public void testSlackPayloadIsUpdatedWithChannel() {
        slackPayload = new SlackPayload(buildInfo);
        slackPayload.setUseAttachments(false);
        slackPayload.setChannel(channel);
        assertTrue(slackPayload.getChannel().equals(channel));
    }
}