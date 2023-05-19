/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.bhaduri.generatecall;

import java.util.List;

/**
 *
 * @author sb
 */
public class ResultData extends RecordCallPrice{
    
    private String lastCallOneUpdated;
    private String lastCallTwoUpdated;
    private String tallyOneUpdated;
    private String tallyTwoUpdated;

    public String getLastCallOneUpdated() {
        return lastCallOneUpdated;
    }

    public void setLastCallOneUpdated(String lastCallOneUpdated) {
        this.lastCallOneUpdated = lastCallOneUpdated;
    }

    public String getLastCallTwoUpdated() {
        return lastCallTwoUpdated;
    }

    public void setLastCallTwoUpdated(String lastCallTwoUpdated) {
        this.lastCallTwoUpdated = lastCallTwoUpdated;
    }

    public String getTallyOneUpdated() {
        return tallyOneUpdated;
    }

    public void setTallyOneUpdated(String tallyOneUpdated) {
        this.tallyOneUpdated = tallyOneUpdated;
    }

    public String getTallyTwoUpdated() {
        return tallyTwoUpdated;
    }

    public void setTallyTwoUpdated(String tallyTwoUpdated) {
        this.tallyTwoUpdated = tallyTwoUpdated;
    }
    
}
