/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsmatchmaking;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author victor
 */
@XmlRootElement(name = "MacthedElement", namespace = "http://www.kth.se/ict/id2208/Matching")
class MatchedElement {

    private String outputElement;
    private String inputElement;
    private double score;

    public String getInputElement() {
        return inputElement;
    }

    public String getOutputElement() {
        return outputElement;
    }

    public double getScore() {
        return score;
    }

    public void setInputElement(String inputElement) {
        this.inputElement = inputElement;
    }

    public void setOutputElement(String outputElement) {
        this.outputElement = outputElement;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
