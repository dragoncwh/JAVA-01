package cwh.java.geektime.shardingspherexa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.StringJoiner;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class ShardingsphereXaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ShardingsphereXaApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Resource
	private DataSource dataSource;

	public void run(String... args) throws Exception {
		test();
		// test1();
	}

	public void test1() throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();

			connection.setAutoCommit(false);

			int shippingId = 0;
			int paymentId = 0;
			int userCount = 5;
			int orderCountPerUser = 2;
			Random random = new Random();

			// 插入数据
			PreparedStatement insertStmt =
					dataSource.getConnection().prepareStatement("INSERT INTO t_order (shipping_id, payment_id, user_id, order_status, create_time, update_time) VALUES (?, ?, ?, ?, now(), now())");
			for (int i = 0; i < userCount; i++) {
				for (int j = 0; j < orderCountPerUser; j++) {
					insertStmt.setLong(1, ++shippingId);
					insertStmt.setLong(2, ++paymentId);
					insertStmt.setInt(3, i);
					insertStmt.setInt(4, random.nextInt(7));
					insertStmt.addBatch();
				}
				insertStmt.executeBatch();
			}

			// 查询
			PreparedStatement selectStmt = connection.prepareStatement("SELECT id, user_id, order_status FROM t_order where user_id = ?  order by id");
			selectStmt.setInt(1, 3);
			ResultSet resultSet = selectStmt.executeQuery();
			while (resultSet.next()) {
				StringJoiner sj = new StringJoiner(", ");
				sj.add("order_id: " + String.valueOf(resultSet.getLong("id")));
				sj.add("user_id: " + String.valueOf(resultSet.getLong("user_id")));
				sj.add("order_status: " + String.valueOf(resultSet.getInt("order_status")));
				System.out.println("Order [" + sj.toString() + "]");
			}

			// update
			int id2Update = 3;
			int newStatus = 3;
			PreparedStatement updateStmt = connection.prepareStatement(
					"UPDATE t_order set order_status = (?) WHERE user_id = (?)");
			updateStmt.setInt(1, newStatus);
			updateStmt.setInt(2, id2Update);
			int updatedCount = updateStmt.executeUpdate();
			if (updatedCount == 0) {
				System.out.println("Failed to update order record with id " + id2Update);
			}


			// delete
			int id2Delete = 2;
			PreparedStatement deleteStmt = connection.prepareStatement(
					"DELETE FROM t_order WHERE user_id = (?)");
			deleteStmt.setInt(1, id2Delete);
			int deleteCount = deleteStmt.executeUpdate();
			if (deleteCount == 0) {
				System.out.println("Failed to update student record with id : " + id2Delete);
			}

			connection.commit();
		} catch (Exception e) {
			if (connection != null) {
				connection.rollback();
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Transactional
	@ShardingTransactionType(TransactionType.XA)
	public void test() throws Exception {
		int shippingId = 0;
		int paymentId = 0;
		int userCount = 5;
		int orderCountPerUser = 2;
		Random random = new Random();

		// insert
		for (int i = 0; i < userCount; i++) {
			for (int j = 0; j < orderCountPerUser; j++) {
				jdbcTemplate.update("INSERT INTO t_order "
						+ "(shipping_id, payment_id, user_id, order_status, create_time, update_time) "
						+ "VALUES (?, ?, ?, ?, now(), now())",
						++shippingId, ++paymentId, i, random.nextInt(7));
			}
		}

		// query
		jdbcTemplate.query(
				"SELECT id, user_id, order_status FROM t_order where user_id = ?  order by id",
				(ps) -> ps.setInt(1, 3),
				(rs) -> {
					while (rs.next()) {
						StringJoiner sj = new StringJoiner(", ");
						sj.add("order_id: " + String.valueOf(rs.getLong("id")));
						sj.add("user_id: " + String.valueOf(rs.getLong("user_id")));
						sj.add("order_status: " + String.valueOf(rs.getInt("order_status")));
						System.out.println("Order [" + sj.toString() + "]");
					}
				});

		// update
		final int id2Update = 3;
		final int newStatus = 3;
		int updatedCount = jdbcTemplate.update(
				"UPDATE t_order set order_status = (?) WHERE user_id = (?)",
				(ps) -> {
					ps.setInt(1, newStatus);
					ps.setInt(2, id2Update);
				});
		if (updatedCount == 0) {
			System.out.println("Failed to update order record with id " + id2Update);
		}

		// delete
		final int id2Delete = 2;
		int deleteCount = jdbcTemplate.update(
				"DELETE FROM t_order WHERE user_id = (?)",
				(ps) -> {
					ps.setInt(1, id2Delete);
				}
		);
		if (deleteCount == 0) {
			System.out.println("Failed to update student record with id : " + id2Delete);
		}
	}
}
