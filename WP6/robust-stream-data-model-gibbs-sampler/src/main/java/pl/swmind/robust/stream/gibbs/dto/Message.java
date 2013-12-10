package pl.swmind.robust.stream.gibbs.dto;


import pl.swmind.robust.streaming.model.RobustStreamMessage;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Entity
@Table(name="robust_scn_messages_view")
public class Message extends RobustStreamMessage {
    private String messageuri;
    private String threaduri;
    private String messagetitle;
    private String contributor;
    private String creationdate;
    private MessagePoint messagepoint;
    private Thread thread;
    private Timestamp timestamp;

    public Message() {
    }

    public Message(String messageuri, String threaduri, String messagetitle, String contributor, String creationdate, Timestamp timestamp) {
        this.messageuri = messageuri;
        this.threaduri = threaduri;
        this.messagetitle = messagetitle;
        this.contributor = contributor;
        this.creationdate = creationdate;
        this.timestamp = timestamp;
    }

    @Id
    @Column(name="messageuri", length = 255, nullable = false)
    public String getMessageuri() {
        return messageuri;
    }

    public void setMessageuri(String messageuri) {
        this.messageuri = messageuri;
    }

    @Column(name="threaduri", length = 255, nullable = false)
    public String getThreaduri() {
        return threaduri;
    }

    public void setThreaduri(String threaduri) {
        this.threaduri = threaduri;
    }

    @Column(name="messagetitle", length = 255, nullable = false)
    public String getMessagetitle() {
        return messagetitle;
    }

    public void setMessagetitle(String messagetitle) {
        this.messagetitle = messagetitle;
    }

    @Column(name="contributor")
    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    @Column(name="creationdate", length = 255, nullable = false)
    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    @OneToOne
    @PrimaryKeyJoinColumn
    public MessagePoint getMessagepoint() {
        return messagepoint;
    }

    public void setMessagepoint(MessagePoint messagepoint) {
        this.messagepoint = messagepoint;
    }

    @OneToOne
    @JoinColumn(name = "threaduri", insertable=false, updatable=false)
    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Column(name="timestamp", nullable = false)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageuri='" + messageuri + '\'' +
                ", threaduri='" + threaduri + '\'' +
                ", messagetitle='" + messagetitle + '\'' +
                ", contributor='" + contributor + '\'' +
                ", creationdate='" + creationdate + '\'' +
                ", messagepoint=" + messagepoint +
                ", thread=" + thread +
                '}';
    }

    @Override
    public long createTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.UK);
        Date date = null;
        try {
            date = dateFormat.parse(creationdate);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
        return date.getTime();
    }
}
