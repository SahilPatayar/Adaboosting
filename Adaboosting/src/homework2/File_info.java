package homework2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class File_info
{
	public int T, n;
	public float eplison;
	public float[] x;
	public int[] y;
	public float[] p;	
	public HashMap<Integer,String> weakClassifier;
	
	
	/*
	 * Constructor which is initializing and making a pool of classifiers
	 * 
	 * */
	
	
	public File_info(String path)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			
			String[] split = line.split(" ");
			T = Integer.parseInt(split[0]);
			n = Integer.parseInt(split[1]);
			eplison = Float.parseFloat(split[2]);
			
			//System.out.println("T : " + T + " n : " + n + " eplison : " + eplison);
			
			x = new float[n];
			y = new int[n];
			p = new float[n];
			
			
			// Initializing x
			line = reader.readLine();
			//System.out.println("line " + line);
			String[] split2 = line.split(" ");
			for(int i = 0; i < split2.length ; i++)
			{				
				x[i] = Float.valueOf(split2[i]);	;	
				//System.out.println(i + " x : " + x[i]);
			}
			
			
			// Initializing y
			line = reader.readLine();
			
			String[] split3 = line.split(" ");
			
			for(int i = 0; i < split3.length ; i++)							
				y[i] = Integer.valueOf(split3[i]);			
				
			
			
			// Initializing Probabilities p
			
			line = reader.readLine();
			String[] split4 = line.split(" ");
			for(int i = 0; i < split4.length ; i++)
			{
				p[i] = Float.valueOf(split4[i]);
				//System.out.println(i + " p : " + p[i]);
			}			
			
			reader.close();
			
			weakClassifier = new HashMap<Integer, String>();
			
			
            for(int i=0 ; i < n-1; i++)
            {
            	//System.out.println("Iteration " + i);
            	if( (y[i] * y[i+1]) == -1)
            	{
            	  
            		int count = 0;
                
            		for(int j=0; j<(i+1);j++)
            		{
            			if(y[j]==-1)
            			{
            				count = count + 1;
            				//System.out.println(i + " count in first : " + count);
            			}
            		}

            		for(int j= (i+1) ; j < n; j++)
            		{
            			if(y[j] == 1)
            			{            				
            				count = count + 1;     
            				//System.out.println(i + " count in second : " + count);
            			}
            		}
            		
            		if(count < (n - count) )
            		{
            			//System.out.println(i + " In if val : " + (((float)x[i]+x[i+1])/2));
            			weakClassifier.put(weakClassifier.size(), "<" + " " + ( ( (float) x[i]+x[i+1]) /2 ) );
            			
            		}
            		else
            		{
            			//System.out.println(i + " In else val : " + (((float)x[i]+x[i+1])/2));
            			weakClassifier.put(weakClassifier.size(), ">" + " " + ( ( (float) x[i]+x[i+1]) /2 ) );
            		}                

            	}
            }    
           
               // System.out.println(weakClassifier.size());
               // System.out.println(weakClassifier.values());
            
			
			
			
		}
		catch(Exception e)
		{
			System.out.println("Unable to read the file....");
		}
	}
	
	
	
	
	
	
	/*
	 * Main Method
	 * 
	 * */
	
	
	public static void main(String[] args)
	{
		File_info a = new File_info("data1.txt");
		BinaryIterations[] B = new BinaryIterations[a.T];
		B[0] = new BinaryIterations(a);
		for(int i = 1; i < B.length ; i++)
		{
			B[i] = new BinaryIterations(a, B[( i - 1)], (i + 1) );
		}
		
	}
	
}
