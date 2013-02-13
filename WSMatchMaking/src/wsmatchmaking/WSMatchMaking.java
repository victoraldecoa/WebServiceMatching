/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsmatchmaking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.xml.sax.InputSource;

/**
 *
 * @author victor & alex
 */
public class WSMatchMaking {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, Exception {
        try {
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

            WSMatching wsmatching = new WSMatching();
            wsmatching.matchedWebServices = new ArrayList<>();

            MatchedWebService mws = new MatchedWebService();
            mws.matchedOperations = new ArrayList<>();

            Definition d_i = WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(inputWs));
            Definition d_o = WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(outputWs));

            Map m = d_i.getAllServices();
            Service s_i = null;
            for (Object o : m.values()) {
                s_i = (Service) o;

                mws.setInputServiceName(s_i.getQName().getLocalPart());
            }
            if (s_i == null) {
                throw new Exception("No services found on the input");
            }

            m = d_o.getAllServices();
            Service s_o = null;
            for (Object o : m.values()) {
                s_o = (Service) o;

                mws.setOutputServiceName(s_o.getQName().getLocalPart());
            }

            if (s_o == null) {
                throw new Exception("No services found on the output");
            }

            Port p_i = null;
            for (Object o : s_i.getPorts().values()) {
                p_i = (Port) o;
            }

            if (p_i == null) {
                throw new Exception("No ports found on the input");
            }

            Port p_o = null;
            for (Object o : s_o.getPorts().values()) {
                p_o = (Port) o;
            }

            if (p_o == null) {
                throw new Exception("No ports found on the output");
            }

            for (Object o1 : p_i.getBinding().getBindingOperations()) {
                BindingOperation o_i = (BindingOperation) o1;
                for (Object o2 : p_o.getBinding().getBindingOperations()) {
                    BindingOperation o_o = (BindingOperation) o2;

                    MatchedOperation mo = new MatchedOperation();
                    mo.matchedElements = new ArrayList<>();
                    mo.setInputOperationName(o_i.getName());
                    mo.setOutputOperationName(o_o.getName());

                    for (Object o3 : o_i.getOperation().getInput().getMessage().getParts().values()) {
                        Part e_i = (Part)o3;
                        for (Object o4 : o_o.getOperation().getOutput().getMessage().getParts().values()) {
                            Part e_o = (Part)o4;
                            MatchedElement me = new MatchedElement();
                            me.setInputElement(e_i.getName());
                            me.setOutputElement(e_o.getName());

                            if (editDistance) {
                                me.calculateScoreUsingEditDistance();
                            } else {
                                me.calculateScoreUsingWordNet();
                            }
                            
                            // if the element doesn't match, then the whole operation doesn't match
                            if (me.getScore() <= 0.8) {
                                break;
                            }
                            
                            mo.matchedElements.add(me);
                        }
                    }

                    if (!mo.matchedElements.isEmpty()){
                        mo.calculateOpScore();
                        mws.matchedOperations.add(mo);
                    }
                }
            }

            if (!mws.matchedOperations.isEmpty()) {
                mws.calculateWSScore();
                wsmatching.matchedWebServices.add(mws);
            }

            createOutput(wsmatching);

            //testOutput();
        } catch (WSDLException ex) {
            Logger.getLogger(WSMatchMaking.class.getName()).log(Level.SEVERE, null, ex);
        }
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
