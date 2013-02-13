/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsmatchmaking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import javax.wsdl.xml.WSDLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 *
 * @author victor
 */
public class WSMatchMaking {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);

        // getting all the WSDL files
        File folder = new File("src/WSDLs");
        File[] listOfFiles = folder.listFiles();

        System.out.println("The available WSDLs are:");
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                String file_name;
                file_name = listOfFiles[i].getName();
                System.out.println("    " + i + " - " + file_name);
            }
        }
        System.out.println();
        System.out.print("Choose the input web service: ");

        FileInputStream inputWs = new FileInputStream(listOfFiles[scanner.nextInt()]);

        System.out.print("Choose the output web service: ");

        FileInputStream outputWs = new FileInputStream(listOfFiles[scanner.nextInt()]);

        System.out.println();
        System.out.println("1 - : Use EditDistance");
        System.out.println("2 - : Use WordNet");
        System.out.print("Choose the method for calculating a matched element score: ");

        boolean editDistance = false;
        if (scanner.nextInt() == 1) {
            editDistance = true;
        }

        // TODO use inputWs and outputWs to get the actual descriptions and populate a MatchedWebService
        // TODO issue #1
        WSDLReader reader;

        WSMatching wsmatching = new WSMatching();
        wsmatching.matchedWebServices = new ArrayList<>();

        MatchedWebService mws = new MatchedWebService();
        mws.matchedOperations = new ArrayList<>();

        // TODO here we should have the name for the actual webservices
        mws.setInputServiceName("something");
        mws.setOutputServiceName("something2");

        // TODO for each operation Oi on WS_I
        for (int oi = 0; oi < 0; oi++) {
            // TODO for each operation Oj on WS_O
            for (int oj = 0; oj < 0; oj++) {
                MatchedOperation mo = new MatchedOperation();
                mo.matchedElements = new ArrayList<>();
                mo.setInputOperationName("getWine");
                mo.setOutputOperationName("getDrink");

                // TODO for each element on Oi
                for (int ei = 0; ei < 0; ei++) {
                    // TODO for each element on Oj
                    for (int ej = 0; ej < 0; ej++) {
                        MatchedElement me = new MatchedElement();
                        me.setInputElement("Country");
                        me.setOutputElement("Country");
                        me.setScore(1.0);
                        mo.matchedElements.add(me);

                        if (editDistance) {
                            me.calculateScoreUsingEditDistance();
                        } else {
                            me.calculateScoreUsingWordNet();
                        }
                    }
                }

                mo.calculateOpScore();

                mws.matchedOperations.add(mo);
            }
        }


        mws.calculateWSScore();
        wsmatching.matchedWebServices.add(mws);

        //createOutput(wsmatching);

        testOutput();
    }

    private static void createOutput(WSMatching wsm) {
        JAXBContext context;
        Marshaller m;
        try {
            context = JAXBContext.newInstance(WSMatching.class);

            m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            m.marshal(wsm, new File("src/output.xml"));

        } catch (JAXBException ex) {
            Logger.getLogger(WSMatchMaking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void testOutput() {
        WSMatching wsm = new WSMatching();
        wsm.matchedWebServices = new ArrayList<>();

        MatchedWebService mws = new MatchedWebService();
        mws.matchedOperations = new ArrayList<>();
        mws.setInputServiceName("WS_A");
        mws.setOutputServiceName("WS_B");

        MatchedOperation mo = new MatchedOperation();
        mo.matchedElements = new ArrayList<>();
        mo.setInputOperationName("getWine");
        mo.setOutputOperationName("getDrink");

        MatchedElement me = new MatchedElement();
        me.setInputElement("Country");
        me.setOutputElement("Country");
        me.setScore(1.0);
        mo.matchedElements.add(me);

        me = new MatchedElement();
        me.setInputElement("Price");
        me.setOutputElement("Cost");
        me.setScore(0.9);
        mo.matchedElements.add(me);


        me = new MatchedElement();
        me.setInputElement("Region");
        me.setOutputElement("Area");
        me.setScore(0.85);
        mo.matchedElements.add(me);

        me = new MatchedElement();
        me.setInputElement("Color");
        me.setOutputElement("Colour");
        me.setScore(1.0);
        mo.matchedElements.add(me);

        mo.calculateOpScore();

        mws.matchedOperations.add(mo);
        mws.calculateWSScore();
        wsm.matchedWebServices.add(mws);

        createOutput(wsm);
    }
}
