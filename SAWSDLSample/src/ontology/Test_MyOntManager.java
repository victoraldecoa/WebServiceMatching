package ontology;

import java.util.HashMap;
import java.util.Vector;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import sort.ClsQuickSort;

public class Test_MyOntManager {

    private static OWLOntologyManager manager = null;
    private static OWLOntology ontology = null;
    private static Reasoner reasoner = null;
    private static String ontLocation = "file:data/travel.owl";
    private static MyOntManager ontsum = null;

    public static void main(String[] args) {
        initializeOntology();

        Test_MyOntManager test = new Test_MyOntManager();
        test.test_Relationships();
        //test.test_subclassing_direct();
        //test.test_subclassing_indirect();
    }

    @BeforeClass
    public static void initializeOntology() {
        ontsum = new MyOntManager();
        manager = ontsum.initializeOntologyManager();
        ontology = ontsum.initializeOntology(manager, ontLocation);
        reasoner = ontsum.initializeReasoner(ontology, manager);
    }

    @Test
    public void test_loadOntology() {
        assertNotNull(ontsum);
        assertNotNull(ontology);
        assertNotNull(manager);
        assertNotNull(reasoner);
    }

    @Test
    public void test_loadClasses() {
        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);
        assertTrue(mapName_OWLClass.size() == 34);
    }

    @Test
    public void test_NullAncestor() {
        OWLClass[] superClasses = ontsum.getAncestorClasses(null, reasoner);
        assertTrue(superClasses.length == 0);
    }

    @Test
    public void test_AncestorStandard() {
        String clsName = "City";
        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);
        OWLClass cls = mapName_OWLClass.get(clsName.toLowerCase());
        assertNotNull(cls);

        OWLClass[] superClasses = ontsum.getAncestorClasses(cls, reasoner);
        assertTrue(superClasses.length == 3);
    }

    @Test
    public void test_subclassing_direct() {
        String clsName1 = "City";
        String clsName2 = "Capital";

        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);
        OWLClass cls1 = mapName_OWLClass.get(clsName1.toLowerCase());
        assertNotNull(cls1);
        OWLClass cls2 = mapName_OWLClass.get(clsName2.toLowerCase());
        assertNotNull(cls2);
        
        assertTrue(reasoner.isSubClassOf(cls2, cls1));  // this is
    }

    @Test
    public void test_subclassing_indirect() {
        String clsName1 = "Destination";
        String clsName2 = "Town";

        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);
        OWLClass cls1 = mapName_OWLClass.get(clsName1.toLowerCase());
        assertNotNull(cls1);
        OWLClass cls2 = mapName_OWLClass.get(clsName2.toLowerCase());
        assertNotNull(cls2);
        assertTrue(reasoner.isSubClassOf(cls2, cls1));  // it considers both immediate and ancestors of a class as its super concepts  
    }

    @Test
    public void test_Relationships() {
        String clsName1 = "Destination";
        String clsName2= "BackPackersDestination";
        
        //String clsName2 = "Accomadation";
        
//        String clsName1 = "Destination";
//        String clsName2 = "Activity";

        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);
        
        OWLClass cls1 = mapName_OWLClass.get(clsName1.toLowerCase());
        assertNotNull(cls1);
        OWLClass cls2 = mapName_OWLClass.get(clsName2.toLowerCase());
        assertNotNull(cls2);
        
        System.out.println(reasoner.isSubClassOf(cls1, cls2));
        System.out.println(reasoner.isSubClassOf(cls2, cls1));
        System.out.println(cls1+"-"+cls2); // for the moment cs1 is Cinput and cs2 is Coutput
              
        Vector<OWLObjectProperty> objprops = ontsum.findRelationship(cls1, cls2, reasoner);
        // assertTrue(objprops.size() == 1);
           assertTrue(objprops.size() == 0);
         // assertTrue(objprops.get(0).getURI().getFragment().equalsIgnoreCase("hasAccommodation"));
        //assertTrue(objprops.get(0).getURI().getFragment().equalsIgnoreCase("has subclass"));
       // assertTrue(objprops.get(0).getURI().getFragment().equalsIgnoreCase("hasActivity"));
        String Match = matching(cls1,cls2);
        System.out.println("Matching results are:   " + Match);
     }

    public String matching(OWLClass a, OWLClass b){ //a=cl1,b=cl2
        
         /* if  (reasoner.isSameAs(b.asOWLIndividual(), a.asOWLIndividual())== true){
            return "Exact";  //need to solve isssue
          }else*/ if (reasoner.isSubClassOf(b, a)){
              return "Plug-in"; 
          }else if(reasoner.isSubClassOf(a, b)){
              return "Subsumption"; 
          }else if(reasoner.isEquivalentClass(a, b)){
             return "Subsumption";    
          }
              //?? we should also do for has relation ship
        return "Not-Matched";
        
            
        }
    @Test
    public void testSorting() {
        ClsQuickSort sorter = new ClsQuickSort();

        String clsName = "City";
        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);
        OWLClass cls = mapName_OWLClass.get(clsName.toLowerCase());
        assertNotNull(cls);

        OWLClass[] superClasses = ontsum.getAncestorClasses(cls, reasoner);
        //sort classes based on their semantic relations, (grand child, child, parent, grandfather,...)
        sorter.sort(superClasses, reasoner);
        if (!validate(superClasses)) {
            fail("Should not happen");
        }
    }

    // find common super concepts of two classes
    @Test
    public void testLUB_Standard() {
        ClsQuickSort sorter = new ClsQuickSort();
        HashMap<String, OWLClass> mapName_OWLClass = ontsum.loadClasses(reasoner);

        String clsName1 = "City";
        OWLClass cls1 = mapName_OWLClass.get(clsName1.toLowerCase());
        assertNotNull(cls1);
        OWLClass[] superClasses1 = ontsum.getAncestorClassesPlusItself(cls1, reasoner);
        sorter.sort(superClasses1, reasoner);
        assertTrue(validate(superClasses1));

        String clsName2 = "Capital";
        OWLClass cls2 = mapName_OWLClass.get(clsName2.toLowerCase());
        assertNotNull(cls2);
        OWLClass[] superClasses2 = ontsum.getAncestorClassesPlusItself(cls2, reasoner);
        sorter.sort(superClasses2, reasoner);
        assertTrue(validate(superClasses2));

        OWLClass[] common = ontsum.getCommon(superClasses1, superClasses2);
        sorter.sort(common, reasoner);

        assertTrue(common.length == 4);
        assertTrue(common[0] == cls1);
    }

    private boolean validate(OWLClass[] classes) {
        for (int i = 0; i < classes.length - 1; i++) {
            if (!reasoner.isSubClassOf(classes[i], classes[i + 1]) && reasoner.isSubClassOf(classes[i + 1], classes[i])) {
                return false;
            }
        }
        return true;
    }

    public void printResult(OWLClass[] classes) {
        for (int i = 0; i < classes.length; i++) {
            System.out.print(classes[i].getURI().getFragment() + " , ");
        }
        System.out.println();
    }
}
