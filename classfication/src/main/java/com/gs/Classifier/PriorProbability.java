package com.gs.Classifier;



public class PriorProbability 
{
	private static TrainingDataManager tdm =TrainingDataManager.getInstance();


	public static float calculatePc(String c)
	{
		float ret = 0F;
		float Nc = tdm.getTrainingFileCountOfClassification(c);
		float N = tdm.getTrainingFileCount();
		ret = Nc / N;
		return ret;
	}
}