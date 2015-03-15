package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.msadraii.multidex");

        Entity label = schema.addEntity("Label");
        label.addIdProperty().autoincrement().unique();
        label.addStringProperty("argb");
        label.addStringProperty("task");

        Entity entry = schema.addEntity("Entry");
        entry.addIdProperty().autoincrement().unique();
        entry.addDateProperty("date");
        entry.addStringProperty("segments");
        Property labelIdProperty = entry.addLongProperty("labelId").getProperty();
        entry.addToOne(label, labelIdProperty);

        new DaoGenerator().generateAll(schema, args[0]);

//        Entity day = schema.addEntity("Day");
//        day.addIdProperty().autoincrement().unique();
//        day.addDateProperty("Date");
//        day.addBooleanProperty("AMPM");
    }
}
