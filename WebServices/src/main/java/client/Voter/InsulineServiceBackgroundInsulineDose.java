package client.Voter;

/**
 * Created by jorl17 on 15/05/15.
 */
public class InsulineServiceBackgroundInsulineDose extends InsulineService {
    private int weight;

    public InsulineServiceBackgroundInsulineDose(String url, int weight) {
        super(url);
        this.weight = weight;
    }

    public InsulineServiceBackgroundInsulineDose(int weight) {
        this(null, weight);
    }


    @Override
    protected int doWork() {
        return service.backgroundInsulinDose(weight);
    }

    @Override
    protected InsulineService constructNew() {
        return new InsulineServiceBackgroundInsulineDose(weight);
    }
}
