/**
 * Description of class ALifeCalcUtil:
 * This class is a utility class for ALifeCalc.
 * It contains some methods and functions to solve some mathematical and statistical usual problems.
 * 
 * TFG for Informatical degree in UNIR
 * 
 * @author Iñigo Marco Veintimillas 
 * @version 05-06-23
 */
public class ALifeCalcUtil {
    
    /**
     * public static double min_max_Normalization(double value, double min, double max)
     * 
     * This method normalizes a value between 0 and 1.
     * @ param value: value to normalize
     * @ param min: minimum value of the range
     * @ param max: maximum value of the range
     * @ return: normalized value
     */
    public static double min_max_Normalization(double value, double min, double max){
        try{
            return (value - min) / (max - min);
        }catch(Exception e){
            //error div by 0
            MultiTaskUtil.threadMsg("Error in min_max_Normalization (value: "+value+" min: "+min+" max: "+max+"))");
        }
        return 0;
    } // End of method min_max_Normalization

    /**
     * public static double[] min_max_Array_Normalization(double[] value, double min[], double max[])
     * his method normalizes a set of values between 0 and 1.
     * @ param value: set of values to normalize
     * @ param min: minimum value of the range
     * @ param max: maximum value of the range
     * @ return: normalized set of values
     */
    public static double[] min_max_Array_Normalization(double[] values, double min[], double max[]){
        double[] normalized_values = new double[values.length];
        for(int i = 0; i < values.length; i++){
            normalized_values[i] = min_max_Normalization(values[i], min[i], max[i]);
            //for test
            if (normalized_values[i] < 0 || normalized_values[i] > 1){
                MultiTaskUtil.threadMsg("Error in min_max_Array_Normalization");
                MultiTaskUtil.threadMsg("value: "+values[i]+" min: "+min[i]+" max: "+max[i]+" normalized: "+normalized_values[i]);
            }
            //end for test
        }  
        return normalized_values;
    } // End of method min_max_Array_Normalization

        /**
     * public static double[] min_max_Normalization(double[] value, double min, double max)
     * 
     * This method normalizes a set of values between 0 and 1.
     * @ param value: set of values to normalize
     * @ param min: minimum value of the range
     * @ param max: maximum value of the range
     * @ return: normalized set of values
     */
     public static double[] min_max_MultipleValuesNormalization(double[] values, double min, double max){
        double[] normalized_values = new double[values.length];
        for(int i = 0; i < values.length; i++){
            normalized_values[i] = min_max_Normalization(values[i], min, max);
        }
        return normalized_values;
    } // End of method min_max_Normalization


    /**
     * public static double z_score_Normalization(double value, double mean, double std_dev)
     * 
     * This method normalizes a value using the z-score method.
     * @ param value: value to normalize
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the rang
     */
    public static double z_score_Normalization(double value, double mean, double std_dev){
        return (value - mean) / std_dev;
    } // End of method z_score_Normalization

    /**
     * public static double[] z_score_Normalization(double[] value, double mean, double std_dev)
     * 
     * This method normalizes a set of values using the z-score method.
     * @ param value: set of values to normalize
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the rang
     */
     public static double[] z_score_Normalization(double[] values, double mean, double std_dev){
        double[] normalized_values = new double[values.length];
        for(int i = 0; i < values.length; i++){
            normalized_values[i] = z_score_Normalization(values[i], mean, std_dev);
        }
        return normalized_values;
    } // End of method z_score_Normalization
    /**
     * public static double hiperbolic_tangent_Normalization(double value, double mean, double std_dev)
     * 
     * This method normalizes a value using the hiperbolic tangent method.
     * @ param value: value to normalize
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the rang
     */
    public static double hiperbolic_tangent_Normalization(double value, double mean, double std_dev){
        return Math.tanh((value - mean) / std_dev);
    } // End of method hiperbolic_tangent_Normalization

    /**
     * public static double[] hiperbolic_tangent_Normalization(double[] value, double mean, double std_dev)
     * 
     * This method normalizes a set of values using the hiperbolic tangent method.
     * @ param value: set of values to normalize
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the rang
     */
    public static double[] hiperbolic_tangent_Normalization(double[] values, double mean, double std_dev){
        double[] normalized_values = new double[values.length];
        for(int i = 0; i < values.length; i++){
            normalized_values[i] = hiperbolic_tangent_Normalization(values[i], mean, std_dev);
        }
        return normalized_values;
    } // End of method hiperbolic_tangent_Normalization

    /**
     * public static double sigmoid_Normalization(double value, double mean, double std_dev)
     * 
     * This method normalizes a value using the sigmoid method.
     * @ param value: value to normalize
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the rang
     */
    public static double sigmoid_Normalization(double value, double mean, double std_dev){
        return 1 / (1 + Math.exp(-(value - mean) / std_dev));
    } // End of method sigmoid_Normalization

    /**
     * public static double[] sigmoid_Normalization(double[] value, double mean, double std_dev)
     * 
     * This method normalizes a set of values using the sigmoid method.
     * @ param value: set of values to normalize
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the rang
     */
    public static double[] sigmoid_Normalization(double[] values, double mean, double std_dev){
        double[] normalized_values = new double[values.length];
        for(int i = 0; i < values.length; i++){
            normalized_values[i] = sigmoid_Normalization(values[i], mean, std_dev);
        }
        return normalized_values;
    } // End of method sigmoid_Normalization 

    /**
     * public static double log_Normal_CDF(double value, double mean, double std_dev)
     * 
     * This method calculates the log of the normal cumulative distribution function.
     * @ param value: value to calculate the log of the normal cumulative distribution function
     * @ param mean: mean of the range
     * @ param std_dev: standard deviation of the range
     * @ return: log of the normal cumulative distribution function
     */
     /*
    public static double log_Normal_CDF(double value, double mean, double std_dev){
        return Math.log(0.5) + Math.log(1 + Math.erf((value - mean) / (std_dev * Math.sqrt(2)))) - Math.log(2);
    } // End of method log_Normal_CDF
    */

    /**
     * public static double mean(double[] values)
     * 
     * This method calculates the mean of a set of values.
     * @ param values: set of values
     * @ return: mean of the set of values
     */
    public static double mean(double[] values){
        double mean = 0;
        for(int i = 0; i < values.length; i++){
            mean += values[i];
        }
        return mean / values.length;
    } // End of method mean

    /**
     * public static double standard_deviation(double[] values)
     *  
     * This method calculates the standard deviation of a set of values.
     * @ param values: set of values
     * @ return: standard deviation of the set of values
     */
    public static double standard_deviation(double[] values){
        double mean = mean(values);
        double std_dev = 0;
        for(int i = 0; i < values.length; i++){
            std_dev += Math.pow(values[i] - mean, 2);
        }
        return Math.sqrt(std_dev / values.length);
    } // End of method standard_deviation

    /**
     * public static double min(double[] values)
     * 
     * This method calculates the minimum value of a set of values.
     * @ param values: set of values
     * @ return: minimum value of the set of values
     */
    public static double min(double[] values){
        double min = values[0];
        for(int i = 1; i < values.length; i++){
            if(values[i] < min){
                min = values[i];
            }
        }
        return min;
    } // End of method min

    /**
     * public static double max(double[] values)    
     * 
     * This method calculates the maximum value of a set of values.
     * @ param values: set of values
     * @ return: maximum value of the set of values
     */
    public static double max(double[] values){
        double max = values[0];
        for(int i = 1; i < values.length; i++){
            if(values[i] > max){
                max = values[i];
            }
        }
        return max;
    } // End of method max

    /**
     * public static doube[] ponderation_Array(double[] values, double[] ponderationArray)
     * 
     * This method calculates the ponderation of a set of values.
     * @ param values: set of values
     * @ param ponderationArray: ponderation array
     * @ return: ponderation of the set of values
     */
    public static double[] ponderation_Array(double[] values, double[] ponderationArray){
        //NOT tested yet
        double[] ponderation = new double[values.length];
        for(int i = 0; i < values.length; i++){
            ponderation[i] = values[i] * ponderationArray[i];
        }
        return ponderation;
    } // End of method ponderation_Array
    
    /**
     * public static double arrayDistance(double[] array1, double[] array2)
     * 
     * This method calculates the distance between two arrays.
     * @ param array1: first array
     * @ param array2: second array
     * @ return: distance between the two arrays
     */
    public static double arrayDistance(double[] array1, double[] array2){
        double distance = 0;
        for(int i = 0; i < array1.length; i++){
            
            //for test
            double a1 = array1[i];
            double a2 = array2[i];
            double a3 = Math.pow(array1[i] - array2[i], 2);
            //end for test

            distance += Math.pow(array1[i] - array2[i], 2);
        }
        return Math.sqrt(distance);
    } // End of method arrayDistance


    //Generate test data and examples
    public static void main(String[] args){
        double[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 45, 75, 100, 2456, 7654, 10000};
        System.out.println("Minimo:  " + min(values));
        System.out.println("Maximo:  " + max(values));
        System.out.println("Mean: " + mean(values));
        System.out.println("Standard deviation: " + standard_deviation(values));

        System.out.println("Normalizations : ");
        for(int i = 0; i < values.length; i++){
            System.out.print(values[i]+" Z: "+z_score_Normalization(values[i], mean(values), standard_deviation(values)) + " ");
            System.out.print("H: "+hiperbolic_tangent_Normalization(values[i], mean(values), standard_deviation(values)) + " ");
            System.out.print("mM: "+min_max_Normalization(values[i], min(values), max(values)) + " ");
            System.out.print("s: "+sigmoid_Normalization(values[i], mean(values), standard_deviation(values)) + " ");
            System.out.println();
        }

        //System.out.print("Log of the normal cumulative distribution function: ");
        //for(int i = 0; i < values.length; i++){
        //    System.out.print(log_Normal_CDF(values[i], mean(values), standard_deviation(values)) + " ");
        //}
        System.out.println();
        
        
    } // End of method main
} // End of class ALifeCalcUtil




