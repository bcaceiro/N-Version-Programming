package server;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;


@WebService()
public class InsulinDoseCalculator{
    public static void main(String[] argv) {
        Object implementor = new InsulinDoseCalculator();
        String address = "http://localhost:9000/InsulinDoseCalculator";
        Endpoint.publish(address, implementor);
    }

    /**
     * Calculates the number of insulin units needed after one meal.
     * <p>
     * This method takes the amount of carbohydrate in a given meal, and returns
     * the number of units of insulin needed after that meal. The returned
     * number of units of insulin equals the carbohydrate dose plus the high
     * blood sugar dose, which are computed as follows.
     * <p>
     * The carbohydrate dose equals the total grams of carbohydrates in the meal
     * divided by the amount of carbohydrate disposed by one unit of insulin,
     * corrected by taking into account the personal sensitivity to insulin.
     * This dose equals <code>carbohydrateAmount / carbohydrateToInsulinRatio /
     * personalSensitivity x 50</code>.
     * <p>
     * The high blood sugar dose equals the difference between the pre-meal
     * blood sugar level and the target blood sugar level, divided by the
     * personal sensitivity to insulin. This equals <code>(preMealBloodSugar -
     * targetBloodSugar) / personalSensitivity</code>. The personal sensitivity
     * may be estimated using <code>personalSensitivityToInsulin()</code>.
     * <p>
     * In the special case when the target blood sugar level is greater than the
     * pre-meal blood sugar level, the return value of this method is zero (no
     * insulin).
     *
     * @param carbohydrateAmount amount of carbohydrate in the meal, in grams
     * @param carbohydrateToInsulinRatio carbohydrate grams disposed by one unit
     * @param preMealBloodSugar pre-meal measured blood sugar level, in mg/dl
     * @param targetBloodSugar prescribed target blood sugar level, in mg/dl
     * @param personalSensitivity personal sensitivity to insulin
     * @return the mealtime units of insulin needed, or -1 in case of error
     */

    @WebMethod
    public int mealtimeInsulinDose(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity)
    {
        //Input verification, all values have been adjusted to -+ 25% of the initial range
        if(carbohydrateAmount < 45 || carbohydrateAmount > 135) {
            System.out.println("Carbohydrate Amount is off the expected values.");
            return -1;
        }
        if(carbohydrateToInsulinRatio < 9 || carbohydrateToInsulinRatio > 16){
            System.out.println("Carbohydrate to insulin ratio is off the expected values.");
            return -1;
        }
        if(preMealBloodSugar < 88 || preMealBloodSugar > 282){
            System.out.println("Pre meal blood sugar is off the expected values.");
            return -1;
        }
        if(targetBloodSugar < 70 || targetBloodSugar > 130){
            System.out.println("Target blood sugar is off the expected values.");
            return -1;
        }
        if(personalSensitivity < 0 || personalSensitivity > 119){
            System.out.println("Personal sensitivity is off the expected values.");
            return -1;
        }

        //Special case, target blood sugar level is higher than the pre-meal blood sugar level
        if(preMealBloodSugar<targetBloodSugar)
            return 0;

        //Converting to float
        double preMealBloodSugarF = preMealBloodSugar;
        double carbohydrateAmountF = carbohydrateAmount;
        //Normal insulin dose calculation
        double highBloodSugarDose = (preMealBloodSugarF-targetBloodSugar)/personalSensitivity;
        double carbohydrateDose = carbohydrateAmountF/carbohydrateToInsulinRatio/personalSensitivity*50;

        return (int) Math.round(highBloodSugarDose + carbohydrateDose);
    }

    /**
     * Calculates the total number of units of insulin needed between meals.
     * <p>
     * The total insulin units required in one day equals <code>0.55 x body
     * weight</code> in kilograms. This method returns 50% of that number, since
     * the background need for insulin, between meals, is around half of the
     * daily total.
     *
     * @param bodyWeight the person's weight in kilograms
     * @return the background units of insulin needed, or -1 in case of error
     */

    @WebMethod
    public int backgroundInsulinDose(int bodyWeight)
    {
        //Input verification
        if(bodyWeight < 18 || bodyWeight > 152)
            return -1;

        //Converting to float
        double bodyWeightF = bodyWeight * 0.55 * 0.5;
        //Insulin dose calculation
        return (int) Math.round(bodyWeightF);
    }

    /**
     * Determines an individual's sensitivity to one unit of insulin.
     * <p>
     * One unit of insulin typically drops blood sugar by 50 mg/dl, but this
     * value depends on each individual's sensitivity and daily physical
     * activity. This method predicts the blood sugar drop (in mg/dl) that will
     * result from one unit of insulin, for a given physical activity level.
     * <p>
     * To predict the blood sugar drop, this method accepts two arrays with
     * K samples of (physical activity level, blood sugar drop). The two arrays
     * must therefore have the same length. First, a simple linear regression
     * (least squares method) is performed to compute alpha and beta. Then, the
     * return value is <code>alpha + beta x physicalActivityLevel</code>.
     * <p>
     * The physical activity levels, including the ones in the array of samples,
     * and the blood sugar drop values are non-negative integers. The return
     * value of this method may be passed to <code>mealtimeInsulinDose()</code>
     * as a parameter.
     *
     * @param physicalActivityLevel most recent activity level (the predictor)
     * @param physicalActivitySamples K samples of past physical activity
     * @param bloodSugarDropSamples corresponding K samples of blood sugar drop
     * @return the blood sugar drop in mg/dl, or -1 in case of error
     */
    @WebMethod
    public int personalSensitivityToInsulin(int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples)
    {
        //Input verification
        if(checkArrayValues(physicalActivitySamples, bloodSugarDropSamples) == -1)
            return -1;
        if(physicalActivityLevel < 0 || physicalActivityLevel > 10)
            return -1;

        //Calculate linear regression
        double[] result = linearRegression(physicalActivitySamples, bloodSugarDropSamples);
        //Calculate personal sensitivity
        return (int) Math.round(result[1] + result[0] * physicalActivityLevel);
    }

    //Checks inputs on both arrays
    private int checkArrayValues(int [] physicalActivitySamples, int[] bloodSugarDropSamples)
    {
        int i, max1 = physicalActivitySamples.length, max2 = bloodSugarDropSamples.length;

        if(max1 < 2 || max1 > 10)
            return -1;
        if(max2 < 2 || max2 > 10)
            return -1;
        if(max1 != max2)
            return -1;

        for(i = 0; i < max1; i++){
            if(physicalActivitySamples[i] < 0 || physicalActivitySamples[i] > 10)
                return -1;
            if(bloodSugarDropSamples[i] < 15 || bloodSugarDropSamples[i] > 100)
                return -1;
        }
        return 0;
    }


    //Performs a linear regression
    private double[] linearRegression(int[] physicalActivitySamples, int[] bloodSugarDropSamples)
    {
        //http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html
        //alpha = beta1
        double[] result = new double[2];
        int n = physicalActivitySamples.length, i;
        double Sx = 0, Sy = 0, Sxx = 0, Syy = 0, Sxy = 0;

        for(i = 0; i < n; i++)
        {
            Sx = Sx + physicalActivitySamples[i];
            Sy = Sy + bloodSugarDropSamples[i];
        }

        double xbar = Sx / n;
        double ybar = Sy / n;

        for(i = 0; i < n; i++)
        {
            Sxx = Sxx + (physicalActivitySamples[i] - xbar) * (physicalActivitySamples[i] - xbar);
            Syy = Syy + (bloodSugarDropSamples[i] - ybar) * (bloodSugarDropSamples[i] - ybar);
            Sxy = Sxy + (physicalActivitySamples[i] - xbar) * (bloodSugarDropSamples[i] - ybar);
        }

        double alpha = Sxy / Sxx;
        double beta = ybar - alpha * xbar;

        result[0] = alpha;
        result[1] = beta;

        return result;
    }
}
