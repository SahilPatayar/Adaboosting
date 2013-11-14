package homework2;

public class Main
{
	
	/*
	 * Main Method
	 * 
	 * */
	
	
	public static void main(String[] args)
	{
		
		// Binary Iterations with input file path n output file path
		
		File_info ob1 = new File_info("data1.txt" , "binary.txt" );
		
		BinaryIterations[] B = new BinaryIterations[ob1.T];
		
		B[0] = new BinaryIterations(ob1);
		
		for(int i = 1; i < ob1.T ; i++)
		{
			B[i] = new BinaryIterations(ob1, B[( i - 1)], (i + 1) );
		}
		
		// For Real Adaboosting with input file and output file name
		
		File_info ob2 = new File_info("realdata.txt" , "real.txt" );
		
		RealIterations[] r = new RealIterations[ob2.T];
		
		r[0] = new RealIterations(ob2);
		
		for(int i = 1; i < ob2.T; i++)
		{
			r[i] = new RealIterations(ob2, r[(i - 1)], i + 1);
		}		
	}

	
}
