package client.Voter;

/**
 * Created by jorl17 on 15/05/15.
 */
public class InsulineServiceMealtimeInsulinDose extends InsulineService {
    private int carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity;

    public InsulineServiceMealtimeInsulinDose(String url, int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity) {
        super(url);
        this.carbohydrateAmount = carbohydrateAmount;
        this.carbohydrateToInsulinRatio = carbohydrateToInsulinRatio;
        this.preMealBloodSugar = preMealBloodSugar;
        this.targetBloodSugar = targetBloodSugar;
        this.personalSensitivity = personalSensitivity;
    }

    public InsulineServiceMealtimeInsulinDose(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity) {
        this(null, carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
    }

    @Override
    protected int doWork() {
        return service.mealtimeInsulinDose(carbohydrateAmount,carbohydrateToInsulinRatio,preMealBloodSugar,targetBloodSugar,personalSensitivity);
    }

    @Override
    protected InsulineService constructNew() {
        return new InsulineServiceMealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
    }
}
