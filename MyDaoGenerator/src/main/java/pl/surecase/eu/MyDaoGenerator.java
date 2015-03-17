package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.msadraii.multidex");

        Entity colorCode = schema.addEntity("ColorCode");
        colorCode.addIdProperty().autoincrement().unique();
        colorCode.addStringProperty("argb");
        colorCode.addStringProperty("task");

        Entity entry = schema.addEntity("Entry");
        entry.addIdProperty().autoincrement().unique();
        entry.addDateProperty("date");
        entry.addStringProperty("segments");
        Property colorCodeIdProperty = entry.addLongProperty("colorCodeId").getProperty();
        entry.addToOne(colorCode, colorCodeIdProperty);

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
