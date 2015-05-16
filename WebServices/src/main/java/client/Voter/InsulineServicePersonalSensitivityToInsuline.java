package client.Voter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jorl17 on 15/05/15.
 */
public class InsulineServicePersonalSensitivityToInsuline extends InsulineService {
    private int carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar;
    private int physicalActivityLevel;
    private int[] physicalActivitySamples;
    private int[] bloodSugarDropSamples;

    public InsulineServicePersonalSensitivityToInsuline(String url, int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples) {
        super(url);
        this.carbohydrateAmount = carbohydrateAmount;
        this.carbohydrateToInsulinRatio = carbohydrateToInsulinRatio;
        this.preMealBloodSugar = preMealBloodSugar;
        this.targetBloodSugar = targetBloodSugar;
        this.physicalActivityLevel = physicalActivityLevel;
        this.physicalActivitySamples = physicalActivitySamples;
        this.bloodSugarDropSamples = bloodSugarDropSamples;
    }

    public InsulineServicePersonalSensitivityToInsuline(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples) {
        this(null,carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar,physicalActivityLevel,physicalActivitySamples,bloodSugarDropSamples);
    }

    @Override
    protected int doWork() {
        return service.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar,
                service.personalSensitivityToInsulin(physicalActivityLevel, toList(physicalActivitySamples), toList(bloodSugarDropSamples)));
    }

    private List<Integer> toList(int[] arr) {
        List<Integer> ret = new ArrayList<Integer>();
        for ( int i : arr )
            ret.add(i);
        return ret;
    }

    @Override
    protected InsulineService constructNew() {
        return new InsulineServicePersonalSensitivityToInsuline(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar,physicalActivityLevel,physicalActivitySamples,bloodSugarDropSamples);
    }
}
