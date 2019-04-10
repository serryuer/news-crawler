package com.gs.Classifier;


public class ClassConditionalProbability 
{
    private static TrainingDataManager tdm = TrainingDataManager.getInstance();
    private static final float M = 0F;

    public static float calculatePxc(String x, String c) 
    {
        float ret = 0F;
        float Nxc = tdm.getCountContainKeyOfClassification(c, x);
        float Nc = tdm.getTrainingFileCountOfClassification(c);
        float V = tdm.getTraningClassifications().length;
        ret = (Nxc + 1) / (Nc + M + V);
        return ret;
    }
}