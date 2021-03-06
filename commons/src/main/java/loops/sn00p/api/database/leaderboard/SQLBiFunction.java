package loops.sn00p.api.database.leaderboard;

import java.sql.SQLException;

public interface SQLBiFunction<A, B, C> {

    C apply(A a, B b) throws SQLException;

}
