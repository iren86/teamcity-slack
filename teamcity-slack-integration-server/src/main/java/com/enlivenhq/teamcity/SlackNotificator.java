package com.enlivenhq.teamcity;

import com.enlivenhq.slack.BuildInfo;
import com.enlivenhq.slack.Changeset;
import com.enlivenhq.slack.SlackWrapper;
import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.web.util.WebUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SlackNotificator implements Notificator {

    private static final Logger log = Logger.getLogger(SlackNotificator.class);

    private static final String type = "SlackNotificator";

    private static final String slackChannelKey = "slack.Channel";
    private static final String slackUsernameKey = "slack.Username";
    private static final String slackUrlKey = "slack.Url";

    private static final PropertyKey slackChannel = new NotificatorPropertyKey(type, slackChannelKey);
    private static final PropertyKey slackUsername = new NotificatorPropertyKey(type, slackUsernameKey);
    private static final PropertyKey slackUrl = new NotificatorPropertyKey(type, slackUrlKey);

    private SBuildServer myServer;

    public SlackNotificator(NotificatorRegistry notificatorRegistry, SBuildServer server) {
        registerNotificatorAndUserProperties(notificatorRegistry);
        myServer = server;
    }

    @NotNull
    public String getNotificatorType() {
        return type;
    }

    @NotNull
    public String getDisplayName() {
        return "Slack Notifier";
    }

    public void notifyBuildFailed(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        sendNotification(extractInfo(build, "failed: " + build.getStatusDescriptor().getText(), "danger"), users);
    }

    public void notifyBuildFailedToStart(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        sendNotification(extractInfo(build, "failed to start: " + build.getStatusDescriptor().getText(), "danger"), users);
    }

    public void notifyBuildSuccessful(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        sendNotification(extractInfo(build, "built successfully", "good"), users);
    }

    public void notifyBuildFailing(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        sendNotification(extractInfo(build, "failing", "danger"), users);
    }

    public void notifyBuildProbablyHanging(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        sendNotification(extractInfo(build, "probably hanging", "warning"), users);
    }

    public void notifyBuildStarted(@NotNull SRunningBuild build, @NotNull Set<SUser> users) {
        sendNotification(extractInfo(build, "started", "warning"), users);
    }

    public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot vcsRoot, @NotNull Throwable throwable, @NotNull Set<SUser> users) {
    }

    public void notifyResponsibleChanged(@NotNull SBuildType sBuildType, @NotNull Set<SUser> users) {

    }

    public void notifyResponsibleAssigned(@NotNull SBuildType sBuildType, @NotNull Set<SUser> users) {

    }

    public void notifyResponsibleChanged(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry2, @NotNull SProject sProject, @NotNull Set<SUser> users) {

    }

    public void notifyResponsibleAssigned(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry2, @NotNull SProject sProject, @NotNull Set<SUser> users) {

    }

    public void notifyResponsibleChanged(@NotNull Collection<TestName> testNames, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> users) {

    }

    public void notifyResponsibleAssigned(@NotNull Collection<TestName> testNames, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> users) {

    }

    public void notifyBuildProblemResponsibleAssigned(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> users) {

    }

    public void notifyBuildProblemResponsibleChanged(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> users) {

    }

    public void notifyTestsMuted(@NotNull Collection<STest> sTests, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> users) {

    }

    public void notifyTestsUnmuted(@NotNull Collection<STest> sTests, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> users) {

    }

    public void notifyBuildProblemsMuted(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> users) {

    }

    public void notifyBuildProblemsUnmuted(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> users) {

    }

    private void registerNotificatorAndUserProperties(NotificatorRegistry notificatorRegistry) {
        ArrayList<UserPropertyInfo> userPropertyInfos = getUserPropertyInfosList();
        notificatorRegistry.register(this, userPropertyInfos);
    }

    private ArrayList<UserPropertyInfo> getUserPropertyInfosList() {
        ArrayList<UserPropertyInfo> userPropertyInfos = new ArrayList<>();

        userPropertyInfos.add(new UserPropertyInfo(slackChannelKey, "Slack Channel"));
        userPropertyInfos.add(new UserPropertyInfo(slackUsernameKey, "Slack Username"));
        userPropertyInfos.add(new UserPropertyInfo(slackUrlKey, "Slack Webhook URL"));

        return userPropertyInfos;
    }

    private void sendNotification(@NotNull BuildInfo info, @NotNull Set<SUser> users) {
        for (SUser user : users) {
            SlackWrapper slackWrapper = getSlackWrapperWithUser(user);
            try {
                slackWrapper.send(info);
            } catch (IOException e) {
                log.error("Failed to send notification", e);
            }
        }
    }

    private SlackWrapper getSlackWrapperWithUser(SUser user) {
        String channel = user.getPropertyValue(slackChannel);
        String username = user.getPropertyValue(slackUsername);
        String url = user.getPropertyValue(slackUrl);

        if (slackConfigurationIsInvalid(channel, username, url)) {
            log.error("Could not send Slack notification. The Slack channel, username, or URL was null. " +
                    "Double check your Notification settings");

            return new SlackWrapper();
        }

        return constructSlackWrapper(channel, username, url);
    }

    private boolean slackConfigurationIsInvalid(String channel, String username, String url) {
        return channel == null || username == null || url == null;
    }

    private SlackWrapper constructSlackWrapper(String channel, String username, String url) {
        SlackWrapper slackWrapper = new SlackWrapper();

        slackWrapper.setChannel(channel);
        slackWrapper.setUsername(username);
        slackWrapper.setSlackUrl(url);

        return slackWrapper;
    }

    private BuildInfo extractInfo(@NotNull SBuild build, String statusText, String statusColor) {
        List<Changeset> changesetList = new ArrayList<>();
        List<SVcsModification> containingChanges = build.getContainingChanges();
        for (SVcsModification modification : containingChanges) {
            String commit = modification.getVersion();
            String author = modification.getUserName();
            String summary = modification.getDescription();
            changesetList.add(new Changeset(commit, author, summary));
        }
        BuildInfo info = new BuildInfo.Builder()
                .username(getUsername(build))
                .fullname(getFullname(build))
                .email(getEmail(build))
                .project(build.getFullName())
                .build(build.getBuildNumber())
                .branch(getBranch(build))
                .statusText(statusText)
                .statusColor(statusColor)
                .btId(build.getBuildTypeExternalId())
                .buildId(build.getBuildId())
                .serverUrl(WebUtil.escapeUrlForQuotes(myServer.getRootUrl()))
                .changesetList(changesetList)
                .createBuildInfo();
        log.error(String.format("Results: %s", info));

        return info;
    }

    private String getUsername(SBuild build) {
        String defaultUsername = "guest";
        TriggeredBy triggeredBy = build.getTriggeredBy();
        if (triggeredBy == null) {
            return defaultUsername;
        }
        SUser user = triggeredBy.getUser();
        if (user == null) {
            return defaultUsername;
        }
        String username = user.getUsername();
        if (username == null) {
            return defaultUsername;
        }

        return username;
    }

    private String getFullname(SBuild build) {
        String defaultFullname = "guest";
        TriggeredBy triggeredBy = build.getTriggeredBy();
        if (triggeredBy == null) {
            return defaultFullname;
        }
        SUser user = triggeredBy.getUser();
        if (user == null) {
            return defaultFullname;
        }
        String fullname = user.getDescriptiveName();
        if (fullname == null) {
            return defaultFullname;
        }

        return fullname;
    }

    private String getEmail(SBuild build) {
        String defaultEmail = "-";
        TriggeredBy triggeredBy = build.getTriggeredBy();
        if (triggeredBy == null) {
            return defaultEmail;
        }
        SUser user = triggeredBy.getUser();
        if (user == null) {
            return defaultEmail;
        }
        String email = user.getEmail();
        if (email == null) {
            return defaultEmail;
        }

        return email;
    }

    private String getBranch(SBuild build) {
        Branch branch = build.getBranch();
        if (branch != null) {
            return branch.getDisplayName();
        }

        return "default";
    }
}
