package cwh.java.geektime.l15w8;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringJoiner;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class L15W8Application implements CommandLineRunner {

	@Resource
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(L15W8Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();

			int shippingId = 0;
			int paymentId = 0;
			int userCount = 10;
			int orderCountPerUser = 10;
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
