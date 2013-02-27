/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsmatchmaking;

import javax.xml.bind.annotation.XmlRootElement;
import jwsl.SimilarityAssessor;
import jwsl.WordNotFoundException;
import transition.EditDistance;

/**
 *
 * @author victor & alex
 */
@XmlRootElement(name = "MacthedElement", namespace = "http://www.kth.se/ict/id2208/Matching")
class MatchedElement {

    private String outputElement;
    private String inputElement;
    private double score;

    public void calculateScoreUsingWordNet() {
        SimilarityAssessor sim = new SimilarityAssessor();

        // you can choose the proper metric among the implemented one by specifying its name.
        String metric = SimilarityAssessor.PIRRO_SECO_METRIC;

        try {
            score = sim.getSimilarity(outputElement, inputElement, metric);
        } catch (WordNotFoundException e) {
            System.out.print(e.getMessage());
            System.out.println(" Using EditDistance instead.");
            calculateScoreUsingEditDistance();
        }
    }

    public void calculateScoreUsingEditDistance() {
        score = EditDistance.getSimilarity(inputElement, outputElement);
    }

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
