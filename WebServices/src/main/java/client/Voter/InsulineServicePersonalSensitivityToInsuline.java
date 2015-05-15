package client.Voter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jorl17 on 15/05/15.
 */
public class InsulineServicePersonalSensitivityToInsuline extends InsulineService {
    private int physicalActivityLevel;
    private int[] physicalActivitySamples;
    private int[] bloodSugarDropSamples;
    public InsulineServicePersonalSensitivityToInsuline(String url, int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples) {
        super(url);
        this.physicalActivityLevel = physicalActivityLevel;
        this.physicalActivitySamples = physicalActivitySamples;
        this.bloodSugarDropSamples = bloodSugarDropSamples;
    }

    public InsulineServicePersonalSensitivityToInsuline(int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples) {
        this(null, physicalActivityLevel, physicalActivitySamples, bloodSugarDropSamples);
    }



    @Override
    protected int doWork() {
        return service.personalSensitivityToInsulin(physicalActivityLevel, toList(physicalActivitySamples), toList(bloodSugarDropSamples));
    }

    private List<Integer> toList(int[] arr) {
        List<Integer> ret = new ArrayList<Integer>();
        for ( int i : arr )
            ret.add(i);
        return ret;
    }

    @Override
    protected InsulineService constructNew() {
        return new InsulineServicePersonalSensitivityToInsuline(physicalActivityLevel, physicalActivitySamples, bloodSugarDropSamples);
    }
}
