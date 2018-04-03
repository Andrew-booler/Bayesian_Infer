package bn.sln;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.inference.Inferencer;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

public class RSampleInference implements Inferencer {
	
	private int N;
	public RSampleInference() {
		// TODO 自动生成的构造函数存根
	}
	
	void setN(int n) {
		this.N=n;
	}
	@Override
	public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e) {
		// TODO 自动生成的方法存根
		return null;
	}
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
		
		
		
		//Sample size
		int N = Integer.parseInt(args[0]);
		
						
						
		// load bn xml or bif		
		BayesianNetwork network;
		if(args[0].contains(".xml")) {
			XMLBIFParser parser = new XMLBIFParser();
			network = parser.readNetworkFromFile("src/bn/examples/"+args[1]);
		}else {
			File f= new File("src/bn/examples/"+args[1]);
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
	    
	    RSampleInference rinfer = new RSampleInference();
	    rinfer.setN(N);
	    // solve
	    System.out.println(rinfer.ask(network,X, e));
	}
}
