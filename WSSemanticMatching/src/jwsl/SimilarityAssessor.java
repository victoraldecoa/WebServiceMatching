/**
 * 
 *  
 * Java WordNet Similarity Library
 * authors: Giuseppe Pirro and Nuno Seco
 * 
 * for information contact Giuseppe at  gpirro@deis.unical.it
 *
 */
package jwsl;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class SimilarityAssessor 
{

	public static final String RESNIK_METRIC="Resnik";
	public static final String JIANG_METRIC="Jiang";
	public static final String LIN="Lin";
	public static final String PIRRO_SECO_METRIC="Pirr� and Seco";
	
	
	
	
	/**
	 * Holds a reference to an instance of an Index Broker.
	 */
	private IndexBroker _broker;

	/**
	 * The constructor. Obtains an instance of an Index Broker.
	 */
	public SimilarityAssessor() {
		_broker = IndexBroker.getInstance();
	}

	/**
	 * Calculates the similarity between two specific senses.
	 * 
	 * @param word1
	 *            String
	 * @param senseForWord1
	 *            int The sense number for the first word
	 * @param word2
	 *            String
	 * @param senseForWord2
	 *            int The sense number for the second word
	 * @throws WordNotFoundException
	 *             An exception is thrown if one of the words is not contained
	 *             in the WordNet dictionary.
	 * @return double The degree of similarity between the words; 0 means no
	 *         similarity and 1 means that they may belong to the same synset.
	 */
	public double getSenseSimilarity(String word1, int senseForWord1,
			String word2, int senseForWord2, String metric) throws WordNotFoundException {
		Hits synsets1 = _broker.getHits(word1 + "." + senseForWord1);
		Hits synsets2 = _broker.getHits(word2 + "." + senseForWord2);

		if (synsets1.length() == 0) {
			throw new WordNotFoundException("Word " + word1 + "."
					+ senseForWord1 + " is not in the dictionary.");
		}

		if (synsets2.length() == 0) {
			throw new WordNotFoundException("Word " + word2 + "."
					+ senseForWord2 + " is not in the dictionary.");
		}

		try
		{
			return getSimilarity(synsets1.doc(0), synsets2.doc(0),  metric);

		} catch (IOException ex) {
			ex.printStackTrace();
			return 0.0;
		}

	}
	
	/**
	 * Does the actual calculation between synsets.
	 * 
	 * @param synset1
	 *            Document
	 * @param synset2
	 *            Document
	 * @return double
	 */
	private double getSimilarity(Document synset1, Document synset2, String metric) 
	{
		double msca = getBestMSCAValue(synset1, synset2);

		if (msca == -1) 
		{
			return 0;
		}

	
		if(metric.equalsIgnoreCase(JIANG_METRIC))
		 return getJiangSimilarity(synset1, synset2);
		else

			if(metric.equalsIgnoreCase(LIN))
			 return getLinSimilarity(synset1, synset2);
		
			else

				if(metric.equalsIgnoreCase(PIRRO_SECO_METRIC))
				 return getPirroAndSecoSimilarity(synset1, synset2);
				else

					if(metric.equalsIgnoreCase(RESNIK_METRIC))
					 return getResnikSimilarity(synset1, synset2);
		return -1;
	
	}

	

	/**
	 * Calculates the similarity between the two words
	 * 
	 * @param word1
	 *            String
	 * @param word2
	 *            String
	 * @throws WordNotFoundException
	 *             An exception is thrown if one of the words is not contained
	 *             in the WordNet dictionary.
	 * @return double The degree of similarity between the words; 0 means no
	 *         similarity and 1 means that they may belong to the same synset.
	 */
	public double getSimilarity(String word1, String word2, String metric)
			throws WordNotFoundException {
		Hits synsets1 = _broker.getHits(word1 + ".*");
		Hits synsets2 = _broker.getHits(word2 + ".*");

		if (synsets1.length() == 0) {
			throw new WordNotFoundException("Word " + word1
					+ " is not in the dictionary.");
		}

		if (synsets2.length() == 0) {
			throw new WordNotFoundException("Word " + word2
					+ " is not in the dictionary.");
		}

		double current = 0;
		double best = 0;

		try {
			for (int i = 0; i < synsets1.length(); i++) {
				for (int j = 0; j < synsets2.length(); j++) 
				{
					
					current = getSimilarity(synsets1.doc(i), synsets2.doc(j),metric);

					if (current > best) {
						best = current;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return best;
	}

	/**
	 * Does the actual calculation between synsets.
	 * 
	 * @param synset1
	 *            Document
	 * @param synset2
	 *            Document
	 * @return double
	 */
	private double getJiangSimilarity(Document synset1, Document synset2) {
		double msca = getBestMSCAValue(synset1, synset2);

		if (msca == -1) {
			return 0;
		}

		double ic_synset1 = ((Double.parseDouble(synset1
				.get(IndexBroker.INFORMATION_CONTENT))));

		double ic_synset2 = ((Double.parseDouble(synset2
				.get(IndexBroker.INFORMATION_CONTENT))));

		return 1 - (((ic_synset1 + ic_synset2) - 2 * msca)) / 2;
	}

	private double getResnikSimilarity(Document synset1, Document synset2) {
		double msca = getBestMSCAValue(synset1, synset2);

		if (msca == -1) {
			return 0;
		}

		return msca;
	}

	private double getPirroAndSecoSimilarity(Document synset1, Document synset2) {
		double msca = getBestMSCAValue(synset1, synset2);

		if (msca == -1) {
			return 0;
		}

		double ic_synset1 = ((Double.parseDouble(synset1
				.get(IndexBroker.INFORMATION_CONTENT))));

		double ic_synset2 = ((Double.parseDouble(synset2
				.get(IndexBroker.INFORMATION_CONTENT))));

		return (2 + (3 * msca) - ic_synset1 - ic_synset2) / 3;

	}

	private double getLinSimilarity(Document synset1, Document synset2) {
		double msca = getBestMSCAValue(synset1, synset2);

		if (msca == -1) {
			return 0;
		}

		double ic_synset1 = ((Double.parseDouble(synset1
				.get(IndexBroker.INFORMATION_CONTENT))));

		double ic_synset2 = ((Double.parseDouble(synset2
				.get(IndexBroker.INFORMATION_CONTENT))));

		return (2 * msca) / (ic_synset1 + ic_synset2);

	}

	/**
	 * Discovers the best Most Specific Common Abstraction (MSCA) value for the
	 * two given Synsets. Note that synsets are represented as Lucene documents.
	 * 
	 * @param doc1
	 *            Document One synset
	 * @param doc2
	 *            Document Another synset
	 * @return double The value of the MSCA with the highest IC value
	 */
	private double getBestMSCAValue(Document doc1, Document doc2) {
		double current = 0;
		double best = 0;
		String offset;

		LinkedList intersection = getIntersection(doc1
				.getValues(IndexBroker.HYPERNYM)[0].split(" "), doc2
				.getValues(IndexBroker.HYPERNYM)[0].split(" "));

		if (intersection.isEmpty()) {
			return -1;
		}

		while (!intersection.isEmpty()) {
			offset = intersection.removeFirst().toString();

			current = getIC(offset);
			if (current > best) {
				best = current;
			}
		}

		return best;
	}

	/**
	 * Obtains the Information Content (IC) value for a given synset offset.
	 * 
	 * @param offset
	 *            String The offset to be queried
	 * @return double The IC value
	 */
	private double getIC(String offset) {
		Hits synset = _broker.getHits(IndexBroker.SYNSET + ":" + offset);
		try {
			return Double.parseDouble(synset.doc(0).get(
					IndexBroker.INFORMATION_CONTENT));
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0.0;
		}
	}

	/**
	 * Gets a list of strings that are contained in both arrays. The strings in
	 * the arrays represent different synsets.
	 * 
	 * @param values1
	 *            String[] An array of synsets
	 * @param values2
	 *            String[] Another array of synsets.
	 * @return LinkedList The list of synsets common to each array
	 */
	private LinkedList getIntersection(String[] values1, String[] values2) {
		LinkedList intersection = new LinkedList();

		for (int i = 0; i < values1.length; i++) {
			for (int j = 0; j < values2.length; j++) {
				if (values1[i].equals(values2[j])) {
					intersection.add(values1[i]);
					break;
				}
			}
		}

		return intersection;
	}

}
