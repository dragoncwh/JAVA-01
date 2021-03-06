package cwh.java.geektime.l13w7;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Util {
  private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

  private static final String DROP = "DROP TABLE IF EXISTS orders";
  private static final String CREATE = "CREATE TABLE `orders`\n"
      + "(\n"
      + "  `order_id` BIGINT NOT NULL AUTO_INCREMENT,\n"
      + "  `shipping_id` BIGINT DEFAULT NULL,\n"
      + "  `payment_id` BIGINT DEFAULT NULL,\n"
      + "  `user_id` INT(20) NOT NULL,\n"
      + "  `order_status` TINYINT NOT NULL DEFAULT '1',\n"
      + "  `create_time` TIMESTAMP NOT NULL,\n"
      + "  `update_time` TIMESTAMP NOT NULL,\n"
      + "  \n"
      + "  PRIMARY KEY (`order_id`)\n"
      + ") ENGINE = InnoDB DEFAULT CHARSET = 'utf8mb4';";

  // 原生jdbc
  public static void rawJDBCTest(DataSource dataSource, int size) throws Exception {
    Connection connection = null;
    try {
      Statement statement = connection.createStatement();
      statement.execute(DROP);
      statement.execute(CREATE);

      int shippingId = 0;
      int paymentId = 0;
      Random random = new Random();
      long start = System.currentTimeMillis();
      for (int i = 0; i < size; i++) {
        long cur = System.currentTimeMillis();
        // Timestamp curTimestamp = new Timestamp(cur);
        // String insert = String.format("INSERT INTO orders(shipping_id, payment_id, user_id, order_status, create_time, update_time) VALUES ('%d', '%d', '%d', '%d', '%s', '%s')",
        // 		++shippingId, ++paymentId, random.nextInt(), random.nextInt(7), curTimestamp, curTimestamp);
        String insert = String.format("INSERT INTO orders(shipping_id, payment_id, user_id, order_status, create_time, update_time) VALUES ('%d', '%d', '%d', '%d', now(), now())",
            ++shippingId, ++paymentId, random.nextInt(), random.nextInt(7));
        statement.execute(insert);
      }
      System.out.println("原生JDBC插入" + size + "条数据所用时间: "
          + (System.currentTimeMillis() - start));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  // PreparedStatement JDBC
  public static void preparedStatementTest(DataSource dataSource, int size) throws Exception {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      statement.execute(DROP);
      statement.execute(CREATE);
      int shippingId = 0;
      int paymentId = 0;
      long start = System.currentTimeMillis();
      Random random = new Random();
      PreparedStatement insertStmt =
          dataSource.getConnection().prepareStatement("INSERT INTO orders(shipping_id, payment_id, user_id, order_status, create_time, update_time) VALUES (?, ?, ?, ?, now(), now())");
      for (int i = 0; i < size; i++) {
        long cur = System.currentTimeMillis();
        // Timestamp curTimestamp = new Timestamp(cur);
        insertStmt.setLong(1, ++shippingId);
        insertStmt.setLong(2, ++paymentId);
        insertStmt.setInt(3, random.nextInt());
        insertStmt.setInt(4, random.nextInt(7));
        // insertStmt.setTimestamp(5, curTimestamp);
        // insertStmt.setTimestamp(6, curTimestamp);
        insertStmt.execute();
      }
      System.out.println("JDBC(PreparedStatement方式)插入" + size + "条数据所用时间: "
          + (System.currentTimeMillis() - start));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.close();
      }
    }

  }

  // PreparedStatement + batch
  public static void PreparedStatementAndBatchTest(DataSource dataSource,
      int size, int batchSize) throws Exception {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      statement.execute(DROP);
      statement.execute(CREATE);
      int shippingId = 0;
      int paymentId = 0;
      int count = 0;
      long start = System.currentTimeMillis();
      Random random = new Random();
      PreparedStatement insertStmt =
          dataSource.getConnection().prepareStatement("INSERT INTO orders(shipping_id, payment_id, user_id, order_status, create_time, update_time) VALUES (?, ?, ?, ?, now(), now())");
      for (int i = 0; i < size; i++) {
        count++;
        // long cur = System.currentTimeMillis();
        // Timestamp curTimestamp = new Timestamp(cur);
        insertStmt.setLong(1, ++shippingId);
        insertStmt.setLong(2, ++paymentId);
        insertStmt.setInt(3, random.nextInt());
        insertStmt.setInt(4, random.nextInt(7));
        // insertStmt.setTimestamp(5, curTimestamp);
        // insertStmt.setTimestamp(6, curTimestamp);
        insertStmt.addBatch();
        if (count == batchSize) {
          insertStmt.executeBatch();
          count = 0;
        }
      }
      if (count != 0) {
        insertStmt.executeBatch();
      }
      System.out.println("JDBC(PreparedStatement + batch)插入" + size + "条数据所用时间: "
          + (System.currentTimeMillis() - start));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  public static void concurrentPreparedStatementAndBatchTest(DataSource ds,
      int size, int batchSize) throws Exception {
    Connection connection = null;
    try {
      connection = ds.getConnection();
      Statement statement = connection.createStatement();
      statement.execute(DROP);
      statement.execute(CREATE);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.close();
      }
    }

    int numOfBatches = Math.max(0, (size - 1) / batchSize + 1);

    long start = System.currentTimeMillis();
    // List<Future<?>> futures = new ArrayList<>();

    List<CompletableFuture<?>> futures =  IntStream.range(0, numOfBatches).parallel()
        .mapToObj(idx -> {
          return CompletableFuture.runAsync(() -> {
            Connection conn = null;
            try {
              conn = ds.getConnection();
              PreparedStatement insertStmt =
                  conn.prepareStatement(
                      "INSERT INTO orders(shipping_id, payment_id, user_id, order_status, create_time, update_time) VALUES (?, ?, ?, ?, now(), now())");
              int begin = idx * batchSize;
              int end = Math.min(size, (idx + 1) * batchSize);
              int shippingId = begin;
              int paymentId = begin;
              Random random = new Random();
              for (int i = begin; i < end; i++) {
                insertStmt.setLong(1, ++shippingId);
                insertStmt.setLong(2, ++paymentId);
                insertStmt.setInt(3, random.nextInt());
                insertStmt.setInt(4, random.nextInt(7));
                // insertStmt.setTimestamp(5, curTimestamp);
                // insertStmt.setTimestamp(6, curTimestamp);
                insertStmt.addBatch();
              }
              insertStmt.executeBatch();

            } catch (Exception e) {
              e.printStackTrace();
              throw new RuntimeException(e);
            } finally {
              try {
                if (conn != null) {
                  conn.close();
                }
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }, executor);
        })
        .collect(Collectors.toList());

    CompletableFuture<Void> allFutures
        = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));

    allFutures.thenAccept(fut -> {
      System.out.println("JDBC(Concurrent + PreparedStatement + batch)插入" + size + "条数据所用时间: "
          + (System.currentTimeMillis() - start));
    });

    allFutures.get();
  }
}
