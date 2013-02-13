
package jwsl;



/**
 * 
 *  
 * Java WordNet Similarity Library
 * authors: Giuseppe Pirro and Nuno Seco
 * 
 * for information contact Giuseppe at  gpirro@deis.unical.it
 *
 */
public class TestSemanticSymilarity
{

	
	/*
	 * this is a running example of how the library works
	 * You just have to specify two words and the metric you want to use
	 */
	
	
	public static void main(String argv[]) 
	{

		SimilarityAssessor sim=new SimilarityAssessor();
		
		String word1 = "car";
		String word2 = "wheel";
		
		
			
		// you can choose the proper metric among the implemented one by specifying its name.
		String metric=SimilarityAssessor.PIRRO_SECO_METRIC;
		
		double score;
		try {
			score = sim.getSimilarity(word1, word2, metric);
		
		

			System.out.println("Semantic Similarity between " + word1 + " and " + word2 + " score "+score);
			
			
		} catch (WordNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	

}
