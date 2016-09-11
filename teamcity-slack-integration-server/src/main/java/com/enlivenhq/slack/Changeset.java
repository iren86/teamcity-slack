package com.enlivenhq.slack;

public class Changeset {
    private final String commit;
    private final String author;
    private final String summary;

    public Changeset(String commit, String author, String summary) {
        this.commit = commit;
        this.author = author;
        this.summary = summary;
    }

    public String getCommit() {
        return commit;
    }

    public String getAuthor() {
        return author;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Changeset{");
        sb.append("commit='").append(commit).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
