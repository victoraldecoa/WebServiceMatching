package jwsl;

/**
 * 
 *  
 * Java WordNet Similarity Library
 * authors: Giuseppe Pirro and Nuno Seco
 * 
 * for information contact Giuseppe: gpirro AT deis.unical.it
 *
 */

public class WordNotFoundException extends Exception {

	/**
	 * The constructor.
	 * 
	 * @param err
	 *            String The error message that is to be shown with the
	 *            exception.
	 */
	public WordNotFoundException(String err) {
		super(err);
	}

}
