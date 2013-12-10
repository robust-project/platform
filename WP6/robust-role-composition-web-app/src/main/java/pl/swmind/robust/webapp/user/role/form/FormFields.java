package pl.swmind.robust.webapp.user.role.form;

/**
 * Form fields for data input. <br>
 * <p/>
 * Creation date: 18/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public enum FormFields {
    USER("userName"),
    START_DATE("startDate"),
    END_DATE("endDate"),
    DATE("date");

    private String fieldName;

    private FormFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
