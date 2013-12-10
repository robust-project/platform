package pl.swmind.robust.webapp.user.role.form;

import java.io.Serializable;
import java.util.Date;

/**
 * Request generated from form. <br>
 * <p/>
 * Creation date: 11/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class FormRequest implements Serializable {
    private String userName = "";
    private Date startDate;
    private Date endDate;
    private Date date;

    public FormRequest() {
    }

    public FormRequest(String userName, Date startDate, Date endDate, Date date) {
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FormRequest){
            FormRequest formRequest = (FormRequest) o;
            if(formRequest.getStartDate().equals(startDate)
                && formRequest.getEndDate().equals(endDate)
                && formRequest.getUserName().equals(userName))
            {
                return true;
            }
        }
        return false;
    }
}