package model;

public class AttendanceRecord {
    private int theoryConducted;
    private int theoryAttended;
    private int labConducted;
    private int labAttended;

    public AttendanceRecord() {
    }

    public AttendanceRecord(int theoryConducted, int theoryAttended, int labConducted, int labAttended) {
        this.theoryConducted = theoryConducted;
        this.theoryAttended = theoryAttended;
        this.labConducted = labConducted;
        this.labAttended = labAttended;
    }

    public int getTheoryConducted() {
        return theoryConducted;
    }

    public void setTheoryConducted(int theoryConducted) {
        this.theoryConducted = theoryConducted;
    }

    public int getTheoryAttended() {
        return theoryAttended;
    }

    public void setTheoryAttended(int theoryAttended) {
        this.theoryAttended = theoryAttended;
    }

    public int getLabConducted() {
        return labConducted;
    }

    public void setLabConducted(int labConducted) {
        this.labConducted = labConducted;
    }

    public int getLabAttended() {
        return labAttended;
    }

    public void setLabAttended(int labAttended) {
        this.labAttended = labAttended;
    }

    // --- Calculation Helpers ---

    public double getTheoryPercent() {
        if (theoryConducted <= 0)
            return 0.0;
        return (theoryAttended * 100.0) / theoryConducted;
    }

    public double getLabPercent() {
        if (labConducted <= 0)
            return 0.0;
        return (labAttended * 100.0) / labConducted;
    }

    // Legacy helper for generic displays (weighted or simple average)
    public double getOverallPercent() {
        int total = theoryConducted + labConducted;
        int attended = theoryAttended + labAttended;
        if (total <= 0)
            return 0.0;
        return (attended * 100.0) / total;
    }
}
