package logic;

import model.Unit;

public class Combination {
    private Unit unitA;
    private Unit unitB;

    public Combination(Unit unitA, Unit unitB) {
        this.unitA = unitA;
        this.unitB = unitB;
    }

    public combine() {
        if (compatible(unitA, unitB)!=null) {
            System.out.println();
            Unit combinedUnit = compatible(unitA, unitB);

        } else {
            // Handle incompatible units (e.g., throw an exception or return an error message)
        }
    }
}
