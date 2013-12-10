package pl.swmind.robust.stream.gibbs.dto;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="robust_scn_message_points_view")
public class MessagePoint implements Serializable {
    private String messageuri;
    private Integer awardedpoints;
    private String comment;
    private Message message;

    public MessagePoint() {
    }

    public MessagePoint(String messageuri, String comment, Integer awardedpoints) {
        this.messageuri = messageuri;
        this.comment = comment;
        this.awardedpoints = awardedpoints;
    }

    @Id
    @Column(name="messageuri", length = 255, nullable = false)
    public String getMessageuri() {
        return messageuri;
    }

    public void setMessageuri(String messageuri) {
        this.messageuri = messageuri;
    }

    @Column(name="awardedpoints")
    public Integer getAwardedpoints() {
        return awardedpoints;
    }

    public void setAwardedpoints(Integer awardedpoints) {
        this.awardedpoints = awardedpoints;
    }

    @Column(name="comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @OneToOne
    @PrimaryKeyJoinColumn
    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessagePoint{" +
                "messageuri='" + messageuri + '\'' +
                ", awardedpoints=" + awardedpoints +
                ", comment='" + comment + '\'' +
                '}';
    }
}
