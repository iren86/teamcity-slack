package com.enlivenhq.teamcity;

import com.enlivenhq.slack.BuildInfo;
import com.enlivenhq.slack.Changeset;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlackPayload {
    @Expose
    protected String text;
    @Expose
    protected String channel;
    @Expose
    protected String username;
    @Expose
    protected List<Attachment> attachments;
    private List<Attachment> _attachments;

    private class Attachment {
        @Expose
        protected String fallback;
        @Expose
        protected String pretext;
        @Expose
        protected String color;
        @Expose
        protected List<AttachmentField> fields;
    }

    private class AttachmentField {
        public AttachmentField(String name, String val, boolean isShort) {
            title = name;
            value = val;
            this.isShort = isShort;
        }

        @Expose
        protected String title;
        @Expose
        protected String value;
        @Expose
        protected boolean isShort;
    }

    private boolean useAttachments = true;

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUseAttachments(boolean useAttachments) {
        this.useAttachments = useAttachments;
        if (!useAttachments) {
            _attachments = attachments;
            attachments = null;
        } else {
            attachments = _attachments;
        }
    }

    public boolean hasAttachments() {
        return attachments != null && attachments.size() > 0;
    }

    public SlackPayload(@NotNull BuildInfo info) {
        Objects.requireNonNull(info);

        String statusText = "<" + info.getServerUrl() + "/viewLog.html?buildId=" + info.getBuildId() + "&buildTypeId=" + info.getBtId() + "|" + info.getStatusText() + ">";
        String statusEmoji = info.getStatusColor().equals("danger") ? ":x: " : info.getStatusColor().equals("warning") ? "" : ":white_check_mark: ";
        String payloadText = statusEmoji + info.getProject() + " #" + info.getBuild();

        this.text = payloadText;

        Attachment attachment = new Attachment();
        attachment.color = info.getStatusColor();
        attachment.pretext = "Build Info";
        attachment.fallback = payloadText;
        attachment.fields = new ArrayList<>();

        AttachmentField attachmentStatus = new AttachmentField("Status", statusText, false);
        AttachmentField attachmentBranch = new AttachmentField("Branch", info.getBranch(), true);

        attachment.fields.add(attachmentStatus);
        attachment.fields.add(attachmentBranch);
        attachment.fields.add(getCommitAttachmentField(info));

        this._attachments = new ArrayList<>();
        this._attachments.add(0, attachment);

        if (this.useAttachments) {
            this.attachments = this._attachments;
        }
    }

    private AttachmentField getCommitAttachmentField(BuildInfo info) {
        if (!info.getChangesetList().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Changeset changeset : info.getChangesetList()) {
                sb.append("Commit: ").append(changeset.getCommit()).append("\n");
                sb.append("Author: ").append(changeset.getAuthor()).append("\n");
                sb.append("Summary: ").append(changeset.getSummary()).append("\n");
                sb.append("\n");
            }
            return new AttachmentField("Changesets", sb.toString(), false);
        }

        return new AttachmentField("Build restarted By: " + getUserInfo(info), "", false);
    }

    private String getUserInfo(BuildInfo info) {
        return String.format("%s <%s>", info.getFullname(), info.getEmail());
    }
}