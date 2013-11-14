package homework2;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class RealIterations 
{
	public float G; 
	public int h_t;   // Selected Hypothesis
	public float z_t; // Normalization Factor
	
	
	public int iteration = 0;
	public float pr_plus;
	public float pr_negative;
	public float pw_plus;
	public float pw_negative;
	
	public float c_plus;
	public float c_negative;
	
	public float[] preNormProb;
	public float[] newProb;
	public float[] f_t;
	
	public int mistakes;
	public float e_t;    // error of boosted classifier
	public float bound;
	
	/*
	 * Constructor with parameter File_info
	 * 
	 * */	
	
	public RealIterations(File_info f)
	{
		int index = 0;
		float min = 1;
		iteration++;
		preNormProb = new float[f.T];
		newProb = new float[f.T];
		f_t = new float[f.T];
		
		//System.out.println("size " + f.weakClassifier.values());
		
		
		for(int i = 0; i < f.weakClassifier.size(); i++)
		{
			float temp_Pr_plus = 0;
			float temp_Pr_negative = 0;
			float temp_Pw_plus = 0;
			float temp_Pw_negative = 0;
			
			float error = 0;	
			
			String classifier = f.weakClassifier.get(i).toString();
			
			//System.out.println("Class : " + classifier);
			
			String[] temp = classifier.split(" ");
			String sign = temp[0];
			float threshhold = Float.valueOf(temp[1]);
						
			int value = 0;
			
			// For x < threshold
			if( sign.equals("<") )
			{				
				for(int j = 0; j < f.n ; j++)
				{
					if( f.x[j] < threshhold)
					{
						value = 1;						
						
						if(f.y[j] == value)
							temp_Pr_plus += f.p[j];
						else
							temp_Pw_negative += f.p[j];
					}
					else
					{
						value = -1;
						
						if(f.y[j] == value)
							temp_Pr_negative += f.p[j];
						else
							temp_Pw_plus += f.p[j];
					}
				}		
								
			}
			else    // for x > threshold
			{				
				for(int j = 0; j < f.n ; j++)
				{
					if( f.x[j] > threshhold)
					{
						value = 1;						
						
						if(f.y[j] == value)
							temp_Pr_plus += f.p[j];
						else
							temp_Pw_negative += f.p[j];
					}
					else
					{
						value = -1;
						
						if(f.y[j] == value)
							temp_Pr_negative += f.p[j];
						else
							temp_Pw_plus += f.p[j];
					}
				}
			}			
						
			error = (float) ( ( Math.sqrt( ( temp_Pr_plus * temp_Pw_negative) ) + (Math.sqrt( (temp_Pr_negative * temp_Pw_plus ) ) ) ) );
			
			//System.out.println("error at " + i + " : " + error);
			
			if(error < min)
			{
				index = i;
				min = error;
				pr_plus = temp_Pr_plus;
				pr_negative = temp_Pr_negative;
				pw_plus = temp_Pw_plus;
				pw_negative = temp_Pw_negative;			
			}			
		}
		
		G = min;
		h_t = index;
		
		//System.out.println("G : " + G + " h-t : " + h_t);
		
		c_plus = (float) ( ( Math.log( (double) ( (pr_plus  + f.eplison) / (pw_negative + f.eplison  ) ) )   ) ) ;
		c_plus = c_plus /2;
		
		c_negative = (float) (  Math.log( (double) ( (pw_plus + f.eplison) / (pr_negative + f.eplison ) ) ) );
		c_negative = c_negative / 2;
		
		z_t = ( (float) ( (float)( Math.sqrt( (double) (pr_plus * pw_negative) ) ) )+ ( (float) ( Math.sqrt( (double) (pr_negative * pw_plus) )  ) ) );
		z_t = 2 * z_t;		
			
		updateProbabilities(f , h_t);
					
		//System.out.println("C+ : " + c_plus + "\nC- : " + c_negative + "\nZ-t : " + z_t);
		
		// Normalizing Probability
		for(int i = 0; i < f.n ; i++)
		{
			newProb[i] = preNormProb[i] / z_t;		
			//System.out.println("new Prob : " + newProb[i] + " preN : " + preNormProb[i]);
		}		
		bound = z_t;	
		calError(f);
		writeToFile( f , f.outPath);
		
	} // Real Iteration( File_info f) ends here
	
	
	/*
	 * 
	 * Updating probabilities here for first iteration
	 * 
	 * */
	
	
	public void updateProbabilities(File_info f, int index)	
	{
		String classifier = f.weakClassifier.get(index).toString();
				
		String[] temp = classifier.split(" ");
		String sign = temp[0];
		float threshhold = Float.valueOf(temp[1]);
		
		int value = 0;
			
		if(sign.equals("<"))
		{			
			for(int j = 0; j < f.n ; j++)
			{
				if( f.x[j] < threshhold)
				{
					value = 1;						
					preNormProb[j] = f.p[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_plus) )   )  );
					f_t[j] = c_plus;					
				}
				else
				{
					value = -1;					
					preNormProb[j] = f.p[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_negative) )   )  );
					f_t[j] = c_negative;					
				}
			}
			
		} // if end
		else
		{
			for(int j = 0; j < f.n; j++)
			{
				if(f.x[j] > threshhold)
				{
					value = 1;
					if(f.y[j] == value)
					{
						preNormProb[j] = f.p[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_plus) )   )  );
						f_t[j] = c_plus;						
					}
					else
					{
						preNormProb[j] = f.p[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_negative) )   )  );
						f_t[j] = c_negative;
					}
					
				}
				else
				{
					value = -1;
					
					if(f.y[j] == value)
					{
						preNormProb[j] = f.p[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_plus) )   )  );
						f_t[j] = c_plus;
					}
				else
					{
						preNormProb[j] = f.p[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_negative) )   )  );
						f_t[j] = c_negative;
					}
					
				}
				
				
			}
			
		}	
			
	} // Updating Probability ends here
	
	
	/*
	 * Calculating error of boosted classifier
	 * 
	 * */
	
	public void calError(File_info f)
	{
		for(int i=0 ; i < f.n; i++)
	    {
			
	      if(f_t[i] > 0)
	      {	    	  
	    	  
	        if(f.y[i] != 1)	        
	          mistakes += 1;
	              
	      }
	      if(f_t[i] < 0)
	      {
	    	  
	        if(f.y[i] != -1)	        
	          mistakes += 1;        
	        
	      }
	      
	      e_t =  (float) mistakes/ f.n;
	    }		
		
	}     // Calculate Error ends here 
	
	/*
	 * 
	 * Constructor with different arguments for iteration > 1
	 * 
	 * 
	 * */
	
	public RealIterations(File_info f, RealIterations prev, int itr)
	{
		iteration = itr;
		int index = 0;
		float min = 1;	
		preNormProb = new float[f.T];
		newProb = new float[f.T];
		f_t = new float[f.T];
					
		for(int i = 0; i < f.weakClassifier.size(); i++)
		{
			float temp_Pr_plus = 0;
			float temp_Pr_negative = 0;
			float temp_Pw_plus = 0;
			float temp_Pw_negative = 0;
			
			float error = 0;	
			
			String classifier = f.weakClassifier.get(i).toString();
									
			String[] temp = classifier.split(" ");
			String sign = temp[0];
			float threshhold = Float.valueOf(temp[1]);
					
			int value = 0;	
			
			// For x < threshold
			if( sign.equals("<") )
			{				
				for(int j = 0; j < f.n ; j++)
				{
					if( f.x[j] < threshhold)
					{
						value = 1;						
						
						if(f.y[j] == value)
							temp_Pr_plus += prev.newProb[j];
						else
							temp_Pw_negative += prev.newProb[j];
					}
					else
					{
						value = -1;
						
						if(f.y[j] == value)
							temp_Pr_negative += prev.newProb[j];
						else
							temp_Pw_plus += prev.newProb[j];
					}
				}		
								
			}
			else    // for x > threshold
			{				
				for(int j = 0; j < f.n ; j++)
				{
					if( f.x[j] > threshhold)
					{
						value = 1;						
						
						if(f.y[j] == value)
							temp_Pr_plus += prev.newProb[j];
						else
							temp_Pw_negative += prev.newProb[j];
					}
					else
					{
						value = -1;
						
						if(f.y[j] == value)
							temp_Pr_negative += prev.newProb[j];
						else
							temp_Pw_plus += prev.newProb[j];
					}
				}
			}		
			
			error = (float) ( ( Math.sqrt( ( temp_Pr_plus * temp_Pw_negative) ) + (Math.sqrt( (temp_Pr_negative * temp_Pw_plus ) ) ) ) );
			
			//System.out.println("error at " + i + " : " + error);
			
			if(error < min)
			{
				index = i;
				min = error;
				pr_plus = temp_Pr_plus;
				pr_negative = temp_Pr_negative;
				pw_plus = temp_Pw_plus;
				pw_negative = temp_Pw_negative;			
			}			
		}
		
		G = min;
		h_t = index;
		
		
		
		c_plus = (float) ( ( Math.log( (double) ( (pr_plus  + f.eplison) / (pw_negative + f.eplison  ) ) )   ) ) ;
		c_plus = c_plus /2;
		
		c_negative = (float) (  Math.log( (double) ( (pw_plus + f.eplison) / (pr_negative + f.eplison ) ) ) );
		c_negative = c_negative / 2;
		
		z_t = ( (float) ( (float)( Math.sqrt( (double) (pr_plus * pw_negative) ) ) )+ ( (float) ( Math.sqrt( (double) (pr_negative * pw_plus) )  ) ) );
		z_t = 2 * z_t;		
		
		//System.out.println("G : " + G + " h-t : " + h_t + " zt : " +z_t);		
		
		updateProbabilities(f , h_t, prev);
		
		// Normalizing Probability
		for(int i = 0; i < f.n ; i++)
			{
				newProb[i] = preNormProb[i] / z_t;		
				//System.out.println("new Prob : " + newProb[i] + " preN : " + preNormProb[i]);
			}		
		bound = z_t * prev.z_t;	
		
		calError(f);
		writeToFile( f , f.outPath);		
	
	}     // Constructor RealIterations with previous argument ends here
	
	/*
	 * 
	 * Updating probabilities respective to previous RealIteration Object's prob
	 * 
	 * */
	
	public void updateProbabilities(File_info f, int index, RealIterations prev)	
	{
		String classifier = f.weakClassifier.get(index).toString();
				
		String[] temp = classifier.split(" ");
		String sign = temp[0];
		float threshhold = Float.valueOf(temp[1]);
			
		if(sign.equals("<"))
		{			
			for(int j = 0; j < f.n ; j++)
			{
				if( f.x[j] < threshhold)
				{
					preNormProb[j] = prev.newProb[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_plus) )   )  );
					f_t[j] = prev.f_t[j] + c_plus;					
				}
				else
				{
					preNormProb[j] = prev.newProb[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_negative) )   )  );
					f_t[j] = prev.f_t[j] + c_negative;					
				}
			}		
		}
		else
		{			
				for(int j = 0; j < f.n; j++)
				{
					if(f.x[j] > threshhold)
					{
							preNormProb[j] = prev.newProb[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_plus) )   )  );
							f_t[j] = prev.f_t[j] + c_plus;					
					}
					else
					{
						preNormProb[j] = prev.newProb[j] * (  (float)    (  Math.exp( (double) ( -1 * f.y[j] * c_negative) )   )  );
							f_t[j] = prev.f_t[j] + c_negative;						
					}					
				}
				
			}	
			
	} // Updating Probability  with previous argument ends here
	
	/*
	 * 
	 * Writing the calculations on the text file name 'real.txt'
	 * 
	 * */
	
	public void writeToFile(File_info f , String path)
	{
		
		try
		{
			boolean flag;
			if(iteration == 1)
				flag = false;
			else
				flag = true;
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(path, flag));
						
			writer.write("Iteration : " + iteration);
			writer.newLine();
						
			writer.write("The selected weak classifier ht : " + " ( x " + f.weakClassifier.get(h_t).toString() + " )");
			writer.newLine();
			
			writer.write("The G error value of h(x) : " + G);
			writer.newLine();
			
			writer.write("The Weight of c+ : " + c_plus + " c- : " + c_negative);
			writer.newLine();
			
			writer.write("The probabilities normalization factor Zt : " + z_t);
			writer.newLine();
			
			writer.write("The probabilities after normalization (p) : ");
			writer.newLine();
			for(int i = 0 ; i < newProb.length ; i++)
			{
				writer.write( Float.toString(newProb[i]) );
				writer.write(" ");
			}
			writer.newLine();
			
			writer.write("The values ft(x) for each one of the examples : ");			
			writer.newLine();
			for(int i = 0 ; i < f_t.length ; i++)
			{
				writer.write( Float.toString(f_t[i]) );
				writer.write(" ");
			}			
			writer.newLine();
			
			writer.write("The error of the boosted classifier Et : " + e_t);
			writer.newLine();
			
			writer.write("The bound on Et : " + bound);
			writer.newLine();
			writer.write("--------------------------------------------------------");
			writer.newLine();
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in Writing File. ");
		}		
	} // Writing files ends here	
	
	
}         // Main Class ends here
