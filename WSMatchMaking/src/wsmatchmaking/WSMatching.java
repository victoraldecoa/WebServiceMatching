/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsmatchmaking;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author victor
 */
@XmlRootElement(namespace = "http://www.kth.se/ict/id2208/Matching", name = "WSMatching")
public class WSMatching {
    @XmlElement(name = "Macthing")
    public ArrayList<MatchedWebService> matchedWebServices;
}
