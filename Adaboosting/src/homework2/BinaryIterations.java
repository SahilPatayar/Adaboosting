package homework2;

import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;




public class BinaryIterations
{
	
	public float e_t; // error of weak classifier
	public float alpha_t; // weight of weak classifier
	public int h_t; // selected weak classifier ID
	public float z_t; // normalizing factor
	public float[] newProb; // probabilities after normalization
	public float[] preNormProb;
	public int mistake;
	public float Error_t;
	public HashMap<String, Float> boostedClassifier;
	public float bound;
	public int iteration = 0;
	
	
	/*
	 * 
	 * Constructor with one argument
	 * 
	 * */
	
	public BinaryIterations(File_info f)
	{
		float min = 1;
		int index = 0;
		iteration++;
		
		preNormProb = new float[f.n];
		newProb = new float[f.n];
		boostedClassifier = new HashMap<String, Float>();
		 
		for(int i = 0; i < f.weakClassifier.size() ; i++)
		{
			float error = 0;			
			boostedClassifier.put(f.weakClassifier.get(i).toString(), (float) 0.0);
			String classifier = f.weakClassifier.get(i).toString();
			
			//System.out.println("Class : " + classifier);
			
			String[] temp = classifier.split(" ");
			String sign = temp[0];
			float threshhold = Float.valueOf(temp[1]);
					
			int value;
			
			if(sign.equals("<"))
			{
				value = 1;
			}
			else
			{
				value = -1;
			}
			
			for(int j = 0; j < f.n; j++)
			{
				if(f.x[j] < threshhold)
				{
					if(f.y[j] != value)
					{
						error = error + f.p[j];
					}					
				}
				else
				{
					if(f.y[j] != (value * -1))
		              {
		                 error=error + f.p[j]; 
		              }
				}				
			}
			
			if(min > error)
			{
				min = error;
				index = i;
			}			
		}	
		
		e_t = min;
		h_t = index;
		alpha_t = (float) ( Math.log( (double) ( (1 - e_t) / e_t) ) );		
		alpha_t = alpha_t / 2;
		
		boostedClassifier.remove(f.weakClassifier.get(index).toString());
		boostedClassifier.put(f.weakClassifier.get(index).toString(), alpha_t);
		
		float q1 = (float) Math.exp((double) (-alpha_t) ); // For Correctly classified 
		
		float q2 = (float) Math.exp((double) (alpha_t) ); // For wrongly Classified 
		
		updateProbabiltity(f, index, q1,q2);		
		
		//CalCulating Z_t
		z_t = 0;		
		for(int p = 0; p < preNormProb.length; p++)		
			z_t = z_t + preNormProb[p];		
		
		bound = z_t;
		
		
		for(int i = 0; i < newProb.length ; i++)
			newProb[i] = preNormProb[i] / z_t;		
		
		boostedClassifier.put( (f.weakClassifier.get(h_t).toString() ), alpha_t);
		
		
		Error_t =  ((float) mistake) / f.n;
		
		//System.out.println("Iteration : " + iteration);
		
		//System.out.println("e_t : " + e_t + "\nht : " + h_t + "\naplha : " + alpha_t + "\nq1 :" + q1 + "\nq2 : " + q2 + "\nZ t : " + z_t + "\nError et : " + Error_t);
		//System.out.println("Hyp : " + boostedClassifier.entrySet());
		//System.out.println("-------------------------------------------------------");
		
		
		//f.p = newProb;
		
		//for(int i = 0; i < f.n; i++)
			//System.out.println("p : " + f.p[i]);
		 
		writeToFile(f, f.outPath);
		
	}
	
	/*
	 * 
	 * Updating Probabilities
	 * 
	 * 
	 * */
	
	public void updateProbabiltity(File_info f, int index, float q1, float q2)
	{		
		String classifier = f.weakClassifier.get(index).toString();
		mistake = 0;
		//System.out.println("Class : " + classifier);
		
		String[] temp = classifier.split(" ");
		String sign = temp[0];
		float threshhold = Float.valueOf(temp[1]);
		//System.out.println(" Threshhold = " + threshhold + " sign : " + sign);
		
		int value;
		
		if(sign.equals("<"))
		{
			value = 1;
		}
		else
		{
			value = -1;
		}
		
		for(int j = 0; j < f.n; j++)
		{
			if(f.x[j] < threshhold)
			{
				if(f.y[j] != value)
				{
					preNormProb[j] = f.p[j] * q2;
					mistake++;
				}
				else
				{
					preNormProb[j] = f.p[j] * q1;
				}
			}
			else
			{
				if(f.y[j] != (value * -1))
	              {
					preNormProb[j] = f.p[j] * q2;
					mistake++;
	              }
				else
				{
					preNormProb[j] = f.p[j] * q1;
				}
			}				
		}		
	
	}
	
	/*
	 * 
	 * Constructor with more arguments
	 * 
	 * */
	
	public BinaryIterations(File_info f, BinaryIterations prev, int itr)
	{
		float min = 1;
		int index = 0;
		preNormProb = new float[f.n];
		newProb = new float[f.n];
		iteration = itr;		
		
		boostedClassifier = new HashMap<String, Float>();
		
		// System.out.println("prev : " + prev.boostedClassifier.values());
	
		
		for(int i = 0; i < f.weakClassifier.size() ; i++)
		{
			float error = 0;
					
			boostedClassifier.put(f.weakClassifier.get(i).toString(), (float) ( prev.boostedClassifier.get( f.weakClassifier.get(i).toString() )) ) ;
			
			String classifier = f.weakClassifier.get(i).toString();
						
			String[] temp = classifier.split(" ");
			String sign = temp[0];
			float threshhold = Float.valueOf(temp[1]);
			
			
			int value;
			
			if(sign.equals("<"))
			{
				value = 1;
			}
			else
			{
				value = -1;
			}
			
			for(int j = 0; j < f.n; j++)
			{
				if(f.x[j] < threshhold)
				{
					if(f.y[j] != value)
					{
						error = error + prev.newProb[j];
					}					
				}
				else
				{
					if(f.y[j] != (value * -1))
		              {
		                 error=error + prev.newProb[j]; 
		              }
				}				
			}
			
			if(min > error)
			{
				min = error;
				index = i;
			}			
		}
		
				
				e_t = min;
				h_t = index;
				alpha_t = (float) ( Math.log( (double) ( (1 - e_t) / e_t) ) );		
				alpha_t = alpha_t / 2;
				
				boostedClassifier.remove(f.weakClassifier.get(index).toString());
				boostedClassifier.put(f.weakClassifier.get(index).toString(), alpha_t);
				
				float q1 = (float) Math.exp((double) (-alpha_t) ); // For Correctly classified 
				
				float q2 = (float) Math.exp((double) (alpha_t) ); // For wrongly Classified 
				
				updateProbabiltity(f, prev, index, q1,q2);		
				
				//CalCulating Z_t
				z_t = 0;		
				for(int p = 0; p < preNormProb.length; p++)		
					z_t = z_t + preNormProb[p];		
				
				bound = z_t * prev.bound;
				
				
				for(int i = 0; i < newProb.length ; i++)
					newProb[i] = preNormProb[i] / z_t;			
				
				
				 mistake = 0;
				for(int i = 0; i < f.n; i++)
				{
					if(f.y[i] > 0)
					{
						if(calculateError(f, i) < 0)
						{
							mistake++;
						}
					}
					else // (y[i] < 0) 
					{
						if(calculateError(f, i) > 0)
						{
							mistake++;
						}
					}
				}
				Error_t =  ((float) mistake) / f.n;
				//System.out.println("Mistake Here : " + Error_t);
				//System.out.println("Iteration : " + iteration);
				//System.out.println("e_t : " + e_t + "\nht : " + h_t + "\naplha : " + alpha_t + "\nq1 :" + q1 + "\nq2 : " + q2 + "\nZ t : " + z_t + "\nMistakes : " + Error_t);
				
				//System.out.println("-------------------------------------------------------");
				
				 
				writeToFile(f, f.outPath);
		
		
	}
	
	public float calculateError(File_info f, int ind)
	{
		float value = 0;
		for(int i = 0; i < boostedClassifier.size() ; i++)
		{
			if( ( boostedClassifier.get(f.weakClassifier.get(i).toString()) ) != 0 )
			{
				String classifier = f.weakClassifier.get(i).toString();
								
				String[] temp = classifier.split(" ");
				String sign = temp[0];
				float threshhold = Float.valueOf(temp[1]);
								
				float cofficient = ( boostedClassifier.get(f.weakClassifier.get(i).toString()) );
				 
				int classify;
				
				if( sign.equals("<") )
				{
					if(f.x[ind] < threshhold)
						classify = 1;
					else
						classify = -1;
					
					value = value + (classify * cofficient);			
				}
				else // if(sign == ">")
				{
					if(f.x[ind] > threshhold)
						classify = 1;
					else
						classify = -1;
					value = value + (classify * cofficient);
				}		
				
			}
		 //System.out.println("Value here : " + value);	
		}
		return value;
	}	
	
	/*
	 * UpdateProbability for more iterations
	 * 
	 * 
	 * */
	
	
	public void updateProbabiltity(File_info f, BinaryIterations prev, int index, float q1, float q2)
	{		
		
		String classifier = f.weakClassifier.get(index).toString();
		
		String[] temp = classifier.split(" ");
		String sign = temp[0];
		float threshhold = Float.valueOf(temp[1]);
		int value;
		
		if(sign.equals("<"))
		{
			value = 1;
		}
		else
		{
			value = -1;
		}
		
		for(int j = 0; j < f.n; j++)
		{
			if(f.x[j] < threshhold)
			{
				if(f.y[j] != value)
				{
					preNormProb[j] = prev.newProb[j] * q2;
					//mistake++;
				}
				else
				{
					preNormProb[j] = prev.newProb[j] * q1;
				}
			}
			else
			{
				if(f.y[j] != (value * -1))
	              {
					preNormProb[j] = prev.newProb[j] * q2;
					//mistake++;
	              }
				else
				{
					preNormProb[j] = prev.newProb[j] * q1;
				}
			}				
		}
	}	
	
	/*
	 * 
	 * Writing to file
	 * 
	 * */
	
	public void writeToFile(File_info f, String path)
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
			
			writer.write("The selected weak classifier : H(x) = " + "x " + f.weakClassifier.get(h_t));
			writer.newLine();
			
			writer.write("The error of h(x) : " + e_t);
			writer.newLine();
			
			writer.write("The weight of h(x) : " + alpha_t);
			writer.newLine();
			
			writer.write("The probabilities normalization factor Zt : " + z_t);
			writer.newLine();
			
			writer.write("The probabilities after normalization :");			
			writer.newLine();
			for(int i = 0; i < newProb.length ; i++)
				writer.write(newProb[i] + " ");
			writer.newLine();
			
			writer.write("The boosted classifier :");
			writer.newLine();
			writer.write("f(x) = ");
			//System.out.println("Size : " + boostedClassifier.size());
			for(int i = 0; i < boostedClassifier.size(); i++)
			{
				if( ( boostedClassifier.get(f.weakClassifier.get(i).toString() ) ) != 0)
				{
				
					if(i > 0)
					{
						writer.write(" + ");
					}
					writer.write(  ( boostedClassifier.get(f.weakClassifier.get(i) ) ) + " I( " + "x " + f.weakClassifier.get(i) + ")");
				
				}
			}
			
			writer.newLine();
			
			writer.write("The error of the boosted classifier: " + Error_t);
			writer.newLine();
			
			writer.write("The bound on Et : " + bound);
			writer.newLine();
			writer.write("------------------------------------------------------------");
			writer.newLine();
			
			writer.close();
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
}
