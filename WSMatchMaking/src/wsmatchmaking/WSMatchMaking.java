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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
        File folder = new File("WSDLs");
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

        // TODO use inputWs and outputWs to get the actual descriptions
        
        testOutput();
    }
    
    private static void createOutput(WSMatching wsm) {
        JAXBContext context;
        Marshaller m;
        try {
            context = JAXBContext.newInstance(WSMatching.class);

            m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            m.marshal(wsm, new File("output.xml"));

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
