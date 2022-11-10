import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evenkafka", "root", "");
        } catch (Exception e) {
            System.out.println((e));
        }
        KafkaConsumer consumer;
        String topic = "naturalnumber";
        String broker = "localhost:9092";
        Properties props = new Properties();
        props.put("bootstrap.servers", broker);
        props.put("group.id", "test.group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
                int number = Integer.parseInt(record.value());
                int reverse = 0;
                while (number > 0) {
                    int digit = number % 10;
                    reverse = reverse * 10 + digit;
                    number = number / 10;
                }
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evenkafka", "root", "");
                    String sql = "INSERT INTO `reverse`(`reverse`) VALUES (?)";
                    PreparedStatement stmt = con.prepareStatement((sql));
                    stmt.setInt(1, reverse);
                } catch (Exception e) {
                    System.out.println((e));
                }
            }
        }
    }
}


