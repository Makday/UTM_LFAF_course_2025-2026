package lfaf.university.labs2026;

public class Production {
    private final String lhs;
    private final String rhs;

    public Production(String lhs, String rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String getLhs() { return lhs; }
    public String getRhs() { return rhs; }
}
