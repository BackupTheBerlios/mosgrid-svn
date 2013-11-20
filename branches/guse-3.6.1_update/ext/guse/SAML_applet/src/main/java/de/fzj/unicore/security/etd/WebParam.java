package de.fzj.unicore.security.etd;

import java.util.Date;

/**
 * Class to store the keyword, description and value of an applet parameter.
 * 
 * @author v.huber
 *
 */
/**
 * @author v.huber
 *
 */
public class WebParam {

    private String keyword;
    private String description;
    private String value;
    private Date d;
    private boolean hideValue;

    public Date getD() {
        return d;
    }

    public void setD(Date d) {
        this.d = d;
    }

    public WebParam(String keyword) {
        this(keyword, null, null, null);
    }

    public WebParam(String keyword, String description) {
        this(keyword, description, null, null);
    }

    public WebParam(String keyword, String description, String value, Date d) {
        this.keyword = keyword;
        this.description = description;
        this.value = value;
        this.d = d;
    }

    public WebParam(String keyword, String description, int value, Date d) {
        this(keyword, description, String.valueOf(value), d);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return null if value is an empty string or null
     */
    public String getValue() {
        return ("".equals(value)) ? null : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = String.valueOf(value);
    }

    /**
     * @return value as an integer.
     * If the value is not specified then 0 will be returned.
     */
    public int getIntValue() throws NumberFormatException {
        return (value != null) ? Integer.parseInt(value) : 0;
    }

    /**
     *
     * @return if true then the value will be displayed as sequence of '*'-characters
     * in the toString() m.ethod
     * (useful for password fields)
     */
    public boolean hideValue() {
        return hideValue;
    }

    public void setHideValue(boolean hideValue) {
        this.hideValue = hideValue;
    }

    public String toString() {
	if (this == null || keyword == null) return "(null)";
        StringBuilder sb = new StringBuilder(keyword);
        sb.append("=");
        if (!hideValue) {
            sb.append(value != null?value:"");
        } else {
	    if (value != null) {
		for (int i = 0; i < value.length(); i++) {
		    sb.append("*");
		}
	    }
        }
        return sb.toString();
    }
}
