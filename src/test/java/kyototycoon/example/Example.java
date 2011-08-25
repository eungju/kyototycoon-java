package kyototycoon.example;

import kyototycoon.Cursor;
import kyototycoon.KyotoTycoonConnection;
import kyototycoon.Record;
import kyototycoon.SimpleKyotoTycoonClient;

import java.net.URI;

public class Example {
    public static void main(String[] args) {
        SimpleKyotoTycoonClient db = null;
        try {
            // open the database
            db = new SimpleKyotoTycoonClient();
            db.setHost(URI.create("http://localhost:1978"));
            db.start();

            // store records
            db.set("foo", "hop");
            db.set("bar", "step");
            db.set("baz", "jump");

            // retrieve a record
            String value = (String) db.get("foo");
            System.out.println(value);

            // traverse records
            KyotoTycoonConnection conn = db.getConnection();
            Cursor cur = conn.cursor();
            try {
                cur.jump();
                Record record;
                while ((record = cur.get(true)) != null) {
                  System.out.println(record.key + ":" + record.value + "," + record.xt);
                }
            } finally {
                cur.close();
                conn.close();
            }
        } finally {
            // close the database
            if (db != null) {
                db.stop();
            }
        }
    }
}
