package bn.sln;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.Domain;
import bn.core.RandomVariable;
import bn.inference.Inferencer;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

public class RSampleInference implements Inferencer {
	
	private int N;
	
	public RSampleInference() {
	}
	
	void setN(int n) {
		this.N = n;
	}
	
	public Assignment priorSample(BayesianNetwork bn) {
		Assignment e = new Assignment();
		
		// get all random variables in bn
		List<RandomVariable> rvs = bn.getVariableListTopologicallySorted();
		
		for (RandomVariable rv : rvs) {
			// generate a random variable between 0 and 1
			Random random = new java.util.Random();
			double randnum = random.nextDouble();
			
			Domain dm = rv.getDomain();
			// for each value in RV's domain
			for (Object val : dm) {	
				e.set(rv, val);
				randnum -= bn.getProb(rv, e);
				if (randnum <= 0) {
					break;
				}
			}
		}
		
		return e;
	}
	
	@Override
	public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e) {

		// store counts for each value
		Distribution valueCnt = new Distribution();
		for (Object val : X.getDomain()) {
			valueCnt.put(val, 0);
		}
		
		for(int j = 1; j <= N; j++) {
			Assignment eps = priorSample(bn);
			// consistency test
			if (eps.isConsistent(e)) {
				valueCnt.put(eps.get(X), valueCnt.get(eps.get(X)) + 1);
			}
		}
		
		valueCnt.normalize();
		
		return valueCnt;
		
	}
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

		//Sample size
		int N = Integer.parseInt(args[0]);
		
		// load bn xml or bif		
		BayesianNetwork network = new BayesianNetwork();
		if(args[1].contains(".xml")) {
			XMLBIFParser parser = new XMLBIFParser();
			network = parser.readNetworkFromFile("src/bn/examples/" + args[1]);
		}else {
			File f= new File("src/bn/examples/" + args[1]);
			BIFParser parser = new BIFParser(new FileInputStream(f));
			network = parser.parseNetwork();
		}
		
		// read in query variable X
		RandomVariable X = network.getVariableByName(args[2]);

		// read in observed values for variables E
	    Assignment e = new Assignment();
	    for (int i = 3; i < args.length; i += 2) {
	    	e.set(network.getVariableByName(args[i]), args[i + 1]);
	    }
	    
	    RSampleInference rsinfer = new RSampleInference();
	    rsinfer.setN(N);
	    
	    // solve
	    System.out.println(rsinfer.ask(network, X, e));
	}
}
