package wsmatchmaking;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victor & alex
 */
@XmlRootElement(name = "MacthedOperation", namespace = "http://www.kth.se/ict/id2208/Matching")
@XmlType(propOrder = { "inputOperationName", "outputOperationName", "opScore", "matchedElements" })
public class MatchedOperation {

    private String OutputOperationName;
    private String InputOperationName;
    
    @XmlElement(name = "OpScore")
    private double opScore;
    
    @XmlElement(name = "MacthedElement")
    public ArrayList<MatchedElement> matchedElements;

    public String getInputOperationName() {
        return InputOperationName;
    }

    public double getOpScore() {
        return opScore;
    }

    public String getOutputOperationName() {
        return OutputOperationName;
    }

    public void setInputOperationName(String inputOperationName) {
        this.InputOperationName = inputOperationName;
    }

    public void setOutputOperationName(String outputOperationName) {
        this.OutputOperationName = outputOperationName;
    }
    
    public void calculateOpScore() {
        opScore = 0;
        
        if (matchedElements.isEmpty()) {
            return;
        }
        
        for (MatchedElement me : matchedElements) {
            opScore += me.getScore();
        }
        opScore /= matchedElements.size();
    }
}
