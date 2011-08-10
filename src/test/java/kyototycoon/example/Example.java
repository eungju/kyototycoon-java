package kyototycoon.example;

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
    //        Cursor cur = db.cursor();
    //        cur.jump();
    //        String ckey, cvalue;
    //        while (cur.get(&ckey, &cvalue, NULL, true)) {
    //          System.out.println(ckey + ":" + cvalue);
    //        }
    //        cur.close();
        } finally {
            // close the database
            if (db != null) {
                db.stop();
            }
        }
    }
}
