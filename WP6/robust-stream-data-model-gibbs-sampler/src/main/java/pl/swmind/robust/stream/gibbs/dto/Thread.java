package pl.swmind.robust.stream.gibbs.dto;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="robust_scn_threads_view")
public class Thread implements Serializable {
    private String threaduri;
    private String threadtitle;
    private String status;
    private Double noofviews;
    private String forumuri;
    private Message message;

    public Thread() {
    }

    public Thread(String threaduri, String threadtitle, String status, Double noofviews, String forumuri) {
        this.threaduri = threaduri;
        this.threadtitle = threadtitle;
        this.status = status;
        this.noofviews = noofviews;
        this.forumuri = forumuri;
    }

    @Id
    @Column(name="threaduri", length = 255, nullable = false)
    public String getThreaduri() {
        return threaduri;
    }

    public void setThreaduri(String threaduri) {
        this.threaduri = threaduri;
    }

    @Column(name="threadtitle", length = 255)
    public String getThreadtitle() {
        return threadtitle;
    }

    public void setThreadtitle(String threadtitle) {
        this.threadtitle = threadtitle;
    }

    @Column(name="status", length = 255)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name="noofviews")
    public Double getNoofviews() {
        return noofviews;
    }

    public void setNoofviews(Double noofviews) {
        this.noofviews = noofviews;
    }

    @Column(name="forumuri", length = 255, nullable = false)
    public String getForumuri() {
        return forumuri;
    }

    public void setForumuri(String forumuri) {
        this.forumuri = forumuri;
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
        return "Thread{" +
                "threaduri='" + threaduri + '\'' +
                ", threadtitle='" + threadtitle + '\'' +
                ", status='" + status + '\'' +
                ", noofviews=" + noofviews +
                ", forumuri='" + forumuri + '\'' +
                '}';
    }
}
