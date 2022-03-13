package loops.sn00p.api.database.leaderboard;

public enum Order {

    ASCENDING("ASC"),
    DESCENDING("DESC"),

    ;

    private final String sqlText;

    Order(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getSqlText(String column) {
        return " ORDER BY " + column +  " " + this.sqlText;
    }
}
